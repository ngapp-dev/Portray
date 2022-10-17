package com.ngapp.portray.data.db.models.photo_remote_keys

object PhotoRemoteKeysContract {

    const val TABLE_NAME = "photo_remote_keys"

    object Columns {

        const val photoId = "photo_id"
        const val prevKey = "prev_key"
        const val nextKey = "next_key"

    }
}