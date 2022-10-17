package com.ngapp.portray.data.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.GsonBuilder
import com.ngapp.portray.data.Api
import com.ngapp.portray.data.network.AuthorizationFailedInterceptor
import com.ngapp.portray.data.network.AuthorizationInterceptor
import com.ngapp.portray.utils.haveM
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.openid.appauth.AuthorizationService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkingModule {

    private const val BASE_URL = "https://api.unsplash.com/"

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class LoggingInterceptorForOkHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthorizationInterceptorForOkHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthorizationFailedInterceptorForOkHttpClient

    @Singleton
    @Provides
    fun provideMasterKeyAlias(application: Application): MasterKey {
        return MasterKey.Builder(application, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

    }

    @Singleton
    @Provides
    fun provideEncryptedSharedPreferences(
        application: Application,
        masterKeyAlias: MasterKey
    ): EncryptedSharedPreferences {
        return EncryptedSharedPreferences.create(
            application,
            "access_token_secured",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }

    @Singleton
    @Provides
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("access_token", Context.MODE_PRIVATE)
    }


    @LoggingInterceptorForOkHttpClient
    @Provides
    fun provideHttpLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @AuthorizationInterceptorForOkHttpClient
    @Provides
    fun provideAuthorizationInterceptor(
        encryptedSharedPreferences: EncryptedSharedPreferences,
        sharedPreferences: SharedPreferences
    ): Interceptor {
        return if (haveM()) {
            AuthorizationInterceptor(encryptedSharedPreferences, null)
        } else {
            AuthorizationInterceptor(null, sharedPreferences)
        }
    }

    @AuthorizationFailedInterceptorForOkHttpClient
    @Provides
    fun provideAuthorizationFailedInterceptor(
        encryptedSharedPreferences: EncryptedSharedPreferences,
        sharedPreferences: SharedPreferences,
        @ApplicationContext context: Context,
    ): Interceptor {
        return if (haveM()) {
            AuthorizationFailedInterceptor(
                AuthorizationService(context),
                encryptedSharedPreferences,
                null
            )
        } else {
            AuthorizationFailedInterceptor(
                AuthorizationService(context),
                null,
                sharedPreferences
            )
        }
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(
        @LoggingInterceptorForOkHttpClient httpLoggingInterceptor: Interceptor,
        @AuthorizationInterceptorForOkHttpClient authorizationInterceptor: Interceptor,
        @AuthorizationFailedInterceptorForOkHttpClient authorizationFailedInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(authorizationInterceptor)
            .addInterceptor(authorizationFailedInterceptor)
            .build()
    }


    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): Api {
        return retrofit.create(Api::class.java)
    }

}