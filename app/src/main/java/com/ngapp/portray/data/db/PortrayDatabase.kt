package com.ngapp.portray.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ngapp.portray.data.db.PortrayDatabase.Companion.DB_VERSION
import com.ngapp.portray.data.db.models.Converters
import com.ngapp.portray.data.db.models._dao.*
import com.ngapp.portray.data.db.models.collection.Collection
import com.ngapp.portray.data.db.models.collection_has_photo.CollectionHasPhoto
import com.ngapp.portray.data.db.models.collection_remote_keys.CollectionRemoteKeys
import com.ngapp.portray.data.db.models.onboading_screen.OnboardingScreen
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.data.db.models.photo_remote_keys.PhotoRemoteKeys
import com.ngapp.portray.data.db.models.user.User
import com.ngapp.portray.data.db.models.user_photo_remote_keys.UserPhotoRemoteKeys

@Database(
    entities = [
        OnboardingScreen::class,
        Photo::class,
        PhotoRemoteKeys::class,
        Collection::class,
        CollectionRemoteKeys::class,
        CollectionHasPhoto::class,
        User::class,
        UserPhotoRemoteKeys::class
    ],
    version = DB_VERSION
)
@TypeConverters(Converters::class)
abstract class PortrayDatabase : RoomDatabase() {

    abstract fun badgeDao(): BadgeDao
    abstract fun collectionDao(): CollectionDao
    abstract fun collectionHasPhotoDao(): CollectionHasPhotoDao
    abstract fun linksDao(): LinksDao
    abstract fun locationDao(): LocationDao
    abstract fun positionDao(): PositionDao
    abstract fun onboardingScreenDao(): OnboardingScreenDao
    abstract fun photoDao(): PhotoDao
    abstract fun exifDao(): ExifDao
    abstract fun tagsDao(): TagsDao
    abstract fun profileImageDao(): ProfileImageDao
    abstract fun photoRemoteKeysDao(): PhotoRemoteKeysDao
    abstract fun collectionRemoteKeysDao(): CollectionRemoteKeysDao
    abstract fun socialDao(): SocialDao
    abstract fun urlsDao(): UrlsDao
    abstract fun userDao(): UserDao
    abstract fun userPhotoRemoteKeysDao(): UserPhotoRemoteKeysDao
    abstract fun userLikedPhotosDao(): UserLikedPhotosDao
    abstract fun userPublicDao(): UserPublicDao

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "db_portray"
    }
}