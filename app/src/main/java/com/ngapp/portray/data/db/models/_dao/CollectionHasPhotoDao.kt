package com.ngapp.portray.data.db.models._dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.ngapp.portray.data.db.models.collection_has_photo.CollectionHasPhoto

@Dao
interface CollectionHasPhotoDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectionHasPhoto(collectionHasPhoto: List<CollectionHasPhoto>)
}