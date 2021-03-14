package com.freedomrun.finish.running.model.api

import com.freedomrun.finish.running.model.MyPlaces
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
    @GET
    fun getNearByPlaces(@Url url: String): Call<MyPlaces>
}