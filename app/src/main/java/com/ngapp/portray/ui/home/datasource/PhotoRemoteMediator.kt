package com.ngapp.portray.ui.home.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.ngapp.portray.data.Api
import com.ngapp.portray.data.db.PortrayDatabase
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.data.db.models.photo_remote_keys.PhotoRemoteKeys
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class PhotoRemoteMediator(
    private val api: Api,
    private val portrayDatabase: PortrayDatabase
) : RemoteMediator<Int, Photo>() {

    private val photoDao = portrayDatabase.photoDao()
    private val photoRemoteKeysDao = portrayDatabase.photoRemoteKeysDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH

//        val currentTime = System.currentTimeMillis()
//        val lastUpdated = photoDao.lastUpdated()
//
//        val cacheTimeout = TimeUnit.HOURS.convert(1, TimeUnit.MILLISECONDS)
//
//        return if (lastUpdated != null && (currentTime - lastUpdated >= cacheTimeout)) {
//            InitializeAction.SKIP_INITIAL_REFRESH
//        } else {
//            InitializeAction.LAUNCH_INITIAL_REFRESH
//        }

    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Photo>
    ): MediatorResult {
        try {
            val page = when (loadType) {

                LoadType.REFRESH -> {
//                    null
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1)
                        ?: STARTING_PAGE_INDEX
//                     currentPageKey = nextPageKey - 1
                }

                LoadType.PREPEND -> {

                val remoteKeys = getRemoteKeyForFirstItem(state)
                if (remoteKeys == null) {
                    throw InvalidObjectException("Remote key should not be null for $loadType")
                }

                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                prevKey
//                    val remoteKeys = getRemoteKeyForFirstItem(state)
//                    val prevKey = remoteKeys?.prevKey
//                    if (prevKey == null) {
//                        return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
//                    }
//                    prevKey
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {

                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                    if (nextKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    }
                    if (remoteKeys?.nextKey == null) {
                        throw InvalidObjectException("Remote key should not be null for $loadType")
                    }
                    nextKey
                }
            }

            val photos = api.getLatestPhotos(page, state.config.pageSize)
            photos.forEach {
                it.modDate = System.currentTimeMillis()
            }
            val endOfPaginationReached = photos.isEmpty()

            portrayDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    state.config.initialLoadSize
//                    photoRemoteKeysDao.clearRemoteKeys()
//                    photoDao.clearPhotos()
                }

                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = photos.map {
                    PhotoRemoteKeys(photoId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                photoRemoteKeysDao.insertAll(keys)
                photoDao.insertAllPhotos(photos)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
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