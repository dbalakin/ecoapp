package com.example.myapplication.data.common

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
class BlogArticle(
    var title: String? = null,
    var author: String? = null,
    var date: Date? = null,
    var image: String? = null,
    var description: String? = null,
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
): Parcelable {


    fun takeTitle() = title ?: "-"
    fun takeAuthor() = "Автор: " + (author ?: "-")
    fun takeDate(): String {
        try {
            return dateFormat.format(date)
        } catch (e: Exception) {
            return "-"
        }
    }
    fun takeDescription() = description ?: "-"
}