package com.example.sightsync.api.other

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface PostIntentAPI {
    @POST("intent/")
    fun postIntent(
        @Query("query") query: String,
    ): Call<String>
}

class PostIntentService {
    private val retrofit = ApiRetrofitClient.retrofit
    val postIntentAPI: PostIntentAPI = retrofit.create(PostIntentAPI::class.java)
}
