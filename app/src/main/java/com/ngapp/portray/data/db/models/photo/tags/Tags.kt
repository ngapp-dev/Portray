package com.ngapp.portray.data.db.models.photo.tags

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

private val contract = TagsContract.Columns

data class Tags(
//    @PrimaryKey(autoGenerate = true)
//    @SerializedName(contract.ID)
//    @ColumnInfo(name = "tags_id")
//    val id: Long,

    @SerializedName(contract.TITLE)
    val title: String?
)