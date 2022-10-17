package com.ngapp.portray.data.db.models.photo.exif

object ExifContract {

    const val TABLE_NAME = "exif"

    object Columns {
        const val ID = "id"
        const val MAKE = "make"
        const val MODEL = "model"
        const val NAME = "name"
        const val EXPOSURE_TIME = "exposure_time"
        const val APERTURE = "aperture"
        const val FOCAL_LENGTH = "focal_length"
        const val ISO = "iso"
    }
}