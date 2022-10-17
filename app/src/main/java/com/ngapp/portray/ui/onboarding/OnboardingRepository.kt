package com.ngapp.portray.ui.onboarding

import com.ngapp.portray.R
import com.ngapp.portray.data.db.models._dao.OnboardingScreenDao
import com.ngapp.portray.data.db.models.onboading_screen.OnboardingScreen
import com.ngapp.portray.data.di.Repository
import javax.inject.Inject

class OnboardingRepository @Inject constructor(
    private val onboardingScreenDao: OnboardingScreenDao
) : Repository {

    suspend fun getElementList(): List<OnboardingScreen> {
        return onboardingScreenDao.getOnboardingScreens()
    }

    suspend fun getElementById(id: Long): OnboardingScreen {
        return onboardingScreenDao.getOnboardingScreenById(id)
    }

    private suspend fun insertElements(element: List<OnboardingScreen>) {
        onboardingScreenDao.insertOnboardingScreens(element)
    }

    suspend fun createBasicOnboardScreens() {
        val onboardingScreens = listOf(
            OnboardingScreen(
                id = 1,
                createdAt = System.currentTimeMillis().toString(),
                updatedAt = System.currentTimeMillis().toString(),
                position = 1,
                title = "Create",
                descriptionEn = "Take shots, post, build an audience, get feedback!",
                descriptionRu = "Создавайте снимки, публикуйте, собирайте аудиторию, получайте фидбек!",
                image = R.drawable.asset_1
            ),
            OnboardingScreen(
                id = 2,
                createdAt = System.currentTimeMillis().toString(),
                updatedAt = System.currentTimeMillis().toString(),
                position = 2,
                title = "Share",
                descriptionEn = "Share with friends, make collections",
                descriptionRu = "Делитесь с друзьями, собирайте коллекции",
                image = R.drawable.asset_2
            ),
            OnboardingScreen(
                id = 3,
                createdAt = System.currentTimeMillis().toString(),
                updatedAt = System.currentTimeMillis().toString(),
                position = 3,
                title = "Upload",
                descriptionEn = "Upload your favorite shots and track statistics",
                descriptionRu = "Загружайте любимые снимки и отслеживайте статистику",
                image = R.drawable.asset_3
            )
        )
        insertElements(onboardingScreens)
    }
}