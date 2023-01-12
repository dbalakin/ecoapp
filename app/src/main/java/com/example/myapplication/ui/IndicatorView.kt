package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.myapplication.R
import com.example.myapplication.activities.MapsActivity.Companion.TYPE_AIR_DAY
import com.example.myapplication.activities.MapsActivity.Companion.TYPE_AIR_NIGHT
import com.example.myapplication.activities.MapsActivity.Companion.TYPE_GREEN
import com.example.myapplication.activities.MapsActivity.Companion.TYPE_RADIATION
import com.example.myapplication.activities.MapsActivity.Companion.TYPE_SOUND
import com.example.myapplication.data.common.Colors.airDayColors
import com.example.myapplication.data.common.Colors.airNightColors
import com.example.myapplication.data.common.Colors.greenColors
import com.example.myapplication.data.common.Colors.radiationColors
import com.example.myapplication.data.common.Colors.soundColors

class IndicatorView(context: Context, attrs: AttributeSet): View(context, attrs) {

    private val paint = Paint()
    private lateinit var colorsCodes: Array<CharSequence>
    private lateinit var colors: IntArray
    private var colorsCount = 2
    private var indicatorPart = 1  // max value = 10, 1 = 10%, 2 = 20%, ..., 10 = 100%
    private var styledAttributes: TypedArray

    init {
        styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.IndicatorView)
        try {
            colorsCodes = styledAttributes.getTextArray(R.styleable.IndicatorView_android_entries)
            colors = IntArray(colorsCodes.size)
            colorsCodes.forEachIndexed { index, code ->
                colors[index] = Color.parseColor(code.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            styledAttributes.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val modeX = MeasureSpec.getMode(widthMeasureSpec)
        val sizeX = MeasureSpec.getSize(widthMeasureSpec)
        val modeY = MeasureSpec.getMode(heightMeasureSpec)
        val sizeY = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(resolveSize(sizeX, widthMeasureSpec), resolveSize(sizeY, heightMeasureSpec))
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        paint.shader = LinearGradient(0f, 0f, width.toFloat(), 5f, colors, null, Shader.TileMode.CLAMP)
        paint.strokeWidth = 5f
        canvas?.drawRoundRect(0f, 0f, width.toFloat() / 10 * indicatorPart, 5f, 15f, 15f, paint)
    }

    fun setColors(value: Double, type: Int) {
        val _colorsCodes: Array<String>
        if (value == -1.0) {
            indicatorPart = 10
            colors = intArrayOf(Color.parseColor("#C4C4C4"), Color.parseColor("#C4C4C4"))
            invalidate()
            return
        }

        when(type) {
            TYPE_GREEN -> {
                _colorsCodes = greenColors
                when (value.toInt()) {
                    1 -> {
                        indicatorPart = 2
                        colorsCount = 2
                    }
                    2 -> {
                        indicatorPart = 4
                        colorsCount = 3
                    }
                    in 3..4 -> {
                        indicatorPart = 7
                        colorsCount = 4
                    }
                    5 -> {
                        indicatorPart = 10
                        colorsCount = 5
                    }
                    else -> return
                }
            }
            TYPE_RADIATION -> {
                _colorsCodes = radiationColors
                when  {
                    value < 0.105 -> {
                        indicatorPart = 1
                        colorsCount = 2
                    }
                    value in (0.105..0.120) -> {
                        indicatorPart = 2
                        colorsCount = 3
                    }
                    value in (0.120..0.130) -> {
                        indicatorPart = 4
                        colorsCount = 4
                    }
                    value in (0.130..0.140) -> {
                        indicatorPart = 6
                        colorsCount = 5
                    }
                    value in (0.140..0.160) -> {
                        indicatorPart = 8
                        colorsCount = 6
                    }
                    value in (0.160..0.190) -> {
                        indicatorPart = 10
                        colorsCount = 7
                    }
                    else -> return
                }
            }
            TYPE_SOUND -> {
                _colorsCodes = soundColors
                when {
                    value <= 45.0 -> {
                        indicatorPart = 2
                        colorsCount = 2
                    }
                    value in 46.0..60.0 -> {
                        indicatorPart = 6
                        colorsCount = 3
                    }
                    value > 60 -> {
                        indicatorPart = 10
                        colorsCount = 4
                    }
                }
            }
            TYPE_AIR_DAY -> {
                _colorsCodes = airDayColors
                when {
                    value < 5 -> {
                        indicatorPart = 1
                        colorsCount = 2
                    }
                    value in (5.0..6.0) -> {
                        indicatorPart = 3
                        colorsCount = 2
                    }
                    value in (6.0..7.0) -> {
                        indicatorPart = 5
                        colorsCount = 3
                    }
                    value in (8.0..10.0) -> {
                        indicatorPart = 7
                        colorsCount = 4
                    }
                    else -> return
                }
            }
            TYPE_AIR_NIGHT -> {
                _colorsCodes = airNightColors
                when {
                    value < 5 -> {
                        indicatorPart = 1
                        colorsCount = 2
                    }
                    value in (5.0..6.0) -> {
                        indicatorPart = 3
                        colorsCount = 2
                    }
                    value in (6.0..7.0) -> {
                        indicatorPart = 5
                        colorsCount = 3
                    }
                    value in (8.0..10.0) -> {
                        indicatorPart = 7
                        colorsCount = 4
                    }
                    else -> return
                }
            }
            else -> return
        }
        try {
            colors = IntArray(colorsCount)
            _colorsCodes.forEachIndexed { index, code ->
                if (index < colorsCount)
                    colors[index] = Color.parseColor(code)
            }
            invalidate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}