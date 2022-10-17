package com.ngapp.portray.data.db.models.urls

import com.google.gson.annotations.SerializedName

private val contract = UrlsContract.Columns

data class Urls(

    @SerializedName(contract.RAW)
    val raw: String?,

    @SerializedName(contract.FULL)
    val full: String?,

    @SerializedName(contract.REGULAR)
    val regular: String?,

    @SerializedName(contract.SMALL)
    val small: String?,

    @SerializedName(contract.THUMB)
    val thumb: String?
)