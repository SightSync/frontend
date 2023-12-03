package com.example.sightsync.api.cog

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface PostCaptionAPI {
    @POST("caption/")
    fun postCaption(
        @Query("image_name") image_name: String,
        @Query("query") query: String = "Describe the image as best as possible, given that you're an assistant to a visual impaired person"
    ): Call<String>
}

class PostCaptionServiceCog {
    private val retrofit = CogApiRetrofitClient.retrofit
    val postCaptionAPI: PostCaptionAPI = retrofit.create(PostCaptionAPI::class.java)
}
