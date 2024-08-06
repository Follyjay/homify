package com.example.homify

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Declaring a variable of type retrofit with null value
    private var retrofit: Retrofit? = null

    // Retrofit function to create a retrofit client base on the specified URL
    fun getClient(baseUrl: String): Retrofit {

        // Checks if the retrofit instance is null
        if (retrofit == null) {

            // Logs HTTP request through the interceptor variable
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            // Creates OKHTTP with the interceptor added
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            // Builds instance of retrofit
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        // returns the built instance
        return retrofit!!
    }
}
