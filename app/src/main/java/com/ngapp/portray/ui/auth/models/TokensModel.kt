package com.ngapp.portray.ui.auth.models


data class TokensModel(
    val accessToken: String,
    val refreshToken: String,
    val idToken: String
)
