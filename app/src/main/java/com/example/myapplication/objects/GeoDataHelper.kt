package com.example.myapplication.objects

import android.annotation.SuppressLint
import com.example.myapplication.data.Codes

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import com.example.myapplication.activities.MapsActivity
import com.example.myapplication.data.common.UnpackedMapsInfo
import com.example.myapplication.data.geometry.WDYHM
import com.example.myapplication.data.getName
import com.example.myapplication.data.marker
import com.example.myapplication.data.show
import com.example.myapplication.data.sql_helper.FavoriteInfo
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@SuppressLint("StaticFieldLeak")
object GeoDataHelper {

    var pollutionType: String = "green"
    lateinit var context: Context
    lateinit var symbolManager: SymbolManager
    private lateinit var mapsRef: MapboxMap

    val loadedMaps: MutableMap<String, UnpackedMapsInfo> = mutableMapOf()
    val loadedMarkers: MutableMap<String, MutableList<Feature>> = mutableMapOf()
    var currentLayerId: String? = null
    private var chosenPolygons: MutableList<Pair<List<LatLng>, String>> = mutableListOf()
    private var currentColorBuilder: UnpackedMapsInfo.ColorBuilder? = null

    /**
     * Сохраняем ссылки на контекст и карту
     */
    fun init(context: Context, mMap: MapboxMap, mapView: MapView) {
        mapsRef = mMap
        mMap.getStyle {
            symbolManager = SymbolManager(mapView, mMap, it)
        }
        this.context = context
        mapsRef.addOnMapClickListener {
            //MapsActivity.markers.forEach(Marker::remove)
            if (currentLayerId == null) {
                getClicksInfoWithCallback(it)
                return@addOnMapClickListener true
            }
            val screenPoint: PointF = mapsRef.projection.toScreenLocation(it)
            val features: List<Feature> = mapsRef.queryRenderedFeatures(screenPoint, currentLayerId)
            if (features.isNotEmpty()) {
                with(MapsActivity) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val p = features[0].geometry() as Point
                        val res = getClicksInfo(LatLng(p.latitude(), p.longitude()))
                        enteredPlace = res
                        update(enteredPlace!!, false)
                        bottomSheetBehavior.show()
                    }
                }
            } else {
                getClicksInfoWithCallback(it)
            }
            true
        }
    }

    /**
     * @param name - имя папки с картами
     * @param loadInfo - что грузить
     */

    inline fun load(
        name: String,
        loadInfo: UnpackedMapsInfo.() -> Unit
    ) {
        if (name !in loadedMaps) {
            loadedMaps[name] = UnpackedMapsInfo(name)
        }
        loadInfo(loadedMaps[name]!!)
        loadedMaps[name]!!.let { umi ->
            umi.markers.forEach {
                loadedMarkers[it.key] = it.value
            }
        }

    }


    suspend fun clearMarkers() = suspendCoroutine<Unit> { suspend ->
        mapsRef.getStyle {
            it.removeLayer("marker-layer")
            suspend.resume(kotlin.Unit)
        }
    }

    fun getClicksInfoWithCallback(latLng: LatLng) = CoroutineScope(Dispatchers.Main).launch {
        val info = getClicksInfo(latLng)
        MapsActivity.update(info)
    }


    suspend fun getClicksInfo(latLng: LatLng) = flowOf(getClicksPolygons(latLng))
        .flowOn(Dispatchers.IO)
        .map(Deferred<Pair<List<String>, LatLng>>::await)
        .map { res ->
            val geoCoder = Geocoder(context, Locale("RU"))
            val addr = geoCoder.getFromLocation(res.second.latitude, res.second.longitude, 1)[0]
            val fullAddr = addr.getAddressLine(0).split(',')
            Log.d("click-debug", fullAddr.toString())
            FavoriteInfo(
                getFullname(addr),
                "${fullAddr[0]}${fullAddr.getOrNull(1) ?: ""}",
                currentColorBuilder?.sortTypes(res.first)?.getOrNull(0) ?: "Нет данных",
                res.second.latitude,
                res.second.longitude
            )
        }
        .first()


    fun getFullname(addr: Address) = buildString {
        val count = addr.maxAddressLineIndex
        (0..count).forEach {
            append(addr.getAddressLine(it))
        }
    }

    fun getClicksPolygons(latLng: LatLng) = CoroutineScope(Dispatchers.Main).async {
        val mayBeHere = mutableListOf<String>()
        val correctLatLng =
            if (latLng.latitude < latLng.longitude)
                latLng
            else
                LatLng(latLng.longitude, latLng.latitude)
        chosenPolygons.forEach {
            if (WDYHM.contains(it.first, correctLatLng)) {
                mayBeHere.add(it.second)
            }
        }
        mayBeHere.distinct() to latLng
    }

    /**
     * Двигаем камеру
     */
    fun moveCamera(content: FavoriteInfo) {
        val latLng = LatLng(content.lat, content.lng)
        MapsActivity.update(content)
        mapsRef.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0))
    }

    fun updateUI(mapName: String, overlayName: String, vararg markers: String) {
        updateUI(loadedMaps[mapName]!!, overlayName, *markers)
    }

    fun updateUI(map: Codes, vararg markers: String) {
        val all = map.code.split('.')
        val mapName = all[0]
        val overlayName = all[1]
        updateUI(mapName, overlayName, *markers)
    }

    fun updateSymbolManager(style: Style) {
        with(MapsActivity) {
            symbolManager.addClickListener {
                Toast.makeText(context, "123", Toast.LENGTH_LONG).show()
                GlobalScope.launch {
                    val res = getClicksInfo(it.latLng)
                    enteredPlace = res
                    update(enteredPlace!!, false)
                    bottomSheetBehavior.show()
                }
                true
            }
        }
    }

    private fun updateUI(maps: UnpackedMapsInfo, overlayName: String, vararg markers: String) {
        chosenPolygons = maps.polygons[overlayName]!!
        currentColorBuilder = maps.builders[overlayName]!!
        val style = Style.Builder().fromUrl(maps.builders[overlayName]?.url!!)
        markers.forEach {
            addMarkersToLayer(style, it)
        }
        mapsRef.setStyle(style)
/*        MapsActivity.apply {
            mapsRef.getStyle {
                updateSymbolManager(it)
            }
        }*/
    }

    fun loadMarkers(markers: List<String>) {
        val style = Style.Builder().fromUrl(currentColorBuilder!!.url)
        markers.forEach { addMarkersToLayer(style, it) }
        mapsRef.setStyle(style)
    }

    fun addMarkersToLayer(style: Style.Builder, markersName: String) {
        val value = loadedMarkers[markersName]!!
        val id = context.resources.getIdentifier(getName(markersName.toLowerCase()), "drawable", context.packageName)
        val mapName = currentColorBuilder!!.mapName
        val mapNameKey = mapName.marker(markersName)
        val lowerKey = "${mapName}_$markersName"
        currentLayerId = "${mapNameKey}_symbol"
        style
            .withSource(GeoJsonSource(mapNameKey, FeatureCollection.fromFeatures(value)))
            .withImage("${lowerKey}_png", BitmapFactory.decodeResource(context.resources, id))
            .withLayer(
                SymbolLayer(
                    "${mapNameKey}_symbol",
                    mapNameKey
                ).withProperties(
                    PropertyFactory.iconImage("${lowerKey}_png"),
                    PropertyFactory.iconSize(0.1f),
                    PropertyFactory.iconAllowOverlap(true),
                    PropertyFactory.iconIgnorePlacement(true),
                )
            )
    }


}


