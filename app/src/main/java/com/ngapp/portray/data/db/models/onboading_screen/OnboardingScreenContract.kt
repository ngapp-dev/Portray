package com.ngapp.portray.data.db.models.onboading_screen

object OnboardingScreenContract {

    const val TABLE_NAME = "onboarding_screen"

    object Columns {
        const val ID = "id"
        const val CREATED_AT = "created_at"
        const val UPDATED_AT = "updated_at"
        const val POSITION = "position"
        const val TITLE = "title"
        const val DESCRIPTION_EN = "description_en"
        const val DESCRIPTION_RU = "description_ru"
        const val IMAGE = "image"
    }
}