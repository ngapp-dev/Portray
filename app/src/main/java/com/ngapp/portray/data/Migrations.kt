package com.ngapp.portray.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import timber.log.Timber

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Timber.d("migration 1-2 start")
        database.execSQL("ALTER TABLE user ADD COLUMN permission INTEGER NOT NULL DEFAULT 1")
        Timber.d("migration 1-2 success")
    }

}