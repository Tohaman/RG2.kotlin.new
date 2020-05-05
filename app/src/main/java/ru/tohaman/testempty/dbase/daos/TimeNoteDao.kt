package ru.tohaman.testempty.dbase.daos

import androidx.databinding.adapters.Converters
import androidx.room.*
import ru.tohaman.testempty.dbase.LocalDateConverters
import ru.tohaman.testempty.dbase.entitys.CubeType
import ru.tohaman.testempty.dbase.entitys.TimeNoteItem

@Dao
interface TimeNoteDao {
    @Query("SELECT * FROM TimesTable ORDER BY UUID desc")
    suspend fun getAllTimeNotes() : List<TimeNoteItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTimeNote(timeNoteItem: TimeNoteItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTimeNote(timeNoteItem: List<TimeNoteItem>)

    @Update
    suspend fun updateTimeNote(timeNoteItem: TimeNoteItem)

    @Update
    suspend fun updateTimeNote(timeNoteItem: List<TimeNoteItem>)

    @Delete
    suspend fun deleteTimeNote(timeNoteItem: TimeNoteItem)

    @Query("DELETE FROM TimesTable")
    suspend fun deleteAllTimeNotes()
}