package com.ngapp.portray.data

import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.data.db.models.collection.Collection
import com.ngapp.portray.data.db.models.unsplash_search_response.UnsplashSearchCollectionResponse
import com.ngapp.portray.data.db.models.unsplash_search_response.UnsplashSearchPhotoResponse
import com.ngapp.portray.data.db.models.user.User
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface Api {

    @GET("/photos")
    suspend fun getPhotoList(): List<Photo>

    @GET("/photos/{id}")
    suspend fun getPhotoById(
        @Path("id") id: String
    ): Response<Photo>

    @GET("/photos/{id}")
    suspend fun getCollectionById(
        @Path("id") id: String
    ): Response<Collection>

    @GET("/collections")
    suspend fun getPublicCollections(): List<Collection>

    @GET("/me")
    suspend fun getLoggedUser(): Response<User>

    @PUT("/me")
    suspend fun updateUser(
        @Query("first_name") firstName: String,
        @Query("last_name") lastName: String,
        @Query("url") portfolioUrl: String,
        @Query("location") location: String,
        @Query("bio") bio: String
    ): ResponseBody

    @GET("/users/{username}")
    suspend fun getUserById(
        @Path("username") username: String
    ): Response<User>

    @GET("/users/{username}/photos")
    suspend fun getUserPhotos(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): List<Photo>

    @GET("/users/{username}/likes")
    suspend fun getUserLikedPhotos(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): List<Photo>

    @GET("/users/{username}/collections")
    suspend fun getUserCollections(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): List<Collection>

    @POST("/photos/{id}/like")
    suspend fun doLikePhoto(
        @Path("id") id: String
    ): Response<Photo>

    @DELETE("/photos/{id}/like")
    suspend fun doUnlikePhoto(
        @Path("id") id: String
    ): Response<Photo>

    @GET("/photos/{id}/download")
    suspend fun triggerDownload(
        @Path("id") id: String,
        @Query("ixId") ixId: String,
    )

    @GET
    suspend fun downloadPhoto(
        @Url url: String
    ): ResponseBody

    @GET("photos")
    suspend fun getLatestPhotos(
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): List<Photo>

    @GET("search/photos")
    suspend fun getSearchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): UnsplashSearchPhotoResponse

    @GET("collections")
    suspend fun getLatestCollections(
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): List<Collection>

    @GET("search/collections")
    suspend fun getSearchCollections(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): UnsplashSearchCollectionResponse

    @GET("/collections/{id}/photos")
    suspend fun getPhotoListByCollectionId(
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): List<Photo>
}
