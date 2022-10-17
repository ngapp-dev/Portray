package com.ngapp.portray.data.di.module

import android.app.Application
import androidx.room.Room
import com.ngapp.portray.data.db.PortrayDatabase
import com.ngapp.portray.data.db.models._dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun providesDatabase(application: Application): PortrayDatabase {
        return Room.databaseBuilder(
            application,
            PortrayDatabase::class.java,
            PortrayDatabase.DB_NAME
        )
            .build()
    }

    @Provides
    fun providesBadgeDao(db: PortrayDatabase): BadgeDao {
        return db.badgeDao()
    }

    @Provides
    fun providesCollectionDao(db: PortrayDatabase): CollectionDao {
        return db.collectionDao()
    }

    @Provides
    fun providesCollectionHasPhotoDao(db: PortrayDatabase): CollectionHasPhotoDao {
        return db.collectionHasPhotoDao()
    }

    @Provides
    fun providesLinksDao(db: PortrayDatabase): LinksDao {
        return db.linksDao()
    }

    @Provides
    fun providesLocationDao(db: PortrayDatabase): LocationDao {
        return db.locationDao()
    }

    @Provides
    fun providesPositionDao(db: PortrayDatabase): PositionDao {
        return db.positionDao()
    }

    @Provides
    fun providesOnboardingScreenDao(db: PortrayDatabase): OnboardingScreenDao {
        return db.onboardingScreenDao()
    }

    @Provides
    fun providesPhotoDao(db: PortrayDatabase): PhotoDao {
        return db.photoDao()
    }

    @Provides
    fun providesExifDao(db: PortrayDatabase): ExifDao {
        return db.exifDao()
    }

    @Provides
    fun providesTagsDao(db: PortrayDatabase): TagsDao {
        return db.tagsDao()
    }

    @Provides
    fun providesProfileImageDao(db: PortrayDatabase): ProfileImageDao {
        return db.profileImageDao()
    }

    @Provides
    fun providesSocialDao(db: PortrayDatabase): SocialDao {
        return db.socialDao()
    }

    @Provides
    fun providesUrlsDao(db: PortrayDatabase): UrlsDao {
        return db.urlsDao()
    }

    @Provides
    fun providesUserCurrentDao(db: PortrayDatabase): UserDao {
        return db.userDao()
    }

    @Provides
    fun providesLikedPhotosDao(db: PortrayDatabase): UserLikedPhotosDao {
        return db.userLikedPhotosDao()
    }

    @Provides
    fun providesUserPublicDao(db: PortrayDatabase): UserPublicDao {
        return db.userPublicDao()
    }
}