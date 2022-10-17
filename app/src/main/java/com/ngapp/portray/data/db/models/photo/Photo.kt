package com.ngapp.portray.data.db.models.photo

import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.ngapp.portray.data.db.models.links.Links
import com.ngapp.portray.data.db.models.location.Location
import com.ngapp.portray.data.db.models.photo.exif.Exif
import com.ngapp.portray.data.db.models.photo.tags.Tags
import com.ngapp.portray.data.db.models.urls.Urls
import com.ngapp.portray.data.db.models.user.User

private val contract = PhotoContract.Columns

@Entity(
    tableName = PhotoContract.TABLE_NAME,
    indices = [
        Index(value = [PhotoContract.Columns.MOD_DATE])
    ]
)
data class Photo(

    @PrimaryKey
    @SerializedName("id")
    @ColumnInfo(name = contract.PHOTO_ID)
    val id: String,

    @ColumnInfo(name = contract.MOD_DATE)
    var modDate: Long?,

    @ColumnInfo(name = contract.COLLECTION_ID)
    val collectionId: String?,

    @SerializedName(contract.CREATED_AT)
    @ColumnInfo(name = contract.CREATED_AT)
    val createdAt: String,

    @SerializedName(contract.UPDATED_AT)
    @ColumnInfo(name = contract.UPDATED_AT)
    val updatedAt: String,

    @SerializedName(contract.WIDTH)
    @ColumnInfo(name = contract.WIDTH)
    val width: Int,

    @SerializedName(contract.HEIGHT)
    @ColumnInfo(name = contract.HEIGHT)
    val height: Int,

    @SerializedName(contract.COLOR)
    @ColumnInfo(name = contract.COLOR)
    val color: String?,

    @SerializedName(contract.BLUR_HASH)
    @ColumnInfo(name = contract.BLUR_HASH)
    val blurHash: String?,

    @SerializedName(contract.DOWNLOADS)
    @ColumnInfo(name = contract.DOWNLOADS)
    val downloads: Int?,

    @SerializedName(contract.LIKES)
    @ColumnInfo(name = contract.LIKES)
    val likes: String,

    @SerializedName(contract.LIKED_BY_USER)
    @ColumnInfo(name = contract.LIKED_BY_USER)
    val likedByUser: Boolean,

    @SerializedName(contract.PUBLIC_DOMAIN)
    @ColumnInfo(name = contract.PUBLIC_DOMAIN)
    val public_domain: Boolean,

    @SerializedName(contract.DESCRIPTION)
    @ColumnInfo(name = contract.DESCRIPTION)
    val description: String?,

    @SerializedName(contract.EXIF)
    @Embedded
    val exif: Exif?,

    @SerializedName(contract.LOCATION)
    @Embedded
    val location: Location?,

    @SerializedName(contract.TAGS)
    @ColumnInfo(name = contract.TAGS)
    val tags: List<Tags>?,

//    @SerializedName(contract.COLLECTIONS)
//    @Embedded(prefix = "collection_")
//    val collections: ListCollection?,

    @SerializedName(contract.URLS)
    @Embedded
    val urls: Urls,

    @SerializedName(contract.LINKS)
    @Embedded
    val links: Links,

    @SerializedName(contract.USER)
    @Embedded(prefix = "user_")
    val user: User
)

//data class ListCollection(
//    val listCollection: List<Collection>
//)
//
data class ListTags(
    val listTags: List<Tags?>?
)
