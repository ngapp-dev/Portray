package com.ngapp.portray.ui.auth.ui

import android.app.Application
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapp.portray.R
import com.ngapp.portray.ui.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val application: Application,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val authService: AuthorizationService = AuthorizationService(application)

    private val openAuthPageEventChannel = Channel<Intent>(Channel.BUFFERED)
    private val toastEventChannel = Channel<Int>(Channel.BUFFERED)
    private val accessTokenEventChannel = Channel<String>(Channel.BUFFERED)
    private val authSuccessEventChannel = Channel<Unit>(Channel.BUFFERED)
    private val saveUserDataEventChannel = Channel<Unit>(Channel.BUFFERED)

    private val loadingMutableStateFlow = MutableStateFlow(false)

    private val saveAuthUserEventChannel = Channel<Unit>(Channel.BUFFERED)

    val openAuthPageFlow: Flow<Intent>
        get() = openAuthPageEventChannel.receiveAsFlow()

    val loadingFlow: Flow<Boolean>
        get() = loadingMutableStateFlow.asStateFlow()

    val toastFlow: Flow<Int>
        get() = toastEventChannel.receiveAsFlow()

    val accessTokenFlow: Flow<String>
        get() = accessTokenEventChannel.receiveAsFlow()

    val authSuccessFlow: Flow<Unit>
        get() = authSuccessEventChannel.receiveAsFlow()

    val saveAuthUserSuccessFlow: Flow<Unit>
        get() = saveAuthUserEventChannel.receiveAsFlow()

    fun onAuthCodeFailed(exception: AuthorizationException) {
        toastEventChannel.trySendBlocking(R.string.auth_canceled)
    }


    fun checkAccessToken() {
        viewModelScope.launch {
            runCatching {
                authRepository.checkAccessToken()
            }.onSuccess {
                accessTokenEventChannel.send(it)
            }.onFailure {
                accessTokenEventChannel.send("")
            }
        }
    }

    fun onAuthCodeReceived(tokenRequest: TokenRequest) {
        viewModelScope.launch {
            loadingMutableStateFlow.value = true
            runCatching {
                authRepository.performTokenRequest(
                    authService = authService,
                    tokenRequest = tokenRequest
                )
            }.onSuccess {
//                loadingMutableStateFlow.value = false
                authSuccessEventChannel.send(Unit)
                toastEventChannel.send(R.string.auth_successful)
            }.onFailure {
                loadingMutableStateFlow.value = false
                toastEventChannel.send(R.string.auth_canceled)
            }

        }
    }

    fun openLoginPage() {
        val customTabsIntent = CustomTabsIntent.Builder().build()

        val openAuthPageIntent = authService.getAuthorizationRequestIntent(
            authRepository.getAuthRequest(),
            customTabsIntent
        )
        openAuthPageEventChannel.trySendBlocking(openAuthPageIntent)
    }

    fun saveAuthUser() {
        viewModelScope.launch(Dispatchers.IO) {
            loadingMutableStateFlow.value = true
            runCatching {
                authRepository.saveUserData()
            }.onSuccess { it ->
//                loadingMutableStateFlow.value = false
                saveAuthUserEventChannel.send(it)
            }.onFailure {
                loadingMutableStateFlow.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        authService.dispose()
    }
}