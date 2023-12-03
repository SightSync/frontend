package com.example.sightsync.api.cog

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface PostCaptionAPI {
    @POST("caption/")
    fun postCaption(
        @Query("image_name") image_name: String,
        @Query("query") query: String
    ): Call<String>
}

class PostCaptionServiceCog {
    private val retrofit = CogApiRetrofitClient.retrofit
    val postCaptionAPI: PostCaptionAPI = retrofit.create(PostCaptionAPI::class.java)
}
