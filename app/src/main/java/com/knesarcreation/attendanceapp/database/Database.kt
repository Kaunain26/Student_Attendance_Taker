package com.knesarcreation.attendanceapp.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [AttendanceSheet::class,
        StudentPastAttendance::class,
        StudentDetails::class,
        StudentListClass::class,
        AttendanceDateTimes::class,
        AttendanceHistory::class],
    version = 1,
    exportSchema = false
)
abstract class Database : RoomDatabase() {
    abstract fun mDao(): Dao
}