package com.ngapp.portray.data.db.models._dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ngapp.portray.data.db.models.collection_remote_keys.CollectionRemoteKeys
import com.ngapp.portray.data.db.models.collection_remote_keys.CollectionRemoteKeysContract

@Dao
interface CollectionRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photoRemoteKeys: List<CollectionRemoteKeys>)

    @Query("SELECT * FROM ${CollectionRemoteKeysContract.TABLE_NAME} WHERE ${CollectionRemoteKeysContract.Columns.collectionId} = :collectionId")
    suspend fun remoteKeysCollectionId(collectionId: String): CollectionRemoteKeys?

    @Query("DELETE FROM ${CollectionRemoteKeysContract.TABLE_NAME}")
    suspend fun clearRemoteKeys()
}