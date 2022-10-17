package com.ngapp.portray.data.db.models._dao

import androidx.paging.PagingSource
import androidx.room.*
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.data.db.models.photo.PhotoContract

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPhotos(photos: List<Photo>)

    @Query("SELECT * FROM ${PhotoContract.TABLE_NAME}")
    fun getAllPhotos(): PagingSource<Int, Photo>

    @Query("DELETE FROM ${PhotoContract.TABLE_NAME}")
    suspend fun clearPhotos()

    @Query("SELECT * FROM ${PhotoContract.TABLE_NAME} WHERE ${PhotoContract.Columns.PHOTO_ID} = :photoId")
    suspend fun getPhotoById(photoId: String): Photo

    @Update
    suspend fun updatePhoto(photo: Photo)

    @Query("UPDATE ${PhotoContract.TABLE_NAME} SET ${PhotoContract.Columns.COLLECTION_ID} = :collectionId WHERE ${PhotoContract.Columns.PHOTO_ID} = :photoId")
    suspend fun insertCollectionId(photoId: String, collectionId: String)

    @Query("SELECT MAX(${PhotoContract.Columns.MOD_DATE}) from ${PhotoContract.TABLE_NAME}")
    suspend fun lastUpdated(): Long?

    @Query("SELECT * FROM ${PhotoContract.TABLE_NAME} WHERE user_username = :username ")
    fun getUserPhotos(username: String): PagingSource<Int, Photo>

    @Query("SELECT * FROM ${PhotoContract.TABLE_NAME} WHERE liked_by_user = 1 ")
    fun getUserLikedPhotos(): PagingSource<Int, Photo>


    @Query("UPDATE ${PhotoContract.TABLE_NAME} SET ${PhotoContract.Columns.LIKED_BY_USER} = 1, ${PhotoContract.Columns.LIKES} = :likes WHERE ${PhotoContract.Columns.PHOTO_ID} = :photoId")
    fun updateLikePhoto(photoId: String, likes: String)

    @Query("UPDATE ${PhotoContract.TABLE_NAME} SET ${PhotoContract.Columns.LIKED_BY_USER} = 0, ${PhotoContract.Columns.LIKES} = :likes WHERE ${PhotoContract.Columns.PHOTO_ID} = :photoId")
    fun updateUnlikePhoto(photoId: String, likes: String)


}