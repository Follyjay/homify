package com.example.homify

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

interface ApiService {

    @Multipart
    @POST("new_apartment.php")
    fun uploadProperty(
        @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part imageParts: List<MultipartBody.Part>
    ): Call<ResponseBody>
}
