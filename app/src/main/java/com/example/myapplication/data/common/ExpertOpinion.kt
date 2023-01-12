package com.example.myapplication.data.common

import android.graphics.Color
import android.os.Parcelable
import com.example.myapplication.R
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ExpertOpinion(
    var conclusion: String? = null,
    var description: String? = null,
    var pollutions: List<Pollution> = arrayListOf(),
    var photo: String? = null
): Parcelable {

    fun takeConclusion() = conclusion ?: "-"
    fun takeDescription() = description ?: "-"
}

@Parcelize
data class Pollution(
    var id: Int = 0,
    var type: String? = null,
    var dop: String? = null,
    var icon: Int? = null,
    var color: Int? = null
): Parcelable {

    fun takeType() = type ?: "-"
    fun takeDop() = dop ?: "загрязнение"
    fun takeIcon() = icon ?: R.drawable.air
    fun takeColor() = color ?: R.drawable.air_bg
}