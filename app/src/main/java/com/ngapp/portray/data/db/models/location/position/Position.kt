package com.ngapp.portray.data.db.models.location.position

import com.google.gson.annotations.SerializedName

private val contract = PositionContract.Columns

data class Position(

    @SerializedName(contract.LATITUDE)
    val latitude: String?,

    @SerializedName(contract.LONGITUDE)
    val longitude: String?
)