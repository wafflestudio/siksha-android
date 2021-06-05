package com.wafflestudio.siksha2.repositories

import com.wafflestudio.siksha2.network.OAuthProvider
import com.wafflestudio.siksha2.network.SikshaApi
import com.wafflestudio.siksha2.network.dto.VocParam
import com.wafflestudio.siksha2.preferences.SikshaPrefObjects
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

    suspend fun sendVoc(voc: String) {
        sikshaApi.sendVoc(VocParam(voc))
    }

    suspend fun getUserData(): Long {
        return sikshaApi.getUserData().id
    }

    fun logoutUser() {
        clearUserToken()
    }

    private fun clearUserToken() {
        sikshaPrefObjects.accessToken.setValue("")
    }

    // TODO: 필요한가...? 헷갈려서 일단 만듦.
    private fun attachBearerPrefix(token: String): String =
        if (token.startsWith("Bearer ")) token
        else "Bearer $token"
}
