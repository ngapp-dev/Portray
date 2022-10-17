package com.ngapp.portray.data.db.models.collection

import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.ngapp.portray.data.db.models.links.Links
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.data.db.models.photo.PhotoContract
import com.ngapp.portray.data.db.models.user.User

private val contract = CollectionContract.Columns

@Entity(
    tableName = CollectionContract.TABLE_NAME,
    indices = [
        Index(value = [CollectionContract.Columns.MOD_DATE])
    ]
)
data class Collection(

    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = contract.COLLECTION_ID)
    val id: String,

    @SerializedName(contract.TITLE)
    @ColumnInfo(name = contract.TITLE)
    val title: String?,

    @ColumnInfo(name = contract.MOD_DATE)
    var modDate: Long?,

    @SerializedName(contract.DESCRIPTION)
    @ColumnInfo(name = contract.DESCRIPTION)
    val description: String?,

    @SerializedName(contract.PUBLISHED_AT)
    @ColumnInfo(name = contract.PUBLISHED_AT)
    val publishedAt: String,

    @SerializedName(contract.LAST_COLLECTED_AT)
    @ColumnInfo(name = contract.LAST_COLLECTED_AT)
    val lastCollectedAt: String,

    @SerializedName(contract.UPDATED_AT)
    @ColumnInfo(name = contract.UPDATED_AT)
    val updatedAt: String,

    @SerializedName(contract.TOTAL_PHOTOS)
    @ColumnInfo(name = contract.TOTAL_PHOTOS)
    val totalPhotos: Int,

    @SerializedName(contract.PRIVATE)
    @ColumnInfo(name = contract.PRIVATE)
    val isPrivate: String?,

    @SerializedName(contract.SHARE_KEY)
    @ColumnInfo(name = contract.SHARE_KEY)
    val shareKey: String?,

    @SerializedName(contract.COVER_PHOTO)
    @Embedded(prefix = "cover_")
    val coverPhoto: Photo?,

    @SerializedName(contract.USER)
    @Embedded(prefix = "user_")
    val user: User?,

    @SerializedName(contract.LINKS)
    @Embedded
    val links: Links
)