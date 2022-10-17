package com.ngapp.portray.data.db.models.photo.downloadUrl

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

private val contract = DownloadUrlContract.Columns

@Entity(tableName = DownloadUrlContract.TABLE_NAME)
data class DownloadUrl(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = contract.ID)
    val id: String?,

    @SerializedName(contract.DOWNLOAD_URL)
    @ColumnInfo(name = contract.DOWNLOAD_URL)
    val downloadUrl: String?
)