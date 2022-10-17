package com.ngapp.portray.data.db.models._dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ngapp.portray.data.db.models.user_photo_remote_keys.UserPhotoRemoteKeys
import com.ngapp.portray.data.db.models.user_photo_remote_keys.UserPhotoRemoteKeysContract

@Dao
interface UserPhotoRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(userPhotoRemoteKeys: List<UserPhotoRemoteKeys>)

    @Query("SELECT * FROM ${UserPhotoRemoteKeysContract.TABLE_NAME} WHERE ${UserPhotoRemoteKeysContract.Columns.photoId} = :photoId")
    suspend fun userRemoteKeysPhotoId(photoId: String): UserPhotoRemoteKeys?

    @Query("DELETE FROM ${UserPhotoRemoteKeysContract.TABLE_NAME}")
    suspend fun clearRemoteKeys()
}