package ru.tohaman.rg2.dbase

import androidx.room.TypeConverter
import java.util.*

class LocalDateConverters {
    @TypeConverter fun calendarToDatestamp(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter fun datestampToCalendar(value: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = value }
}