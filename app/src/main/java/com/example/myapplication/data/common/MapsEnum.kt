package com.example.myapplication.data.common

// will work

object Sample: MapsInfo() {
    const val Smooth = "smooth"
}

object Green: MapsInfo() {
    const val Smooth = "smooth"
    const val Parks ="parks"
}

open class MapsInfo {
    val name = javaClass.name
}

