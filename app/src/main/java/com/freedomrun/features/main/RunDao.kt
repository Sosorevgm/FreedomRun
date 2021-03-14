package com.freedomrun.features.main

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable

interface RunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRunning(running : Running): Completable?

    @Query("SELECT * FROM Running")
    fun getAllRunnings(): List<Running?>?
}