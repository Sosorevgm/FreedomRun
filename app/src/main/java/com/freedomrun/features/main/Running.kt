package com.freedomrun.features.main

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Running(
    @field:PrimaryKey var id: String,
    @field:ColumnInfo(name = "city_name") var cityName: String,
    @field:ColumnInfo(name = "start_lat") var startLat: Float,
    @field:ColumnInfo(name = "start_long") var startLong: Float,
    @field:ColumnInfo(name = "finish_lat") var finishLat: Float,
    @field:ColumnInfo(name = "finish_long") var finishLong: Float,
    @field:ColumnInfo(name = "current_lat") var currentLat: Float,
    @field:ColumnInfo(name = "current_long") var currentLong: Float,
)