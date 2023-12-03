package com.example.sightsync.api.other

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface PostLocateAPI {
    @POST("locate/")
    fun postLocation(
        @Query("image_name") image_name: String,
        @Query("class_name") class_name: String
    ): Call<String>
}

class PostLocationService {
    private val retrofit = ApiRetrofitClient.retrofit
    val postLocationAPI: PostLocateAPI = retrofit.create(PostLocateAPI::class.java)
}
