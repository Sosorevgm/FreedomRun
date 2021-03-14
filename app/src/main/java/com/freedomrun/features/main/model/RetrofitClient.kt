package com.freedomrun.features.main.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null
    fun getClient(bseUrl: String): Retrofit {
        if(retrofit == null)
        {
            retrofit = Retrofit.Builder()
                    .baseUrl(bseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        return retrofit!!
    }
}