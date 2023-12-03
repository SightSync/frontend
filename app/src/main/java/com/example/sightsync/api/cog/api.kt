package com.example.sightsync.api.cog

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

object CogApiRetrofitClient {
    private const val BASE_URL = "https://2o7hh2v9oqo4l2-8000.proxy.runpod.net/"
    private const val CONNECT_TIMEOUT_SECONDS = 60
    private const val READ_TIMEOUT_SECONDS = 60
    private const val WRITE_TIMEOUT_SECONDS = 60

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
    }
}

