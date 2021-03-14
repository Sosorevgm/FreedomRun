package com.freedomrun.features.main

import android.app.Application
import androidx.room.Room

class App : Application() {
    lateinit var database: RunDatabase

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = Room.databaseBuilder(this, RunDatabase::class.java, "run_database")
            .build()
    }

    companion object {
        @JvmStatic
        lateinit var instance: App
    }
}