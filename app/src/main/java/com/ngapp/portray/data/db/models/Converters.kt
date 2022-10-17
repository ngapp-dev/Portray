package com.ngapp.portray.data.db.models

import android.provider.MediaStore.Video
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ngapp.portray.data.db.models.collection.Collection
import com.ngapp.portray.data.db.models.photo.tags.Tags
import java.lang.reflect.Type


class Converters() {

    @TypeConverter
    fun fromListCollectionToString(collections: List<Collection?>?): String? {
        val type = object : TypeToken<List<Collection>>() {}.type
        return Gson().toJson(collections, type)
    }

    @TypeConverter
    fun fromStringToListCollection(collectionsString: String?): List<Collection>? {
        val type = object : TypeToken<List<Collection>>() {}.type
        return Gson().fromJson<List<Collection>>(collectionsString, type)
    }

    @TypeConverter
    fun fromTagsListToString(tags: List<Tags?>?): String? {
        if (tags == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<Tags?>?>() {}.type
        return gson.toJson(tags, type)
    }

    @TypeConverter
    fun fromStringToTagsList(tagsString: String?): List<Tags?>? {
        if (tagsString == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<Tags?>?>() {}.type
        return gson.fromJson(tagsString, type)
    }
}
