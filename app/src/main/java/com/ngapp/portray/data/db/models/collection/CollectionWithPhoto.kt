package com.ngapp.portray.data.db.models.collection

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.ngapp.portray.data.db.models.collection_has_photo.CollectionHasPhoto
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.data.db.models.photo.PhotoContract

data class CollectionWithPhoto(
    @Embedded
    val collection: Collection,
    @Relation(
        parentColumn = CollectionContract.Columns.COLLECTION_ID,
        entityColumn = PhotoContract.Columns.PHOTO_ID,
        associateBy = Junction(CollectionHasPhoto::class)
    )
    val photo: Photo
)
