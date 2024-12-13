package com.example.genggammakna.reftrofit

import com.example.genggammakna.response.PredictionResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("predict/image")
    suspend fun predictImage(@Part file: MultipartBody.Part): Response<PredictionResponse>
}
