package com.ngapp.portray.ui.collections.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.ngapp.portray.data.Api
import com.ngapp.portray.data.db.PortrayDatabase
import com.ngapp.portray.data.db.models.collection.Collection
import com.ngapp.portray.data.db.models.collection_remote_keys.CollectionRemoteKeys
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class CollectionRemoteMediator(
    private val api: Api,
    private val portrayDatabase: PortrayDatabase
) : RemoteMediator<Int, Collection>() {

    private val collectionDao = portrayDatabase.collectionDao()
    private val collectionRemoteKeysDao = portrayDatabase.collectionRemoteKeysDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH

//        val currentTime = System.currentTimeMillis()
//        val lastUpdated = collectionDao.lastUpdated()
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
        state: PagingState<Int, Collection>
    ): MediatorResult {

        val page = when (loadType) {

            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1)
                    ?: STARTING_PAGE_INDEX            // currentPageKey = nextPageKey - 1
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
//                if (remoteKeys == null) {
//                    throw InvalidObjectException("Remote key should not be null for $loadType")
//                }

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

        try {

            val collections = api.getLatestCollections(page, state.config.pageSize)
            collections.forEach {
                it.modDate = System.currentTimeMillis()
            }
            val endOfPaginationReached = collections.isEmpty()

            portrayDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
//                    collectionRemoteKeysDao.clearRemoteKeys()
//                    collectionDao.clearCollections()
                }

                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = collections.map {
                    CollectionRemoteKeys(collectionId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                collectionRemoteKeysDao.insertAll(keys)
                collectionDao.insertAllCollections(collections)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Collection>): CollectionRemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let {
                // Get the remote keys of the last item retrieved
                collectionRemoteKeysDao.remoteKeysCollectionId(it.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Collection>): CollectionRemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let {
                // Get the remote keys of the first items retrieved
                collectionRemoteKeysDao.remoteKeysCollectionId(it.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Collection>
    ): CollectionRemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let {
                collectionRemoteKeysDao.remoteKeysCollectionId(it)
            }
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }
}