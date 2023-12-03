package com.example.sightsync.api.other

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SttAPI {
    @Multipart
    @POST("stt/")
    fun getTranscription(
        @Part audio: MultipartBody.Part
    ): Call<String>
}

class SttService {
    private val retrofit = ApiRetrofitClient.retrofit
    val sttAPI: SttAPI = retrofit.create(SttAPI::class.java)
}
