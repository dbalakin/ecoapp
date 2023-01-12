package com.example.myapplication.data

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.core.text.isDigitsOnly
import com.example.myapplication.activities.MapsActivity
import com.example.myapplication.activities.MapsActivity.Companion.TYPE_AIR_DAY
import com.example.myapplication.activities.MapsActivity.Companion.TYPE_AIR_NIGHT
import com.example.myapplication.activities.MapsActivity.Companion.TYPE_GREEN
import com.example.myapplication.activities.MapsActivity.Companion.TYPE_RADIATION
import com.example.myapplication.activities.MapsActivity.Companion.TYPE_SOUND
import com.example.myapplication.activities.database
import com.example.myapplication.data.common.Colors
import com.example.myapplication.data.sql_helper.FavoriteInfo
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import kotlinx.coroutines.*


/**
 * @param other прозрачность цвета
 * Добавляет цвету прозрачность
 */
fun Int.alpha(other: Float): Int {
    val alpha = (255 * other).toInt()
    val a = Color.red(this)
    val b = Color.green(this)
    val c = Color.blue(this)
    return alpha and 0xff shl 24 or (a and 0xff shl 16) or (b and 0xff shl 8) or (c and 0xff)
}

/**
 * игнор
 * просто забавно
 */

fun Context.add(info: FavoriteInfo) = GlobalScope.launch {
    this@add.database.insert(info)
}

fun Context.findByLatLng(latLng: LatLng) = GlobalScope.launch {
    this@findByLatLng.database.findByLatLng(latLng.latitude, latLng.longitude)
}

fun Context.all() = GlobalScope.async {
    this@all.database.getAll()
}

fun Context.remove(request: FavoriteInfo) = GlobalScope.launch {
    this@remove.database.delete(request)
}

suspend fun Context.check(request: FavoriteInfo) = withContext(Dispatchers.Default) {
    val res = database.findByLatLng(request.lat, request.lng)
    res != null
}

fun <T : View> BottomSheetBehavior<T>.show() {
    this.state = BottomSheetBehavior.STATE_EXPANDED
}
fun <T : View> BottomSheetBehavior<T>.hide() {
    this.state = BottomSheetBehavior.STATE_HIDDEN
}


fun String.getPollutionString(type: Int): SpannableString {
    var level = ""
    var result = SpannableString("")
    var color = Color.parseColor("#C4C4C4")

    when (type) {
        TYPE_GREEN -> {
            when(this.toInt()) {
                1 -> {
                    level = "очень слабая"
                    color = Color.parseColor("#c8ed11")
                }
                2 -> {
                    level = "слабая"
                    color = Color.parseColor("#9ed20e")
                }
                in 3..4 -> {
                    level = "средняя"
                    color = Color.parseColor("#74b60a")
                }
                5 -> {
                    level = "сильная"
                    color = Color.parseColor("#4a9a05")
                }
            }
            result = SpannableString("Степень озеленения: $level")
            result.setSpan(ForegroundColorSpan(color), 20, result.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
        TYPE_RADIATION -> {
            val rad = when (this) {
                "1" -> "Менее 0,105 мЗв*ч"
                "2" -> "0,105 - 0,120 мЗв*ч"
                "3" -> "0,120 - 0,130 мЗв*ч"
                "4" -> "0,130 - 0,140 мЗв*ч"
                "5" -> "0,140 - 0,160 мЗв*ч"
                "6" -> "0,160 - 0,190 мЗв*ч"
                "7" -> "более 0,190 мЗв*ч"
                else -> ""
            }
            result = SpannableString("Уровень радиации: $rad")
        }
        TYPE_SOUND -> {
            val sound = when (this) {
                ">60 дБ" -> "от 3 до 4 баллов"
                "55-60 дБ" -> "от 2 до 3 баллов"
                "45-55 дБ" -> "от 2 до 3 баллов"
                "<45 дБ" -> "от 1 до 2 баллов"
                else -> ""
            }
            result = SpannableString("Уровень шума: $sound")
        }
        TYPE_AIR_DAY -> {
            result = SpannableString("Загрязнение воздуха: 1 ИЗА")
        }
        TYPE_AIR_NIGHT -> {
            val air = when (this) {
                "1", "2" -> "3 ИЗА"
                "3", "4", "5" -> "4 ИЗА"
                else -> ""
            }
            result = SpannableString("Загрязнение воздуха: $air")

        }
    }

    return result
}


fun String.marker(marker: String) = "${this}_$marker"


// supermarket, repairing, depo, тэс, котельная, завод, тэц, ядерная промышленность,
// химическая промышленность, нии, комбинат радон, медцентр, радиац производство, утилизация рад. отходов
fun getName(req: String): String = when(req) {
    in listOf("supermarket") -> "supermarket"
    in listOf("repairing") -> "repairing"
    in listOf("depo") -> "depo"
    in listOf("тэс", "тэц", "котельная") -> "energy"
    in listOf("завод", "химическая промышленность") -> "factory"
    in listOf("ядерная промышленность", "комбинат радон", "радиац производство", "утилизация рад. отходов", "нии") -> "radiation"
    in listOf("медцентр") -> "health"
    else -> throw NotImplementedError("no image for this type: $req")
}

fun getSearchName(req: String): List<String> = when(req) {
    in listOf("Дорожные работы") -> listOf("repairing")
    in listOf("Стройки") -> listOf()
    in listOf("Заправки") -> listOf("depo")
    in listOf("Парковки") -> listOf()
    in listOf("Супермаркеты") -> listOf("supermarket")
    in listOf("Станции ЭКО мониторинга") -> listOf("медцентр")
    in listOf("Заводы") -> listOf("завод", "химическая промышленность")
    in listOf("Электростанции") -> listOf("тэс", "тэц", "котельная")
    in listOf("Источники радицации") -> listOf("ядерная промышленность", "комбинат радон", "радиац производство", "утилизация рад. отходов", "нии")
    else -> throw NotImplementedError("no image for this type: $req")
}
