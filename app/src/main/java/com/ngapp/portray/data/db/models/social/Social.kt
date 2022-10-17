package com.ngapp.portray.data.db.models.social


import com.google.gson.annotations.SerializedName

private val contract = SocialContract.Columns


data class Social(

    @SerializedName(contract.INSTAGRAM_USERNAME)
    val instagramUsername: String?,

    @SerializedName(contract.PORTFOLIO_URL)
    val portfolioUrl: String?,

    @SerializedName(contract.TWITTER_USERNAME)
    val twitterUsername: String?
)

