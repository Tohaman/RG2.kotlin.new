package ru.tohaman.rg2.dbase

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.tohaman.rg2.DebugTag.TAG
import timber.log.Timber

val MIGRATION_1_4 = object : Migration(1, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE timeTable (uuid INTEGER NOT NULL, currentTime TEXT NOT NULL, dateOfNote TEXT NOT NULL, timeComment TEXT NOT NULL, scramble TEXT NOT NULL, PRIMARY KEY(uuid))")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //Nothing
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        Timber.d("$TAG .migrate 3_4 database version = [${database.version}]")
        // Create the new table
        database.execSQL("CREATE TABLE base_new (phase TEXT NOT NULL, id INTEGER NOT NULL, comment TEXT NOT NULL, subId INTEGER NOT NULL, PRIMARY KEY(phase, id))")
        // Copy the data
        database.execSQL("INSERT INTO base_new (phase, id, comment, subId) SELECT phase, id, comment, subId FROM " +
                "(select distinct phase, CAST (id as INT) id, comment, 0 as subId from baseTable)")
        // Remove the old table
        database.execSQL("DROP TABLE baseTable")
        // Change the table name to the correct one
        database.execSQL("ALTER TABLE base_new RENAME TO baseTable")

        Timber.d("$TAG .migrate baseTable complete")
        // Create the new table
        database.execSQL("CREATE TABLE time_new (uuid INTEGER NOT NULL, currentTime TEXT NOT NULL, dateOfNote TEXT NOT NULL, timeComment TEXT NOT NULL, scramble TEXT NOT NULL, PRIMARY KEY(uuid))")
        // Copy the data
        database.execSQL("INSERT INTO time_new (uuid, currentTime, dateOfNote, timeComment, scramble) SELECT CAST (uuid as INT) , currentTime, dateOfNote, timeComment, scramble FROM timeTable")
        // Remove the old table
        database.execSQL("DROP TABLE timeTable")
        // Change the table name to the correct one
        database.execSQL("ALTER TABLE time_new RENAME TO timeTable")

        Timber.d("$TAG .migrate 3_4 complete")
    }
}

//Миграции для базы Room

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE 'phasePositions' ('phase' TEXT NOT NULL, 'position' INTEGER NOT NULL, PRIMARY KEY(phase))")
        Timber.d("$TAG .migrate 6_7 complete")
    }
}

