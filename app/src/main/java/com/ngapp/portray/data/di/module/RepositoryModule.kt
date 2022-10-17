package com.ngapp.portray.data.di.module

import com.ngapp.portray.data.di.Repository
import com.ngapp.portray.data.repository.CollectionRepositoryImpl
import com.ngapp.portray.data.repository.PhotoRepositoryImpl
import com.ngapp.portray.data.repository.ProfileRepositoryImpl
import com.ngapp.portray.ui.onboarding.OnboardingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun providesOnboardingRepository(impl: OnboardingRepository): Repository

    @Binds
    abstract fun providesCollectionRepository(impl: CollectionRepositoryImpl): Repository

    @Binds
    abstract fun providesPhotoRepository(impl: PhotoRepositoryImpl): Repository

    @Binds
    abstract fun providesProfileRepository(impl: ProfileRepositoryImpl): Repository
}