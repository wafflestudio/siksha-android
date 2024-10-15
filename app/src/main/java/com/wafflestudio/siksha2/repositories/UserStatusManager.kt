package com.wafflestudio.siksha2.repositories

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.kakao.sdk.user.UserApiClient
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.models.User
import com.wafflestudio.siksha2.models.Version
import com.wafflestudio.siksha2.models.toUser
import com.wafflestudio.siksha2.models.toVersion
import com.wafflestudio.siksha2.network.OAuthProvider
import com.wafflestudio.siksha2.network.SikshaApi
import com.wafflestudio.siksha2.network.dto.VocParam
import com.wafflestudio.siksha2.preferences.SikshaPrefObjects
import com.wafflestudio.siksha2.utils.showToast
import okhttp3.MultipartBody
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserStatusManager @Inject constructor(
    private val sikshaApi: SikshaApi,
    private val sikshaPrefObjects: SikshaPrefObjects
) {
    suspend fun loginWithOAuthToken(provider: OAuthProvider, token: String) {
        val tokenWithPrefix = attachBearerPrefix(token)
        val (accessToken) = when (provider) {
            OAuthProvider.GOOGLE -> sikshaApi.loginGoogle(tokenWithPrefix)
            OAuthProvider.KAKAO -> sikshaApi.loginKakao(tokenWithPrefix)
        }
        sikshaPrefObjects.oAuthProvider.setValue(provider)
        sikshaPrefObjects.accessToken.setValue(attachBearerPrefix(accessToken))
    }

    suspend fun refreshUserToken() {
        sikshaPrefObjects.accessToken.getValue().let {
            val (accessToken) = sikshaApi.refreshToken(it)
            sikshaPrefObjects.accessToken.setValue(attachBearerPrefix(accessToken))
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

    suspend fun sendVoc(voc: String, platform: String) {
        val vocParam = VocParam(voc = voc, platform = platform)
        sikshaApi.sendVoc(vocParam)
    }

    suspend fun getUserData(): User {
        return sikshaApi.getUserData().toUser()
    }

    suspend fun updateUserProfile(nickname: String?, changeToDefaultImage: Boolean, image: MultipartBody.Part?): User {
        val nicknameBody = nickname?.let { MultipartBody.Part.createFormData("nickname", it) }
        return sikshaApi.updateUserData(image, changeToDefaultImage, nicknameBody).toUser()
    }

    suspend fun checkNickname(nickname: String) {
        sikshaApi.checkNickname(nickname)
    }

    suspend fun getVersion(): Version {
        return sikshaApi.getVersion().toVersion()
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
}
