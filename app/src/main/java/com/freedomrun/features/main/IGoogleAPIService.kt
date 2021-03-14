package com.freedomrun.features.main

import com.freedomrun.features.main.model.Data
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface IGoogleAPIService {
    @GET
    fun getNearbyPlaces(@Url url: String): Call<Data>
}