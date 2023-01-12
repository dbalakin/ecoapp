package com.example.myapplication.data.noise_information

import com.google.gson.annotations.SerializedName

data class NoiseAttributes(
    @SerializedName("OBJECTID")
    val objectId: Int,
    val source: String,
)
