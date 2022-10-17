package com.ngapp.portray.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ngapp.portray.data.Api
import com.ngapp.portray.data.db.PortrayDatabase
import com.ngapp.portray.data.db.models.collection.Collection
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.data.di.Repository
import com.ngapp.portray.ui.collections.collection_detail.datasource.CollectionPhotoListRemoteMediator
import com.ngapp.portray.ui.collections.datasource.CollectionRemoteMediator
import com.ngapp.portray.ui.collections.datasource.SearchCollectionPagingSource
import com.ngapp.portray.utils.ErrorUtils
import com.ngapp.portray.utils.FetchResult
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import javax.inject.Inject

class CollectionRepositoryImpl @Inject constructor(
    private val api: Api,
    private val portrayDatabase: PortrayDatabase,
    private val retrofit: Retrofit
) : Repository {

    suspend fun getElementList(): List<Collection> {
        return api.getPublicCollections()
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getLatestCollections(itemsPerPage: Int): Flow<PagingData<Collection>> {
        return Pager(
            config = PagingConfig(pageSize = itemsPerPage),
            remoteMediator = CollectionRemoteMediator(api, portrayDatabase),
            pagingSourceFactory = { portrayDatabase.collectionDao().getAllCollections() }
        ).flow
    }

    fun getSearchCollections(query: String, itemsPerPage: Int): Flow<PagingData<Collection>> {
        return Pager(
            config = PagingConfig(pageSize = itemsPerPage)
        ) {
            SearchCollectionPagingSource(api, query)
        }.flow
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getPhotoListByCollectionId(itemsPerPage: Int, collectionId: String): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(pageSize = itemsPerPage),
            remoteMediator = CollectionPhotoListRemoteMediator(collectionId, api, portrayDatabase),
            pagingSourceFactory = { portrayDatabase.collectionDao().getCollectionWithPhotos(collectionId) }
        ).flow
    }

//    suspend fun getCollectionById(collectionId: String): Flow<FetchResult<Collection>> {
//        return flow {
//            emit(getCollectionByIdFromDatabase(collectionId))
//            emit(FetchResult.loading())
//
//            val result = getCollectionByIdFromApi(collectionId)
//            if (result.status == FetchResult.Status.SUCCESS) {
//                result.data?.let { it ->
////                    collectionDao.deleteAll(it)
////                    collectionDao.insertAll(it)
//                }
//            }
//            emit(result)
//
//        }.flowOn(Dispatchers.IO)
//    }
//
//    private suspend fun getCollectionByIdFromApi(collectionId: String): FetchResult<Collection> {
//        return getResponse(
//            request = { api.getCollectionById(collectionId) },
//            defaultErrorMessage = "Error fetching Collection details"
//        )
//    }
//
//    private suspend fun getCollectionByIdFromDatabase(collectionId: String): FetchResult<Collection> {
//        return FetchResult.success(portrayDatabase.collectionDao().getCollectionById(collectionId))
//    }
}