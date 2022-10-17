package com.ngapp.portray.data.db.models.collection

object CollectionContract {

    const val TABLE_NAME = "collections"

    object Columns {
        const val COLLECTION_ID = "collection_id"
        const val MOD_DATE = "mod_date"
        const val TITLE = "title"
        const val DESCRIPTION = "description"
        const val PUBLISHED_AT = "published_at"
        const val LAST_COLLECTED_AT = "last_collected_at"
        const val UPDATED_AT = "updated_at"
        const val TOTAL_PHOTOS = "total_photos"
        const val PRIVATE = "private"
        const val SHARE_KEY = "share_key"
        const val COVER_PHOTO = "cover_photo"
        const val USER = "user"
        const val LINKS = "links"
    }
}