package com.ngapp.portray.ui.profile

import android.app.Application
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.ngapp.portray.data.db.models.user.User
import com.ngapp.portray.data.repository.PhotoRepositoryImpl
import com.ngapp.portray.data.repository.ProfileRepositoryImpl
import com.ngapp.portray.ui.auth.AuthRepository
import com.ngapp.portray.utils.FetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationService
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepositoryImpl,
    private val photoRepository: PhotoRepositoryImpl,
    private val application: Application,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val loadTrigger = MutableLiveData(Unit)

    private val authService: AuthorizationService = AuthorizationService(application)
    private val toastEventChannel = Channel<Int>(Channel.BUFFERED)
    private val logoutPageEventChannel = Channel<Intent>(Channel.BUFFERED)
    private val logoutCompletedEventChannel = Channel<Unit>(Channel.BUFFERED)
    private val usernameQuery = MutableStateFlow<String?>(null)

    private val updateUserEventChannel = Channel<Unit>(Channel.BUFFERED)

    val updateUserSuccessFlow: Flow<Unit>
        get() = updateUserEventChannel.receiveAsFlow()

    val toastFlow: Flow<Int>
        get() = toastEventChannel.receiveAsFlow()

    val logoutPageFlow: Flow<Intent>
        get() = logoutPageEventChannel.receiveAsFlow()

    val logoutCompletedFlow: Flow<Unit>
        get() = logoutCompletedEventChannel.receiveAsFlow()

    val userPhotos = usernameQuery.flatMapLatest { username ->
        profileRepository.getUserPhotos(15, username!!).cachedIn(viewModelScope)
    }

    val userLikedPhotos = usernameQuery.flatMapLatest { username ->
        profileRepository.getUserLikedPhotos(5, username!!).cachedIn(viewModelScope)
    }

    val userCollections = usernameQuery.flatMapLatest { username ->
        profileRepository.getUserCollections(15, username!!).cachedIn(viewModelScope)
    }

    val user: LiveData<FetchResult<User>> = loadTrigger.switchMap {
        liveData {
            profileRepository.getLoggedUser().onStart {
                emit(FetchResult.loading())
            }.collect {
                emit(it)
            }
        }
    }

    fun refresh() {
        loadTrigger.value = Unit
    }

    fun getUserPhotos(username: String) {
        if (usernameQuery.value == username) return
        usernameQuery.value = username
    }

    fun getUserLikedPhotos(username: String) {
        if (usernameQuery.value == username) return
        usernameQuery.value = username
    }

    fun getUserCollections(username: String) {
        if (usernameQuery.value == username) return
        usernameQuery.value = username
    }


    fun changeLike(photoId: String, likedByUser: Boolean, username: String, likes: String) {
        viewModelScope.launch {
            runCatching {
                photoRepository.changeLike(photoId, likedByUser, likes)
            }.onSuccess {
                getUserLikedPhotos(username)
            }.onFailure { t ->
                Timber.e("Change like error = $t")
            }
        }
    }

    fun updateUser(
        firstName: String,
        lastName: String,
        portfolioUrl: String,
        location: String,
        bio: String
    ) {
        viewModelScope.launch {
            runCatching {
                profileRepository.updateUser(firstName, lastName, portfolioUrl, location, bio)
            }.onSuccess {
                updateUserEventChannel.send(it)
            }.onFailure {
                Timber.e("Update user error = $it")
            }
        }

    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            val customTabsIntent = CustomTabsIntent.Builder().build()
            val logoutPageIntent = authService.getEndSessionRequestIntent(
                authRepository.getEndSessionRequest(),
                customTabsIntent
            )
            logoutPageEventChannel.trySendBlocking(logoutPageIntent)
        }
    }

    fun webLogoutComplete() {
        logoutCompletedEventChannel.trySendBlocking(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        authService.dispose()
    }
}