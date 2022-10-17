package com.ngapp.portray.data.db.models.user_photo_remote_keys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ngapp.portray.data.db.models.photo_remote_keys.PhotoRemoteKeysContract

private val contract = UserPhotoRemoteKeysContract.Columns

@Entity(tableName = UserPhotoRemoteKeysContract.TABLE_NAME)
data class UserPhotoRemoteKeys(

    @PrimaryKey
    @ColumnInfo(name = contract.photoId)
    val photoId: String,

    @ColumnInfo(name = contract.prevKey)
    val prevKey: Int?,

    @ColumnInfo(name = contract.nextKey)
    val nextKey: Int?
)
