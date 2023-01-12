package com.example.myapplication.data.common

import com.example.myapplication.data.smooth_information.SmoothInfo
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.myapplication.data.noise_information.NoiseInfo
import com.example.myapplication.objects.GeoDataHelper
import com.example.myapplication.objects.GeoDataHelper.context
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import java.util.*
import kotlin.properties.Delegates

/**
 * @param name - имя карты
 * Загружает карты с assets по названию. Генерирует готовые PolygonOptions для отображения.
 **/
class UnpackedMapsInfo(val name: String) {

    val polygons: MutableMap<String, MutableList<Pair<List<LatLng>, String>>> = mutableMapOf()
    val markers: MutableMap<String, MutableList<Feature>> = mutableMapOf()
    val builders: MutableMap<String, ColorBuilder> = mutableMapOf()
    var lastOverlayName: String? = null

    inline fun loadOverlay(overlayName: String, color: ColorBuilder.() -> Unit) {
        lastOverlayName = overlayName
        builders[overlayName] = ColorBuilder().apply(color)
        val content = context.assets.open("$name/${overlayName}.json")
            .bufferedReader()
            .use { all -> all.readText() }
        val info = Gson().fromJson(content, SmoothInfo::class.java)
        polygons[overlayName] = mutableListOf()
        info.features.map { feature ->
            val id = feature.attributes.level ?: feature.attributes.gridcode!!
            if (id !in builders[overlayName]!!.allValues) {
                builders[overlayName]!!.allValues.add(id)
            }
            Pair(
                feature.geometry.rings.map { it.map { v -> LatLng(v[0], v[1]) }.toTypedArray() },
                feature.attributes.level ?: feature.attributes.gridcode!!,
            )
        }.forEach { value ->
            value.first.forEach {
                CoroutineScope(Dispatchers.Main).launch {
                    polygons[overlayName]!!.add(it.toList() to value.second)
                }
            }
        }
        builders[overlayName]!!.apply(color)
    }

    fun loadOverlay(overlayName: String, defaultColor: Int) {
        loadOverlay(overlayName) {}
        builders[overlayName]!!.defaultColor = defaultColor
    }

    inner class ColorBuilder {


        private val ranges: MutableList<Pair<String, Int>> = mutableListOf()
        var allValues: MutableList<String> = mutableListOf()
        var url = ""
        var defaultColor by Delegates.notNull<Int>()
        val style = Style.Builder()
        var output: List<Pair<Int, String>> = listOf()
        val mapName: String
            get() = "${this@UnpackedMapsInfo.name}_${lastOverlayName}"
        val mapType: String
            get() = this@UnpackedMapsInfo.name

        fun values() {
            ranges.addAll(((allValues.distinct().size downTo 1)).mapIndexed { index, i -> i.toString() to index })
        }

        fun values(vararg req: String) {
            ranges.addAll(req.withIndex().map { it.value to it.index })
        }

        fun output(vararg req: String) {
            output = req.mapIndexed { el, idx -> el to idx }
        }

        fun url(other: String) {
            url = other
        }

        fun markers(name: String) {
            val content = context.assets.open("${this@UnpackedMapsInfo.name}/markers/${name}.json")
                .bufferedReader()
                .use { all -> all.readText() }
            val info = Gson().fromJson(content, NoiseInfo::class.java)
            info.features.forEach {
                var (x, y) = listOf(it.geometry.x, it.geometry.y).sorted()
                val latLng = Feature.fromGeometry(Point.fromLngLat(x, y))
                val header = it.attributes.source.toLowerCase()
                markers[header]?.add(latLng) ?: run {
                    markers[header] = mutableListOf(latLng)
                }
            }


        }

        fun sortTypes(req: List<String>) = ranges
            .asSequence()
            .filter { it.first in req }
            .withIndex()
            .map { it.index to it.value.first }
            .sortedWith(compareBy { it.first })
            .map { it.second }
            .toList()


    }
}
