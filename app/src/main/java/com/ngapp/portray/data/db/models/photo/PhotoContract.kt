package com.ngapp.portray.data.db.models.photo

object PhotoContract {

    const val TABLE_NAME = "photo"

    object Columns {
        const val PHOTO_ID = "photo_id"
        const val MOD_DATE = "mod_date"
        const val COLLECTION_ID = "collection_id"
        const val CREATED_AT = "created_at"
        const val UPDATED_AT = "updated_at"
        const val WIDTH = "width"
        const val HEIGHT = "height"
        const val COLOR = "color"
        const val BLUR_HASH = "blur_hash"
        const val DOWNLOADS = "downloads"
        const val LIKES = "likes"
        const val LIKED_BY_USER = "liked_by_user"
        const val PUBLIC_DOMAIN = "public_domain"
        const val DESCRIPTION = "description"
        const val EXIF = "exif"
        const val LOCATION = "location"
        const val TAGS = "tags"
        const val USER = "user"
        const val COLLECTIONS = "current_user_collections"
        const val URLS = "urls"
        const val LINKS = "links"
    }
}