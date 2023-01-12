package com.example.myapplication.data.common

import android.graphics.Color
import com.example.myapplication.data.alpha

object Colors {
    var  alpha = .25f
    val GREEN = Color.rgb(46, 182, 44).alpha(alpha)
    val CYAN = Color.CYAN.alpha(alpha)
    val BLUE = Color.BLUE.alpha(alpha)
    val RED = Color.RED.alpha(alpha)
    val YELLOW = Color.YELLOW.alpha(alpha)
    val greenColors = arrayOf("#c8ed11", "#9ed20e", "#74b60a", "#1b7b00", "#4a9a05")
    val radiationColors = arrayOf("#00C563", "#46FF5E", "#1AE958", "#00AC86", "#0094A7", "#0068AC", "#0050D2")
    val airDayColors = arrayOf("#FFD550", "#FFB93E", "#FF9F2D", "#FF8119", "#FF6305")
    val airNightColors = arrayOf("#ECFEEF", "#F0FBD3", "#F4F8B5", "#F8F598", "#FBF382", "#FFEF62")
    val soundColors = arrayOf("#33ff9500", "#ff950040", "#33ff9500", "#1Aff9500")
}
