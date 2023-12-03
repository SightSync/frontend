package com.example.sightsync.api.cog

import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object CogApiRetrofitClient {
    private const val BASE_URL = "https://2o7hh2v9oqo4l2-8000.proxy.runpod.net/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
    }
}

