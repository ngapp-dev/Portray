package com.ngapp.portray.data.network

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import com.ngapp.portray.utils.haveM
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthorizationInterceptor @Inject constructor(
    private val encryptedSharedPreferences: EncryptedSharedPreferences?,
    private val sharedPreferences: SharedPreferences?,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.request()
            .addTokenHeader()
            .let { chain.proceed(it) }
    }

    private fun Request.addTokenHeader(): Request {
        val authHeaderName = "Authorization"
        return newBuilder()
            .apply {
                if (haveM()) {
                    encryptedSharedPreferences?.getString("access_token", "")
                    val token = encryptedSharedPreferences?.getString("access_token", "")
                    if (token != null) {
                        header(authHeaderName, token.withBearer())
                    }
                } else {
                    sharedPreferences?.getString("access_token", "")
                    val token = sharedPreferences?.getString("access_token", "")
                    if (token != null) {
                        header(authHeaderName, token.withBearer())
                    }
                }

            }
            .build()
    }

    private fun String.withBearer() = "Bearer $this"
}
