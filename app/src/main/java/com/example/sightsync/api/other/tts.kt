package com.example.sightsync.api.other

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Streaming

interface TtsAPI {
    @Streaming
    @POST("tts/")
    fun getAudio(
        @Query("text") text: String,
    ): Call<ResponseBody>
}

class TtsService {
    private val retrofit = ApiRetrofitClient.retrofit
    val ttsAPI: TtsAPI = retrofit.create(TtsAPI::class.java)
}
