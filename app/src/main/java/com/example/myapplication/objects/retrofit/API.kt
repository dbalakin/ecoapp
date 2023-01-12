package com.example.myapplication.objects.retrofit

import com.example.myapplication.objects.retrofit.data.PlaceInfo
import retrofit2.http.GET
import retrofit2.http.Query

interface API {

    @GET("place/autocomplete/json")
    suspend fun autocompletePlace(
        @Query("input") input: String,
        @Query("key") key: String = "AIzaSyC01Mtc9hUhJNnJjturwqIQEFtDZJRvur4",
        @Query("language") language: String = "ru"
    ): PlaceInfo

}


