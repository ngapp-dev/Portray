package com.ngapp.portray.data.db.models._dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ngapp.portray.data.db.models.photo_remote_keys.PhotoRemoteKeys
import com.ngapp.portray.data.db.models.photo_remote_keys.PhotoRemoteKeysContract

@Dao
interface PhotoRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photoRemoteKeys: List<PhotoRemoteKeys>)

    @Query("SELECT * FROM ${PhotoRemoteKeysContract.TABLE_NAME} WHERE ${PhotoRemoteKeysContract.Columns.photoId} = :photoId")
    suspend fun remoteKeysPhotoId(photoId: String): PhotoRemoteKeys?

    @Query("DELETE FROM ${PhotoRemoteKeysContract.TABLE_NAME}")
    suspend fun clearRemoteKeys()
}