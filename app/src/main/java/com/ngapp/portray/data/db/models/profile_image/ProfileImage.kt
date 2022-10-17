package com.ngapp.portray.data.db.models.profile_image

import com.google.gson.annotations.SerializedName

private val contract = ProfileImageContract.Columns

data class ProfileImage(

    @SerializedName(contract.SMALL)
    val small: String?,

    @SerializedName(contract.MEDIUM)
    val medium: String?,

    @SerializedName(contract.LARGE)
    val large: String?
)


