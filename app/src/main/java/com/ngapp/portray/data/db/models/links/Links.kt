package com.ngapp.portray.data.db.models.links

import com.google.gson.annotations.SerializedName

private val contract = LinksContract.Columns


data class Links(

    @SerializedName(contract.SELF)
    val self: String?,

    @SerializedName(contract.HTML)
    val html: String?,

    @SerializedName(contract.DOWNLOAD)
    val download: String?,

    @SerializedName(contract.DOWNLOAD_LOCATION)
    val downloadLocation: String?,

    @SerializedName(contract.PHOTOS)
    val photos: String?,

    @SerializedName(contract.LIKES)
    val linksLikes: String?,

    @SerializedName(contract.PORTFOLIO)
    val portfolio: String?
)
