package com.ngapp.portray.data.db.models._dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ngapp.portray.data.db.models.onboading_screen.OnboardingScreen
import com.ngapp.portray.data.db.models.onboading_screen.OnboardingScreenContract

@Dao
interface OnboardingScreenDao {

    @Query("SELECT * FROM ${OnboardingScreenContract.TABLE_NAME}")
    suspend fun getOnboardingScreens(): List<OnboardingScreen>

    @Query("SELECT * FROM ${OnboardingScreenContract.TABLE_NAME} WHERE ${OnboardingScreenContract.Columns.ID}= :id")
    suspend fun getOnboardingScreenById(id: Long): OnboardingScreen

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOnboardingScreens(onboardingScreens: List<OnboardingScreen>)


}