package com.ngapp.portray.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapp.portray.R
import com.ngapp.portray.data.db.models.onboading_screen.OnboardingScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    private val onboardingScreensMutableStateFlow = MutableStateFlow<List<OnboardingScreen>?>(null)
    private val currentOnboardingScreenMutableStateFlow = MutableStateFlow<OnboardingScreen?>(null)
    private val toastEventChannel = Channel<Int>(Channel.BUFFERED)

    val toastFlow: Flow<Int>
        get() = toastEventChannel.receiveAsFlow()

    val onboardingScreensStateFlow: Flow<List<OnboardingScreen>?>
        get() = onboardingScreensMutableStateFlow.asStateFlow()

    val currentOnboardingScreenStateFlow: Flow<OnboardingScreen?>
        get() = currentOnboardingScreenMutableStateFlow.asStateFlow()

    fun createOnboardingScreens() {
        viewModelScope.launch {
            runCatching {
                onboardingRepository.createBasicOnboardScreens()
            }.onFailure {
                toastEventChannel.trySendBlocking(R.string.load_error)
            }
        }
    }

    fun loadOnboardingScreens() {
        viewModelScope.launch {
            runCatching {
                onboardingRepository.getElementList()
            }.onSuccess {
                onboardingScreensMutableStateFlow.value = it
            }.onFailure {
                onboardingScreensMutableStateFlow.value = null
                toastEventChannel.trySendBlocking(R.string.load_error)
            }
        }
    }

    fun loadOnboardingScreenById(id: Long) {
        viewModelScope.launch {
            runCatching {
                onboardingRepository.getElementById(id)
            }.onSuccess {
                currentOnboardingScreenMutableStateFlow.value = it
            }.onFailure {
                currentOnboardingScreenMutableStateFlow.value = null
                toastEventChannel.trySendBlocking(R.string.load_error)
            }
        }
    }


}