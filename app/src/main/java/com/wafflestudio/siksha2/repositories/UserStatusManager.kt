package com.wafflestudio.siksha2.repositories

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.kakao.sdk.user.UserApiClient
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.network.OAuthProvider
import com.wafflestudio.siksha2.network.SikshaApi
import com.wafflestudio.siksha2.preferences.SikshaPrefObjects
import com.wafflestudio.siksha2.utils.showToast
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

    suspend fun deleteUser() {
        sikshaApi.deleteAccount()
        clearUserToken()
    }

    fun logoutUser(context: Context) {
        when(sikshaPrefObjects.oAuthProvider.getValue()) {
            OAuthProvider.KAKAO -> {
                UserApiClient.instance.logout { error ->
                    error?.let {
                        Timber.d(it)
                        context.showToast("로그아웃 실패!")
                        return@logout
                    }
                    clearUserToken()
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
                    if (it.isCanceled)
                        context.showToast("로그아웃 실패!")
                    else
                        clearUserToken()
                }
            }
        }
    }

    private fun clearUserToken() {
        sikshaPrefObjects.accessToken.setValue("")
    }

    // TODO: 필요한가...? 헷갈려서 일단 만듦.
    private fun attachBearerPrefix(token: String): String =
        if (token.startsWith("Bearer ")) token
        else "Bearer $token"
}
