package com.ngapp.portray.data.repository

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.ngapp.portray.data.Api
import com.ngapp.portray.data.db.PortrayDatabase
import com.ngapp.portray.data.db.models._dao.UserDao
import com.ngapp.portray.data.db.models.collection.Collection
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.data.db.models.user.User
import com.ngapp.portray.data.di.Repository
import com.ngapp.portray.ui.profile.view_pager.user_collections.datasource.UserCollectionListRemoteMediator
import com.ngapp.portray.ui.profile.view_pager.user_liked_photos.datasource.UserLikedPhotoListRemoteMediator
import com.ngapp.portray.ui.profile.view_pager.user_photos.datasource.UserPhotoListRemoteMediator
import com.ngapp.portray.utils.ErrorUtils
import com.ngapp.portray.utils.FetchResult
import com.ngapp.portray.utils.getResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: Api,
    private val userDao: UserDao,
    private val portrayDatabase: PortrayDatabase,
    private val retrofit: Retrofit
) : Repository {


//    suspend fun getUserPublicProfile(username: String): UserPublic {
//        val userPublicProfile = api.getUserPublicProfile(username)
////        Timber.e("userPublicProfile = $userPublicProfile")
//        return userPublicProfile
//    }

//    suspend fun getCurrentUserProfile(): User {
//        val userCurrent = api.getLoggedUser()
////        Timber.e("userCurrent = $userCurrent")
//        return userCurrent
//    }

    suspend fun getLoggedUser(): Flow<FetchResult<User>> {
        return flow {

            val loggedUser = userDao.findLoggedUser()
//            if (loggedUser != null) {
//                emit(getUserByIdFromDatabase(loggedUser.id))
//            }

            loggedUser?.let {
                val userId = it.id
                val username = it.username
//                emit(FetchResult.loading())

                val resultPublicUser = getPublicUserByIdFromApi(username)
                if (resultPublicUser.status == FetchResult.Status.SUCCESS) {
                    portrayDatabase.withTransaction {
                        resultPublicUser.data?.let { it ->
                            userDao.updateUser(it)
                            userDao.updateLoggedUser(it.username)
                        }
                    }
                }
                emit(resultPublicUser)

                emit(getUserByIdFromDatabase(userId))
            }
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun getPublicUserByIdFromApi(userId: String): FetchResult<User> {
        return getResponse(
            request = { api.getUserById(userId) },
            defaultErrorMessage = "Error fetching Public User details",
            retrofit,
            context
        )
    }

    private suspend fun getUserByIdFromDatabase(userId: String): FetchResult<User> {
        return FetchResult.success(portrayDatabase.userDao().getUserById(userId))
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getUserPhotos(itemsPerPage: Int, username: String): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(pageSize = itemsPerPage),
            remoteMediator = UserPhotoListRemoteMediator(api, portrayDatabase, username),
            pagingSourceFactory = { portrayDatabase.photoDao().getUserPhotos(username) }
        ).flow
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getUserLikedPhotos(itemsPerPage: Int, username: String): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(pageSize = itemsPerPage),
            remoteMediator = UserLikedPhotoListRemoteMediator(api, portrayDatabase, username),
            pagingSourceFactory = { portrayDatabase.photoDao().getUserLikedPhotos() }
        ).flow
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getUserCollections(itemsPerPage: Int, username: String): Flow<PagingData<Collection>> {
        return Pager(
            config = PagingConfig(pageSize = itemsPerPage),
            remoteMediator = UserCollectionListRemoteMediator(api, portrayDatabase, username),
            pagingSourceFactory = { portrayDatabase.collectionDao().getUserCollections(username) }
        ).flow
    }


    suspend fun updateUser(
        firstName: String,
        lastName: String,
        portfolioUrl: String,
        location: String,
        bio: String
    ) {
        api.updateUser(firstName, lastName, portfolioUrl, location, bio)
    }

}