package com.example.sightsync.api.cog

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PostImageAPI {
    @Multipart
    @POST("image/")
    fun postImage(
        @Part image: MultipartBody.Part
    ): Call<String>
}

class PostImageService {
    private val retrofit = CogApiRetrofitClient.retrofit
    val postImageAPI: PostImageAPI = retrofit.create(PostImageAPI::class.java)
}
