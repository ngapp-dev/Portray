package com.ngapp.portray.data.db.models.collection_remote_keys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

private val contract = CollectionRemoteKeysContract.Columns

@Entity(tableName = CollectionRemoteKeysContract.TABLE_NAME)
data class CollectionRemoteKeys(

    @PrimaryKey
    @ColumnInfo(name = contract.collectionId)
    val collectionId: String,

    @ColumnInfo(name = CollectionRemoteKeysContract.Columns.prevKey)
    val prevKey: Int?,

    @ColumnInfo(name = CollectionRemoteKeysContract.Columns.nextKey)
    val nextKey: Int?
)
