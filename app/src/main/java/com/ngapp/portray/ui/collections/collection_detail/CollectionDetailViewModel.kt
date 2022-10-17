package com.ngapp.portray.ui.collections.collection_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ngapp.portray.data.db.models.collection.CollectionWithPhoto
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.data.repository.CollectionRepositoryImpl
import com.ngapp.portray.data.repository.PhotoRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CollectionDetailViewModel @Inject constructor(
    private val photoRepository: PhotoRepositoryImpl,
    private val collectionRepository: CollectionRepositoryImpl
) : ViewModel() {

    private val loadingMutableStateFlow = MutableStateFlow(false)
    private val photoListMutableStateFlow = MutableStateFlow<List<Photo?>?>(null)
    private val photoMutableStateFlow = MutableStateFlow<Photo?>(null)
    private val likeMutableStateFlow = MutableStateFlow<Photo?>(null)
    private val toastEventChannel = Channel<Int>(Channel.BUFFERED)

    private val collectionIdString = MutableStateFlow<String?>(null)

    val photoListByCollectionId = collectionIdString.flatMapLatest { collectionId ->
        collectionRepository.getPhotoListByCollectionId(20, collectionId!!).cachedIn(viewModelScope)
    }

    fun getPhotoListByCollectionId(collectionId: String?) {
        if (collectionIdString.value == collectionId && collectionId.isNullOrEmpty()) return
        collectionIdString.value = collectionId
    }

    val loadingFlow: Flow<Boolean>
        get() = loadingMutableStateFlow.asStateFlow()

    val photoListFlow: Flow<List<Photo?>?>
        get() = photoListMutableStateFlow.asStateFlow()

    val photoFlow: Flow<Photo?>
        get() = photoMutableStateFlow.asStateFlow()

    val likeFlow: Flow<Photo?>
        get() = likeMutableStateFlow.asStateFlow()

    val toastFlow: Flow<Int>
        get() = toastEventChannel.receiveAsFlow()

//    fun getElementListByCollectionId(collectionId: String) {
//        viewModelScope.launch {
//            loadingMutableStateFlow.value = true
//            runCatching {
//                photoRepository.getElementListByListId(collectionId)
//            }.onSuccess {
//                photoListMutableStateFlow.value = it
//                loadingMutableStateFlow.value = false
//            }.onFailure { t ->
//                photoListMutableStateFlow.value = null
//                loadingMutableStateFlow.value = false
//                toastEventChannel.trySendBlocking(R.string.load_error)
//                Timber.e("Get photos error = $t")
//            }
//        }
//    }

    fun changeLike(photoId: String, likedByUser: Boolean, likes: String) {
        viewModelScope.launch {
            runCatching {
                photoRepository.changeLike(photoId, likedByUser, likes)
            }.onSuccess {
                Timber.e("likeStatus = $it")
                photoRepository.getElementList()
            }.onFailure { t ->
                Timber.e("Change like error = $t")
            }
        }
    }
}