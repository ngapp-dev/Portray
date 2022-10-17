package com.ngapp.portray.data.db.models._dao

import androidx.paging.PagingSource
import androidx.room.*
import com.ngapp.portray.data.db.models.collection.Collection
import com.ngapp.portray.data.db.models.collection.CollectionContract
import com.ngapp.portray.data.db.models.collection.CollectionWithPhoto
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.data.db.models.photo.PhotoContract

@Dao
interface CollectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCollections(collections: List<Collection>)

    @Query("SELECT * FROM ${CollectionContract.TABLE_NAME}")
    fun getAllCollections(): PagingSource<Int, Collection>

    @Query("DELETE FROM ${CollectionContract.TABLE_NAME}")
    suspend fun clearCollections()

    @Query("SELECT * FROM ${CollectionContract.TABLE_NAME} WHERE ${CollectionContract.Columns.COLLECTION_ID} = :collectionId")
    suspend fun getCollectionById(collectionId: String): Collection

//    @Query("SELECT * FROM ${PhotoContract.TABLE_NAME} WHERE ${PhotoContract.Columns.COLLECTIONS} = :collectionId")
//    fun getPhotoListByCollectionId(collectionId: String): PagingSource<Int, Photo>

    @Transaction
    @Query("SELECT * FROM ${PhotoContract.TABLE_NAME} WHERE ${PhotoContract.Columns.COLLECTION_ID} = :collectionId")
    fun getCollectionWithPhotos(collectionId: String): PagingSource<Int, Photo>

    @Query("SELECT MAX(${CollectionContract.Columns.MOD_DATE}) from ${CollectionContract.TABLE_NAME}")
    suspend fun lastUpdated(): Long?

    @Query("SELECT * FROM ${CollectionContract.TABLE_NAME} WHERE user_username = :username")
    fun getUserCollections(username: String): PagingSource<Int, Collection>
}