package com.ngapp.portray.data.db.models.collection_has_photo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.ngapp.portray.data.db.models.collection.Collection
import com.ngapp.portray.data.db.models.collection.CollectionContract
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.data.db.models.photo.PhotoContract

@Entity(
    tableName = CollectionHasPhotoContract.TABLE_NAME,
    primaryKeys = [CollectionHasPhotoContract.Columns.COLLECTION_ID, CollectionHasPhotoContract.Columns.PHOTO_ID],
    foreignKeys = [
        ForeignKey(
            entity = Collection::class,
            parentColumns = [CollectionContract.Columns.COLLECTION_ID],
            childColumns = [CollectionHasPhotoContract.Columns.COLLECTION_ID]
        ),
        ForeignKey(
            entity = Photo::class,
            parentColumns = [PhotoContract.Columns.PHOTO_ID],
            childColumns = [CollectionHasPhotoContract.Columns.PHOTO_ID]
        )
    ]
)
data class CollectionHasPhoto(
    @ColumnInfo(name = CollectionHasPhotoContract.Columns.COLLECTION_ID)
    val collectionId: String,

    @ColumnInfo(name = CollectionHasPhotoContract.Columns.PHOTO_ID)
    val photoId: String
)
