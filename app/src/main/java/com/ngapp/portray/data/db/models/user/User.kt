package com.ngapp.portray.data.db.models.user

import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.ngapp.portray.data.db.models.badge.Badge
import com.ngapp.portray.data.db.models.links.Links
import com.ngapp.portray.data.db.models.profile_image.ProfileImage
import com.ngapp.portray.data.db.models.social.Social

private val contract = UserContract.Columns

@Entity(
    tableName = UserContract.TABLE_NAME,
    indices = [
        Index(value = [UserContract.Columns.MOD_DATE]),
        Index(value = [UserContract.Columns.LOGGED_USER])
    ]
)
data class User(

    @PrimaryKey
    @ColumnInfo(name = contract.ID)
    val id: String,

    @ColumnInfo(name = contract.LOGGED_USER)
    var loggedUser: Int?,

    @ColumnInfo(name = contract.MOD_DATE)
    val modDate: Long?,

    @SerializedName(contract.UPDATED_AT)
    @ColumnInfo(name = contract.UPDATED_AT)
    val updatedAt: String,

    @SerializedName(contract.USERNAME)
    @ColumnInfo(name = contract.USERNAME)
    val username: String,

    @SerializedName(contract.NAME)
    @ColumnInfo(name = contract.NAME)
    val name: String?,

    @SerializedName(contract.FIRST_NAME)
    @ColumnInfo(name = contract.FIRST_NAME)
    val firstName: String?,

    @SerializedName(contract.LAST_NAME)
    @ColumnInfo(name = contract.LAST_NAME)
    val lastName: String?,

    @SerializedName(contract.TWITTER_USERNAME)
    @ColumnInfo(name = contract.TWITTER_USERNAME)
    val twitterUsername: String?,

    @SerializedName(contract.PORTFOLIO_URL)
    @ColumnInfo(name = contract.PORTFOLIO_URL)
    val portfolioUrl: String?,

    @SerializedName(contract.BIO)
    @ColumnInfo(name = contract.BIO)
    val bio: String?,

    @SerializedName(contract.LOCATION)
    @ColumnInfo(name = contract.LOCATION)
    val location: String?,

    @SerializedName(contract.TOTAL_LIKES)
    @ColumnInfo(name = contract.TOTAL_LIKES)
    val totalLikes: Int,

    @SerializedName(contract.TOTAL_PHOTOS)
    @ColumnInfo(name = contract.TOTAL_PHOTOS)
    val totalPhotos: Int,

    @SerializedName(contract.TOTAL_COLLECTIONS)
    @ColumnInfo(name = contract.TOTAL_COLLECTIONS)
    val totalCollections: Int,

    @SerializedName(contract.FOLLOWED_BY_USER)
    @ColumnInfo(name = contract.FOLLOWED_BY_USER)
    val followedByUser: String?,

    @SerializedName(contract.FOLLOWERS_COUNT)
    @ColumnInfo(name = contract.FOLLOWERS_COUNT)
    val followersCount: String?,

    @SerializedName(contract.FOLLOWING_COUNT)
    @ColumnInfo(name = contract.FOLLOWING_COUNT)
    val followingCount: String?,

    @SerializedName(contract.DOWNLOADS)
    @ColumnInfo(name = contract.DOWNLOADS)
    val downloads: Int?,

    @SerializedName(contract.SOCIAL)
    @Embedded
    val social: Social?,

    @SerializedName(contract.PROFILE_IMAGE)
    @Embedded
    val profileImage: ProfileImage?,

    @SerializedName(contract.BADGE)
    @Embedded
    val badge: Badge?,

    @SerializedName(contract.UPLOAD_REMAINING)
    @ColumnInfo(name = contract.UPLOAD_REMAINING)
    val uploadRemaining: Int?,

    @SerializedName(contract.INSTAGRAM_USERNAME)
    @ColumnInfo(name = contract.INSTAGRAM_USERNAME)
    val instagramUsername: String?,

    @SerializedName(contract.EMAIL)
    @ColumnInfo(name = contract.EMAIL)
    val email: String?,

    @SerializedName(contract.LINKS)
    @Embedded
    val links: Links
)

