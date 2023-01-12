package com.example.myapplication.objects.retrofit

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object QueryHelper {

    init {
        init()
    }

    private fun init() {
/*        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging) */

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
/*            .client(httpClient.build())*/
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        autocompleteService = retrofit.create(API::class.java)
    }

    private lateinit var autocompleteService: API

    suspend fun makeRequest(name: String) = CoroutineScope(Dispatchers.IO).async {
        autocompleteService.autocompletePlace(name)
    }
}
