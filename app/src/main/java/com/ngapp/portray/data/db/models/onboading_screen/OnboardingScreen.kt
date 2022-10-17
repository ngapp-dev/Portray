package com.ngapp.portray.data.db.models.onboading_screen

import androidx.annotation.DrawableRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = OnboardingScreenContract.TABLE_NAME)
data class OnboardingScreen(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = OnboardingScreenContract.Columns.ID)
    val id: Long,

    @ColumnInfo(name = OnboardingScreenContract.Columns.CREATED_AT)
    val createdAt: String,

    @ColumnInfo(name = OnboardingScreenContract.Columns.UPDATED_AT)
    val updatedAt: String,

    @ColumnInfo(name = OnboardingScreenContract.Columns.POSITION)
    val position: Int,

    @ColumnInfo(name = OnboardingScreenContract.Columns.TITLE)
    val title: String?,

    @ColumnInfo(name = OnboardingScreenContract.Columns.DESCRIPTION_EN)
    val descriptionEn: String?,

    @ColumnInfo(name = OnboardingScreenContract.Columns.DESCRIPTION_RU)
    val descriptionRu: String?,
    
    @ColumnInfo(name = OnboardingScreenContract.Columns.IMAGE)
    @DrawableRes val image: Int?
)
