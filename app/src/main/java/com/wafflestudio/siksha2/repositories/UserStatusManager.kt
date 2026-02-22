package com.wafflestudio.siksha2.repositories

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.user.UserApiClient
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.models.User
import com.wafflestudio.siksha2.models.Version
import com.wafflestudio.siksha2.models.toUser
import com.wafflestudio.siksha2.models.toVersion
import com.wafflestudio.siksha2.network.OAuthProvider
import com.wafflestudio.siksha2.network.SikshaApi
import com.wafflestudio.siksha2.network.dto.GetVersionResult
import com.wafflestudio.siksha2.network.dto.LoginOAuthResult
import com.wafflestudio.siksha2.network.dto.VocParam
import com.wafflestudio.siksha2.network.dto.core.UserDto
import com.wafflestudio.siksha2.network.result.NetworkResult
import com.wafflestudio.siksha2.preferences.SikshaPrefObjects
import com.wafflestudio.siksha2.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserStatusManager @Inject constructor(
    private val sikshaApi: SikshaApi,
    private val sikshaPrefObjects: SikshaPrefObjects
) {
    suspend fun loginWithOAuthToken(provider: OAuthProvider, token: String): NetworkResult<LoginOAuthResult> {
        val tokenWithPrefix = attachBearerPrefix(token)
        val response = when (provider) {
            OAuthProvider.GOOGLE -> sikshaApi.loginGoogle(tokenWithPrefix)
            OAuthProvider.KAKAO -> sikshaApi.loginKakao(tokenWithPrefix)
        }
        when (response) {
            is NetworkResult.Success -> {
                val accessToken = response.body.accessToken
                sikshaPrefObjects.oAuthProvider.setValue(provider)
                sikshaPrefObjects.accessToken.setValue(attachBearerPrefix(accessToken))

                syncFcmTokenIfNeeded(accessToken)
            }
            else -> { }
        }
        return response
    }

    suspend fun refreshUserToken(): Boolean {
        sikshaPrefObjects.accessToken.getValue().let {
            when (val response = sikshaApi.refreshToken(it)) {
                is NetworkResult.Success -> {
                    val accessToken = response.body.accessToken
                    sikshaPrefObjects.accessToken.setValue(attachBearerPrefix(accessToken))
                    return true
                }
                // 로그인 실패시 do nothing -> 다시 로그인 시나리오 타게 냅두기
                else -> return false
            }
        }
    }

    suspend fun deleteUser(context: Context, withdrawCallback: () -> Unit?) {
        sikshaApi.deleteAccount()

        when (sikshaPrefObjects.oAuthProvider.getValue()) {
            OAuthProvider.KAKAO -> {
                UserApiClient.instance.unlink { error ->
                    if (error != null) {
                        Timber.d(error)
                        context.showToast(context.getString(R.string.signout_failed))
                    } else {
                        clearUserToken()
                        withdrawCallback.invoke()
                    }
                }
            }
            OAuthProvider.GOOGLE -> {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(Scope(Scopes.EMAIL))
                    .requestIdToken(context.getString(R.string.google_server_client_id))
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                googleSignInClient.revokeAccess().addOnCompleteListener {
                    if (it.isCanceled) {
                        context.showToast(context.getString(R.string.signout_failed))
                    } else {
                        clearUserToken()
                        withdrawCallback.invoke()
                    }
                }
            }
        }
    }

    suspend fun sendVoc(voc: String, platform: String): NetworkResult<Unit> {
        val vocParam = VocParam(voc = voc, platform = platform)
        return sikshaApi.sendVoc(vocParam)
    }

    suspend fun getUserData(): NetworkResult<User> {
        return sikshaApi.getUserData().map(UserDto::toUser)
    }

    suspend fun updateUserProfile(nickname: String?, changeToDefaultImage: Boolean, image: MultipartBody.Part?): NetworkResult<User> {
        val nicknameBody = nickname?.let { MultipartBody.Part.createFormData("nickname", it) }
        return sikshaApi.updateUserData(image, changeToDefaultImage, nicknameBody).map(UserDto::toUser)
    }

    suspend fun checkNickname(nickname: String): NetworkResult<Unit> {
        return sikshaApi.checkNickname(nickname)
    }

    suspend fun getVersion(): NetworkResult<Version> {
        return sikshaApi.getVersion().map(GetVersionResult::toVersion)
    }

    // TODO: applicationContext 주입받아서 사용 (but google login 에서 activity 필요...)
    fun logoutUser(context: Context, logoutCallback: () -> Unit?) {
        when (sikshaPrefObjects.oAuthProvider.getValue()) {
            OAuthProvider.KAKAO -> {
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Timber.d(error)
                    }

                    clearUserToken()
                    logoutCallback.invoke()
                }
            }
            OAuthProvider.GOOGLE -> {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(Scope(Scopes.EMAIL))
                    .requestIdToken(context.getString(R.string.google_server_client_id))
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                googleSignInClient.signOut().addOnCompleteListener {
                    if (it.isCanceled) {
                        context.showToast(context.getString(R.string.logout_failed))
                    } else {
                        clearUserToken()
                        logoutCallback.invoke()
                    }
                }
            }
        }
    }

    private fun clearUserToken() {
        sikshaPrefObjects.accessToken.setValue("")
    }

    // TODO: 필요한가...? 헷갈려서 일단 만듦.
    private fun attachBearerPrefix(token: String): String =
        if (token.startsWith("Bearer ")) {
            token
        } else {
            "Bearer $token"
        }

    suspend fun syncFcmTokenIfNeeded(accessToken: String? = null) {
        val bearer = accessToken ?: sikshaPrefObjects.accessToken.getValue()
        if (bearer.isBlank()) return

        // 토큰 불러오기
        val currentToken = try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e("UserStatusManager", "FCM token fetch failed", e)
            return
        }

        // prefs에 현재 토큰 저장 (기존 onNewToken으로도 저장되지만 안전하게 동기화)
        sikshaPrefObjects.fcmToken.setValue(currentToken)

        // 서버 등록 여부 판단
        val lastRegistered = sikshaPrefObjects.lastRegisteredFcmToken.getValue()
        val needsRegister = lastRegistered.isBlank() || lastRegistered != currentToken
        if (!needsRegister) return

        // 서버 등록
        try {
            withContext(Dispatchers.IO) {
                sikshaApi.registerUserDevice(
                    mapOf("fcm_token" to currentToken),
                    attachBearerPrefix(bearer)
                )
            }
            sikshaPrefObjects.lastRegisteredFcmToken.setValue(currentToken)
            Timber.d("FCM token registered to server: $currentToken")
        } catch (e: Exception) {
            Log.e("UserStatusManager", "FCM registration exception", e)
        }
    }
}
