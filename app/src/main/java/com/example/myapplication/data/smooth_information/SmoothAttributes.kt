package com.example.myapplication.data.smooth_information

import com.google.gson.annotations.SerializedName

data class SmoothAttributes(
    @SerializedName("OBJECTID")
    val objectId: Int,
    val level: String?,
    val gridcode: String?,
)
