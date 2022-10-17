package com.ngapp.portray.data.db.models.unsplash_search_response

import com.google.gson.annotations.SerializedName
import com.ngapp.portray.data.db.models.collection.Collection
import com.ngapp.portray.data.db.models.photo.Photo

data class UnsplashSearchPhotoResponse(
    @SerializedName("results")
    val result: List<Photo>
)

data class UnsplashSearchCollectionResponse(
    @SerializedName("results")
    val result: List<Collection>
)
