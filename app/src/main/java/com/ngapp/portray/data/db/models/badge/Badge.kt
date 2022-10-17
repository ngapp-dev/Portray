package com.ngapp.portray.data.db.models.badge

import com.google.gson.annotations.SerializedName


data class Badge(

    @SerializedName(BadgeContract.Columns.TITLE)
    val title: String?,

    @SerializedName(BadgeContract.Columns.PRIMARY)
    val primary: String?,

    @SerializedName(BadgeContract.Columns.SLUG)
    val slug: String?,

    @SerializedName(BadgeContract.Columns.LINK)
    val link: String?

)

