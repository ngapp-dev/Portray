package com.ngapp.portray.data.db.models.collection_remote_keys

object CollectionRemoteKeysContract {

    const val TABLE_NAME = "collection_remote_keys"

    object Columns {

        const val collectionId = "collection_id"
        const val prevKey = "prev_key"
        const val nextKey = "next_key"

    }
}