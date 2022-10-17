package com.ngapp.portray.data.db.models.location

import androidx.room.Embedded
import com.google.gson.annotations.SerializedName
import com.ngapp.portray.data.db.models.location.position.Position

private val contract = LocationContract.Columns

data class Location(

    @SerializedName(contract.CITY)
    val city: String?,

    @SerializedName(contract.COUNTRY)
    val country: String?,

    @SerializedName(contract.POSITION)
    @Embedded
    val position: Position?
)