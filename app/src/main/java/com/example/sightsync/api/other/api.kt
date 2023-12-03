package com.example.sightsync.api.other

import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object ApiRetrofitClient {
    private const val BASE_URL = "https://k3bjexxnf0ndvt-8000.proxy.runpod.net/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
    }
}

