package com.freedomrun.features.main.common

import com.freedomrun.features.main.IGoogleAPIService
import com.freedomrun.features.main.model.RetrofitClient

object common {
    private val GOOGLE_API_URL = "https://maps.googleapis.com/"
    val googleApiService: IGoogleAPIService
        get() = RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService::class.java)

}