package com.freedomrun.features.main

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Running::class], version = 1, exportSchema = false)
abstract class RunDatabase : RoomDatabase() {
    abstract fun runDAO(): RunDao?
}