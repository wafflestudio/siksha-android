package com.wafflestudio.siksha2.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.wafflestudio.siksha2.FeatureChecker
import com.wafflestudio.siksha2.R
import com.wafflestudio.siksha2.databinding.ActivitySplashBinding
import com.wafflestudio.siksha2.network.OAuthProvider
import com.wafflestudio.siksha2.network.result.NetworkResult
import com.wafflestudio.siksha2.repositories.UserStatusManager
import com.wafflestudio.siksha2.utils.setVisibleOrGone
import com.wafflestudio.siksha2.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    @Inject
    lateinit var userStatusManager: UserStatusManager

    private lateinit var googleSignInLauncher: ActivityResultLauncher<Unit>

    private lateinit var kakaoSignInLauncher: () -> Unit

    @Inject
    lateinit var featureChecker: FeatureChecker

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch(Dispatchers.Main) {
            if (checkInternetConnection().not()) {
                showToast("네트워크 연결이 불안정합니다.")
                delay(1000L)
                startActivity(Intent(this@SplashActivity, RootActivity::class.java))
                finish()
                return@launch
            }

            changeAppIcon()

            if (checkLoginStatus().not()) {
                binding.googleLoginButton.setVisibleOrGone(true)
                binding.kakaoLoginButton.setVisibleOrGone(true)

                return@launch
            }

            // 실험
            delay(250L)
            startActivity(Intent(this@SplashActivity, RootActivity::class.java))
            finish()
        }

        featureChecker.fetchFeaturesConfig()
        setUpGoogleLogin()
        setUpKakaoLogin()

        binding.googleLoginButton.setOnClickListener {
            googleSignInLauncher.launch(Unit)
        }

        binding.kakaoLoginButton.setOnClickListener {
            kakaoSignInLauncher.invoke()
        }
    }

    private fun onOAuthSuccess(provider: OAuthProvider, token: String) {
        lifecycleScope.launch {
            when (val loginResponse = userStatusManager.loginWithOAuthToken(provider, token)) {
                is NetworkResult.Success -> {
                    startActivity(Intent(this@SplashActivity, RootActivity::class.java))
                    finish()
                }
                is NetworkResult.Failure -> {
                    showToast(loginResponse.message)
                }
                is NetworkResult.NetworkError -> {
                    showToast(getString(R.string.common_network_error))
                }
                else -> showToast(getString(R.string.common_unknown_error))
            }
        }
    }

    private fun setUpKakaoLogin() {
        kakaoSignInLauncher = {
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    showToast("카카오 로그인 실패")
                    Timber.e(error)
                } else if (token != null) {
                    onOAuthSuccess(OAuthProvider.KAKAO, token.accessToken)
                }
            }
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
    }

    private fun setUpGoogleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.EMAIL))
            .requestIdToken(getString(R.string.google_server_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val googleSignInResultContract = object :
            ActivityResultContract<Unit, Task<GoogleSignInAccount>>() {
            override fun createIntent(context: Context, input: Unit): Intent {
                return mGoogleSignInClient.signInIntent
            }

            override fun parseResult(resultCode: Int, intent: Intent?): Task<GoogleSignInAccount> {
                return GoogleSignIn.getSignedInAccountFromIntent(intent)
            }
        }
        googleSignInLauncher =
            this@SplashActivity.registerForActivityResult(googleSignInResultContract) { it ->
                try {
                    it.getResult(ApiException::class.java)?.idToken?.let { token ->
                        onOAuthSuccess(OAuthProvider.GOOGLE, token)
                    }
                } catch (e: ApiException) {
                    showToast("구글 로그인 실패")
                    Timber.e(e)
                }
            }
    }

    private fun checkInternetConnection(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val connection = connectivityManager.activeNetwork.let {
            connectivityManager.getNetworkCapabilities(it)
        }
        return connection != null && (
            connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            )
    }

    private suspend fun checkLoginStatus(): Boolean {
        return userStatusManager.refreshUserToken()
    }

    private fun changeAppIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val mainComponent = ComponentName(this, "com.wafflestudio.siksha2.ui.SplashActivity")
            val normalIconComponent = ComponentName(this, "com.wafflestudio.siksha2.ui.SikshaNormal")
            val festivalIconComponent = ComponentName(this, "com.wafflestudio.siksha2.ui.SikshaFestival")
            packageManager.setComponentEnabledSetting(
                normalIconComponent,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
            packageManager.setComponentEnabledSetting(
                festivalIconComponent,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )

            if (featureChecker.isFeatureEnabled("festivalFeatureEnabled")) {
                packageManager.setComponentEnabledSetting(
                    festivalIconComponent,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
            } else {
                packageManager.setComponentEnabledSetting(
                    normalIconComponent,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
        }
    }
}
