package com.ngapp.portray.ui.home.photo_detail

import android.net.Uri
import androidx.lifecycle.*
import com.ngapp.portray.R
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.data.db.models.photo.downloadUrl.DownloadUrl
import com.ngapp.portray.data.repository.PhotoRepositoryImpl
import com.ngapp.portray.utils.FetchResult
import com.ngapp.portray.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class PhotoDetailViewModel @Inject constructor(
    private val photoRepository: PhotoRepositoryImpl
) : ViewModel() {

    private var currentJob: Job? = null

    private val loadingMutableStateFlow = MutableStateFlow(false)
    private val getUrlSuccessEventChannel = Channel<DownloadUrl>(Channel.BUFFERED)
    private val photoMutableStateFlow = MutableStateFlow<Photo?>(null)
    private val toastEventChannel = Channel<Int>(Channel.BUFFERED)
    private val downloadStatusEventChannel = Channel<Boolean>(Channel.BUFFERED)
    private val likeEventChannel = Channel<FetchResult<Photo>>(Channel.BUFFERED)
    private val likeMutableLiveDate = MutableLiveData<Boolean>()

    private val getUriSuccessMutableLiveEvent = SingleLiveEvent<Uri>()
    private val getUriErrorSingleLiveEvent = SingleLiveEvent<Unit>()

    val getUriSuccessMutableLiveData: LiveData<Uri>
        get() = getUriSuccessMutableLiveEvent

    val getUriErrorSingleLiveData: LiveData<Unit>
        get() = getUriErrorSingleLiveEvent

    val loadingFlow: Flow<Boolean>
        get() = loadingMutableStateFlow.asStateFlow()

    val getUrlFlow: Flow<DownloadUrl>
        get() = getUrlSuccessEventChannel.receiveAsFlow()

//    val photoFlow: Flow<Photo?>
//        get() = photoMutableStateFlow.asStateFlow()

    val toastLiveData: Flow<Int>
        get() = toastEventChannel.receiveAsFlow()

    val downloadStatusLiveData: Flow<Boolean>
        get() = downloadStatusEventChannel.receiveAsFlow()

    val likeLiveData: LiveData<Boolean>
        get() = likeMutableLiveDate

    val likeFlow: Flow<FetchResult<Photo>>
        get() = likeEventChannel.receiveAsFlow()


//    fun getPhotoById(photoId: String) {
//        loadingMutableStateFlow.value = true
//        viewModelScope.launch {
//            runCatching {
////                photoRepository.getElementById(photoId)
//                photoRepository.getElementByIdFromDtb(photoId)
//            }.onSuccess {
//                photoMutableStateFlow.value = it
//                loadingMutableStateFlow.value = false
//            }.onFailure { t ->
//                photoMutableStateFlow.value = null
//                loadingMutableStateFlow.value = false
//                toastEventChannel.trySendBlocking(R.string.load_error)
//                Timber.e("Get photo $photoId by ID error = $t")
//            }
//        }
//    }

    fun changeLike(photoId: String, likedByUser: Boolean, likes: String) {
        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                likeMutableLiveDate.postValue(photoRepository.changeLike(photoId, likedByUser, likes))
//            } catch (t: Throwable) {
//                Timber.e("Change like error = $t")
//            }

            runCatching {
                photoRepository.changeLike(photoId, likedByUser, likes)
            }.onSuccess {
                Timber.e("likeStatus = $it")
//                getPhotoByIdNow(photoId)
                likeEventChannel.send(it)
            }.onFailure { t ->
                Timber.e("Change like error = $t")
            }
        }
    }

    fun triggerDownload(photoId: String, ixId: String) {
        viewModelScope.launch {
            runCatching {
                photoRepository.triggerDownload(photoId, ixId)
            }.onSuccess {
                Timber.d("Trigger download success")
            }.onFailure {
                toastEventChannel.send(R.string.load_error)
            }
        }
    }

    fun saveImage(photoId: String, url: String, fileName: String) {
        viewModelScope.launch {
//            loadingMutableStateFlow.value = true
            runCatching {
                photoRepository.downloadPhoto(photoId, url, fileName)
//                photoRepository.saveImage(name, url)
            }.onSuccess {
                downloadStatusEventChannel.send(true)
                toastEventChannel.send(R.string.photo_download_success)
            }.onFailure {
                downloadStatusEventChannel.send(false)
                toastEventChannel.send(R.string.photo_download_error)
            }
        }
    }

    fun cancelWorker() {
        viewModelScope.launch {
            try {
                photoRepository.cancelWorker()
            } catch (t: Throwable) {

            }
        }
    }

    private var photoId = MutableLiveData<String>()
    private val _photo: LiveData<FetchResult<Photo>> = photoId.switchMap {
        liveData {
            photoRepository.getPhotoById(it).onStart {
//                emit(FetchResult.loading())
            }.collect {
                emit(it)
            }
        }
    }
    val photo = _photo

    fun getPhotoByIdNow(id: String) {
        photoId.value = id
    }

}