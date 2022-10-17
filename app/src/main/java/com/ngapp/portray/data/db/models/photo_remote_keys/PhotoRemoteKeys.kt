package com.ngapp.portray.data.db.models.photo_remote_keys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ngapp.portray.data.db.models.collection_remote_keys.CollectionRemoteKeysContract

private val contract = PhotoRemoteKeysContract.Columns

@Entity(tableName = PhotoRemoteKeysContract.TABLE_NAME)
data class PhotoRemoteKeys(

    @PrimaryKey
    @ColumnInfo(name = contract.photoId)
    val photoId: String,

    @ColumnInfo(name = contract.prevKey)
    val prevKey: Int?,

    @ColumnInfo(name = contract.nextKey)
    val nextKey: Int?
)
