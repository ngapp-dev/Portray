package com.ngapp.portray.ui.home

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.data.repository.PhotoRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val photoRepository: PhotoRepositoryImpl
) : ViewModel() {

    private val likedEventChannel = Channel<Boolean>(Channel.BUFFERED)

    private val latestPhoto = photoRepository.getLatestPhotos(20).cachedIn(viewModelScope)
    private val searchQuery = MutableStateFlow<String?>(null)

    val latestPhotosAndSearch: Flow<PagingData<Photo>> =
        searchQuery.flatMapLatest { query ->
            if (query.isNullOrEmpty()) {
                latestPhoto
            } else {
                photoRepository.getSearchPhotos(query, 20).cachedIn(viewModelScope)
            }
        }

    val likedFlow: Flow<Boolean>
        get() = likedEventChannel.receiveAsFlow()


    fun setQuery(query: String) {
        if (searchQuery.value == query) return
        searchQuery.value = query
    }

    fun changeLike(photoId: String, likedByUser: Boolean, likes: String) {
        viewModelScope.launch {
            runCatching {
                photoRepository.changeLike(photoId, likedByUser, likes)
            }.onSuccess {
                Timber.e("likeStatus = $it")
                latestPhotosAndSearch
            }.onFailure { t ->
                Timber.e("Change like error = $t")
            }
        }
    }
}