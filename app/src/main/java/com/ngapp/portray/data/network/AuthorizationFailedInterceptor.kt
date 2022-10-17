package com.ngapp.portray.data.network

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import com.ngapp.portray.ui.auth.AppAuth
import com.ngapp.portray.utils.haveM
import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthorizationService
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Singleton
class AuthorizationFailedInterceptor(
    private val authorizationService: AuthorizationService,
    private val encryptedSharedPreferences: EncryptedSharedPreferences?,
    private val sharedPreferences: SharedPreferences?
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequestTimestamp = System.currentTimeMillis()
        val originalResponse = chain.proceed(chain.request())
        return originalResponse
            .takeIf { it.code != 401 }
            ?: handleUnauthorizedResponse(chain, originalResponse, originalRequestTimestamp)

    }

    private fun handleUnauthorizedResponse(
        chain: Interceptor.Chain,
        originalResponse: Response,
        requestTimestamp: Long
    ): Response {
        val latch = getLatch()
        return when {
            latch != null && latch.count > 0 -> handleTokenIsUpdating(
                chain,
                latch,
                requestTimestamp,
                originalResponse
            )
                ?: originalResponse
            tokenUpdateTime > requestTimestamp -> updateTokenAndProceedChain(
                chain
            )
            else -> handleTokenNeedRefresh(chain) ?: originalResponse
        }
    }

    private fun handleTokenIsUpdating(
        chain: Interceptor.Chain,
        latch: CountDownLatch,
        requestTimestamp: Long,
        originalResponse: Response
    ): Response? {
        return if (latch.await(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            && tokenUpdateTime > requestTimestamp
        ) {
            updateTokenAndProceedChain(chain)
        } else {
            null
        }
    }

    private fun handleTokenNeedRefresh(
        chain: Interceptor.Chain
    ): Response? {
        return if (refreshToken()) {
            updateTokenAndProceedChain(chain)
        } else {
            null
        }
    }

    private fun updateTokenAndProceedChain(
        chain: Interceptor.Chain
    ): Response {
        val newRequest = updateOriginalCallWithNewToken(chain.request())
        return chain.proceed(newRequest)
    }

    private fun refreshToken(): Boolean {
        initLatch()

        val tokenRefreshed = runBlocking {
            runCatching {
                val refreshRequest =
                    AppAuth.getRefreshTokenRequest(
                        if (haveM()) {
                            encryptedSharedPreferences?.getString(
                                "refresh_token",
                                ""
                            ).orEmpty()
                        } else {
                            sharedPreferences?.getString(
                                "refresh_token",
                                ""
                            ).orEmpty()
                        }
                    )
                AppAuth.performTokenRequestSuspend(authorizationService, refreshRequest)
            }
                .getOrNull()
                ?.let { tokens ->
                    if (haveM()) {
                        encryptedSharedPreferences?.edit()
                            ?.putString("access_token", tokens.accessToken)
                            ?.putString("refresh_token", tokens.refreshToken)
                            ?.putString("id_token", tokens.idToken)
                            ?.apply()
                    } else {
                        sharedPreferences?.edit()
                            ?.putString("access_token", tokens.accessToken)
                            ?.putString("refresh_token", tokens.refreshToken)
                            ?.putString("id_token", tokens.idToken)
                            ?.apply()
                    }
                    true
                } ?: false
        }

        if (tokenRefreshed) {
            tokenUpdateTime = System.currentTimeMillis()
        } else {
            // не удалось обновить токен, произвести логаут
//            unauthorizedHandler.onUnauthorized()
            Timber.d("logout after token refresh failure")
        }
        getLatch()?.countDown()
        return tokenRefreshed
    }

    private fun updateOriginalCallWithNewToken(request: Request): Request {
        return if (haveM()) {
            encryptedSharedPreferences?.getString("access_token", "")?.let { newAccessToken ->
                request
                    .newBuilder()
                    .header("Authorization", newAccessToken)
                    .build()
            } ?: request
        } else {
            sharedPreferences?.getString("access_token", "")?.let { newAccessToken ->
                request
                    .newBuilder()
                    .header("Authorization", newAccessToken)
                    .build()
            } ?: request
        }
    }

    companion object {

        private const val REQUEST_TIMEOUT = 30L

        @Volatile
        private var tokenUpdateTime: Long = 0L

        private var countDownLatch: CountDownLatch? = null

        @Synchronized
        fun initLatch() {
            countDownLatch = CountDownLatch(1)
        }

        @Synchronized
        fun getLatch() = countDownLatch
    }
}