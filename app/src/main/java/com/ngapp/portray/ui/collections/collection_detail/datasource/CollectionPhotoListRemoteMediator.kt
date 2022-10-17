package com.ngapp.portray.ui.collections.collection_detail.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.ngapp.portray.data.Api
import com.ngapp.portray.data.db.PortrayDatabase
import com.ngapp.portray.data.db.models.collection.CollectionWithPhoto
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.data.db.models.photo_remote_keys.PhotoRemoteKeys
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException

@OptIn(ExperimentalPagingApi::class)
class CollectionPhotoListRemoteMediator(
    private val collectionId: String,
    private val api: Api,
    private val portrayDatabase: PortrayDatabase
) : RemoteMediator<Int, Photo>() {

    private val photoDao = portrayDatabase.photoDao()
    private val photoRemoteKeysDao = portrayDatabase.photoRemoteKeysDao()

    override suspend fun initialize(): RemoteMediator.InitializeAction {
        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.
        return RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Photo>
    ): RemoteMediator.MediatorResult {

        val page = when (loadType) {

            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1)
                    ?: STARTING_PAGE_INDEX            // currentPageKey = nextPageKey - 1
            }

            LoadType.PREPEND -> {
//                val remoteKeys = getRemoteKeyForFirstItem(state)
//                if (remoteKeys == null) {
//                    throw InvalidObjectException("Remote key should not be null for $loadType")
//                }
//
//                val prevKey = remoteKeys?.prevKey
//                if (prevKey == null) {
//                    return RemoteMediator.MediatorResult.Success(endOfPaginationReached = true)
//                }
//                prevKey
                return RemoteMediator.MediatorResult.Success(endOfPaginationReached = true)
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return RemoteMediator.MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                if (remoteKeys?.nextKey == null) {
                    throw InvalidObjectException("Remote key should not be null for $loadType")
                }
                nextKey
            }
        }

        try {

            val photos = api.getPhotoListByCollectionId(collectionId, page, state.config.pageSize)
            val endOfPaginationReached = photos.isEmpty()

            portrayDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
//                    photoRemoteKeysDao.clearRemoteKeys()
//                    photoDao.clearPhotos()
                }

                val prevKey =
                    if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = photos.map {
                    PhotoRemoteKeys(photoId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                photoRemoteKeysDao.insertAll(keys)
                photoDao.insertAllPhotos(photos)
                photos.map {
                    photoDao.insertCollectionId(it.id, collectionId)
                }

            }
            return RemoteMediator.MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: IOException) {
            return RemoteMediator.MediatorResult.Error(e)
        } catch (e: HttpException) {
            return RemoteMediator.MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Photo>): PhotoRemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let {
                // Get the remote keys of the last item retrieved
                photoRemoteKeysDao.remoteKeysPhotoId(it.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Photo>): PhotoRemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let {
                // Get the remote keys of the first items retrieved
                photoRemoteKeysDao.remoteKeysPhotoId(it.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Photo>
    ): PhotoRemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let {
                photoRemoteKeysDao.remoteKeysPhotoId(it)
            }
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }
}