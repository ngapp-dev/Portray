package com.ngapp.portray.data.db.models.photo.exif

import com.google.gson.annotations.SerializedName

private val contract = ExifContract.Columns

data class Exif(

    @SerializedName(contract.MAKE)
    val make: String?,

    @SerializedName(contract.MODEL)
    val model: String?,

    @SerializedName(contract.NAME)
    val name: String?,

    @SerializedName(contract.EXPOSURE_TIME)
    val exposureTime: String?,

    @SerializedName(contract.APERTURE)
    val aperture: String?,

    @SerializedName(contract.FOCAL_LENGTH)
    val focalLength: String?,

    @SerializedName(contract.ISO)
    val iso: String?
)