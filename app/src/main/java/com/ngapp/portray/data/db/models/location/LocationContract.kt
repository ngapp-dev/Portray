package com.ngapp.portray.data.db.models.location

object LocationContract {

    const val TABLE_NAME = "location"

    object Columns {
        const val ID = "id"
        const val CITY = "raw"
        const val COUNTRY = "full"
        const val POSITION = "regular"
    }
}