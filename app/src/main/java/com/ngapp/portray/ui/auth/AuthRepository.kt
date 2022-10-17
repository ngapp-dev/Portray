package com.ngapp.portray.ui.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.room.withTransaction
import androidx.security.crypto.EncryptedSharedPreferences
import com.ngapp.portray.data.Api
import com.ngapp.portray.data.db.PortrayDatabase
import com.ngapp.portray.data.db.models._dao.UserDao
import com.ngapp.portray.data.db.models.user.User
import com.ngapp.portray.utils.ErrorUtils
import com.ngapp.portray.utils.FetchResult
import com.ngapp.portray.utils.haveM
import dagger.hilt.android.qualifiers.ApplicationContext
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.TokenRequest
import retrofit2.Response
import retrofit2.Retrofit
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val encryptedSharedPreferences: EncryptedSharedPreferences,
    private val sharedPreferences: SharedPreferences,
    private val api: Api,
    private val userDao: UserDao,
    private val portrayDatabase: PortrayDatabase,
    private val retrofit: Retrofit
) {

    private suspend fun <T> getResponse(
        request: suspend () -> Response<T>,
        defaultErrorMessage: String
    ): FetchResult<T> {
        return try {
            println("I'm working in thread ${Thread.currentThread().name}")
            val result = request.invoke()
            if (result.isSuccessful) {
                return FetchResult.success(result.body())
            } else {
                val errorResponse = ErrorUtils.parseError(result, retrofit)
                FetchResult.error(
                    errorResponse?.status_message ?: defaultErrorMessage,
                    errorResponse
                )
            }

        } catch (e: IOException) {
            FetchResult.error("Internet connection error", null)
        } catch (e: Throwable) {
            FetchResult.error("Unknown Error", null)
        }
    }

    fun checkAccessToken(): String {
        return if (haveM()) {
            encryptedSharedPreferences.getString("access_token", "") ?: ""
        } else {
            sharedPreferences.getString("access_token", "") ?: ""
        }
    }

    suspend fun logout() {
        encryptedSharedPreferences.edit().clear().apply()
        sharedPreferences.edit().clear().apply()
        userDao.deleteLoggedUserData()
    }

    fun getAuthRequest(): AuthorizationRequest {
        return AppAuth.getAuthRequest()
    }

    fun getEndSessionRequest(): EndSessionRequest {
        return AppAuth.getEndSessionRequest()
    }

    suspend fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
    ) {
        val tokens = AppAuth.performTokenRequestSuspend(authService, tokenRequest)
        //обмен кода на токен произошел успешно, сохраняем токены и завершаем авторизацию
        if (haveM()) {
            encryptedSharedPreferences.edit()
                .putString("access_token", tokens.accessToken)
                .putString("refresh_token", tokens.refreshToken)
                .putString("id_token", tokens.idToken)
                .apply()
        } else {
            sharedPreferences.edit()
                .putString("access_token", tokens.accessToken)
                .putString("refresh_token", tokens.refreshToken)
                .putString("id_token", tokens.idToken)
                .apply()
        }
    }

    suspend fun saveUserData() {
        val resultLoggedUser = getLoggedUserFromApi()
        if (resultLoggedUser.status == FetchResult.Status.SUCCESS) {
            portrayDatabase.withTransaction {
                resultLoggedUser.data?.let { it ->
                    userDao.insertUser(it)
                    userDao.updateLoggedUser(it.username)
                }
            }
        }
    }

    private suspend fun getLoggedUserFromApi(): FetchResult<User> {
        return getResponse(
            request = { api.getLoggedUser() },
            defaultErrorMessage = "Error fetching Logged User details"
        )
    }
}