package com.ngapp.portray.data.db.models.user

object UserContract {

    const val TABLE_NAME = "user"

    object Columns {
        const val ID = "id"
        const val LOGGED_USER = "logged_user"
        const val MOD_DATE = "mod_date"
        const val UPDATED_AT = "updated_at"
        const val USERNAME = "username"
        const val NAME = "name"
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"
        const val TWITTER_USERNAME = "twitter_username"
        const val PORTFOLIO_URL = "portfolio_url"
        const val BIO = "bio"
        const val LOCATION = "location"
        const val TOTAL_LIKES = "total_likes"
        const val TOTAL_PHOTOS = "total_photos"
        const val TOTAL_COLLECTIONS = "total_collections"
        const val FOLLOWED_BY_USER = "followed_by_user"
        const val FOLLOWERS_COUNT = "followers_count"
        const val FOLLOWING_COUNT = "following_count"
        const val DOWNLOADS = "downloads"
        const val SOCIAL = "social"
        const val PROFILE_IMAGE = "profile_image"
        const val BADGE = "badge"
        const val UPLOAD_REMAINING = "upload_remaining"
        const val INSTAGRAM_USERNAME = "instagram_username"
        const val EMAIL = "email"
        const val LINKS = "links"
    }
}