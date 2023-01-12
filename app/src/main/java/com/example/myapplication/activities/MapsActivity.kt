package com.example.myapplication.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.isDigitsOnly
import androidx.room.Room
import com.example.myapplication.R
import com.example.myapplication.data.*
import com.example.myapplication.data.common.Pollution
import com.example.myapplication.data.sql_helper.AppDatabase
import com.example.myapplication.data.sql_helper.ContentDao
import com.example.myapplication.data.sql_helper.FavoriteInfo
import com.example.myapplication.objects.GeoDataHelper
import com.example.myapplication.ui.ItemClickListener
import com.example.myapplication.ui.MainPollutionAdapter
import com.example.myapplication.ui.TimeDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.mapbox.geojson.Feature

import android.graphics.PointF
import com.example.myapplication.data.common.Constants
import com.example.myapplication.data.common.ExpertOpinion
import com.example.myapplication.ui.fake_data.FakeExperts


private lateinit var db: AppDatabase


class MapsActivity : AppCompatActivity(), ItemClickListener {
    private val REQUEST_CODE_AUTOCOMPLETE: Int = 810

    companion object {
        private lateinit var context: Context
        private lateinit var mapBox: MapboxMap
        lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
        private lateinit var placeText: TextView
        private lateinit var addressText: TextView
        private lateinit var favIcon: ImageView
        private lateinit var corrupt: TextView
        private lateinit var askButton: Button
        private lateinit var icon: Icon
        lateinit var symbolManager: SymbolManager
        lateinit var mapView: MapView
        lateinit var staticMap: MapboxMap
        lateinit var bottomSheet: ConstraintLayout
        var enteredPlace: FavoriteInfo? = null
        val markers = mutableListOf<Marker>()
        lateinit var yellow: Bitmap
        lateinit var grey: Bitmap
        var status: Boolean? = null

        const val TYPE_GREEN = 0
        const val TYPE_RADIATION = 1
        const val TYPE_AIR_DAY = 2
        const val TYPE_AIR_NIGHT = 3
        const val TYPE_SOUND = 4

        fun update(info: FavoriteInfo, addMarker: Boolean = true) {
            if (addMarker) {
                markers.forEach(Marker::remove)
                markers.add(
                    mapBox.addMarker(
                        MarkerOptions()
                            .position(LatLng(info.lat, info.lng))
                            .icon(icon)
                    )
                )
            }
            enteredPlace = info
            initFavStatus().invokeOnCompletion {
                bottomSheetBehavior.show()
            }
            try {
                setIndicator(value = info.corruptionInfo ?: "")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            placeText.text = info.place
            addressText.text = info.address
            askButton.setOnClickListener {
                val expertOpinion = when(GeoDataHelper.pollutionType) {
                    "green" -> FakeExperts.getGreenTips(info.corruptionInfo ?: "")
                    "rad" -> FakeExperts.getRadTips(info.corruptionInfo ?: "")
                    "sound" -> FakeExperts.getSoundTips(info.corruptionInfo ?: "")
                    "air1", "air2" -> FakeExperts.getAirTips(info.corruptionInfo ?: "")
                    else -> ""
                }
                bottomSheetBehavior.hide()
                context.startActivity(Intent(context, ExpertOpinionActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(
                        Constants.EXTRA_EXPERT_OPINION,
                        ExpertOpinion(
                            conclusion = "${MapsHelper.parseNameRU(GeoDataHelper.pollutionType)} загрязнение",
                            description = expertOpinion
                        )
                    )
                })
            }
        }

        fun initFavStatus() = CoroutineScope(Dispatchers.Default).launch {
            val status = context.check(enteredPlace!!)
            launch(Dispatchers.Main) {
                this@Companion.status = status
                favIcon.setImageBitmap(if (status) yellow else grey)
            }
        }

        fun setIndicator(value: String) {
            if (!value.isDigitsOnly() && value != "Нет данных" && !value.toLowerCase().contains("дб"))
                return
            var convertedValue = value
            if (value == "Нет данных")
                convertedValue = "-1"
            var type = TYPE_GREEN

            when (GeoDataHelper.pollutionType) {
                "green" -> {
                    type = TYPE_GREEN
                }
                "rad" -> {
                    type = TYPE_RADIATION
                }
                "sound" -> {
                    type = TYPE_SOUND
                    convertedValue = convertedValue.toLowerCase()
                        .replace("<", "")
                        .replace(">", "")
                        .replace("дб", "")
                        .trim()
                    if (convertedValue.contains("-") && convertedValue != "-1") {
                        convertedValue = convertedValue
                            .removeRange(convertedValue.indexOf("-"), convertedValue.length)
                        convertedValue = (convertedValue.toDouble() + 1).toString()
                    }
                }
                "air1" -> {
                    type = TYPE_AIR_DAY
                }
                "air2" -> {
                    type = TYPE_AIR_NIGHT
                }
            }

            bottomSheet.indicator.setColors(value = convertedValue.toDouble(), type = type)
            if (convertedValue == "-1")
                corrupt.text = "Нет данных"
            else
                corrupt.text = value.getPollutionString(type = type)
        }
    }

    private val adapter = MainPollutionAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_maps)
        fragment_container.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE)
        }
        bottomSheet = bottom_sheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        placeText = findViewById(R.id.place)
        addressText = findViewById(R.id.address)
        askButton = findViewById(R.id.bottom_sheet_button)
        favIcon = findViewById(R.id.fav)
        corrupt = findViewById(R.id.et_corrupt)
        context = applicationContext
        yellow = BitmapFactory.decodeResource(context.resources, R.drawable.fav_chosed)
        grey = BitmapFactory.decodeResource(context.resources, R.drawable.fav)
        icon = IconFactory.getInstance(context).fromResource(R.drawable.marker)
        mapView = map
        val pollutions = arrayListOf(
            Pollution(0, "Воздушное", icon = R.drawable.air, color = R.drawable.air_bg),
            Pollution(1, "Звуковое", icon = R.drawable.noise, color = R.drawable.noise_bg),
            Pollution(2, "Зелёные", "насаждения", R.drawable.green, R.drawable.green_bg),
            Pollution(3, "Радиационное", icon = R.drawable.rad, color = R.drawable.rad_bg)
        )
        pollutions_block.adapter = adapter
        adapter.putArticles(pollutions)
        initBottomSheet(bottomSheetBehavior)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "content"
        )
            .fallbackToDestructiveMigration()
            .build()
        favorite_main.setOnClickListener {
            startActivity(Intent(this, FavoriteActivity::class.java))
        }

        blog_main.setOnClickListener {
            startActivity(Intent(this, BlogActivity::class.java))
        }
        map.getMapAsync { mapboxMap ->
            mapBox = mapboxMap
            staticMap = mapboxMap
            mapBox.uiSettings.isAttributionEnabled = false
            mapBox.uiSettings.isLogoEnabled = false
            mapBox.uiSettings.compassImage!!.alpha = 0
            mapBox.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(55.765025284921855, 37.51469772515383), 13.0))
            mapBox.setStyle(Style.MAPBOX_STREETS)
            mapBox.getStyle {
                symbolManager = SymbolManager(map, mapboxMap, it)
            }
            GeoDataHelper.init(context, mapBox, map)
            with(GeoDataHelper) {
                load("sound") {
                    loadOverlay("smooth") {
                        url("mapbox://styles/v8tenko/ckic4o84m1dau19odn50uqi0a")
                        values(">60 дБ", "55-60 дБ", "45-55 дБ", "<45 дБ")
                        markers("noise")
                    }
                }
                load("rad") {
                    loadOverlay("smooth") {
                        url("mapbox://styles/v8tenko/ckhz03myo122f19mvlcok4kk1")
                        values()
                        markers("smooth")
                    }
                }
                load("air") {
                    loadOverlay("morning") {
                        url("mapbox://styles/v8tenko/cki04bh6j254q19mvqesqgb6d")
                        values()
                    }
                    loadOverlay("evening") {
                        url("mapbox://styles/v8tenko/ckhz0lxz306wu19lr4lwgzk1g")
                        values()
                    }
                }
                load("green") {
                    loadOverlay("smooth") {
                        url("mapbox://styles/v8tenko/ckhi6es7j2juz19mww683oicf")
                        values()
                    }
                }
            }
            GeoDataHelper.updateUI(Green.Smooth)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            val latLng = data?.getParcelableExtra("point") as? LatLng ?: throw IllegalStateException()
            mapBox.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0))
            CoroutineScope(Dispatchers.Main).launch { GeoDataHelper.clearMarkers() }
            flowOf(latLng)
                .map(GeoDataHelper::getClicksInfo)
                .map {
                    enteredPlace = it
                    update(enteredPlace!!)
                    bottomSheetBehavior.show()
                }
                .launchIn(CoroutineScope(Dispatchers.Main))
            Log.i(TAG, "Place: $latLng")
        }
    }


    private fun initBottomSheet(bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>) {
        bottomSheetBehavior.hide()
        bottom_sheet.bottom_sheet_button.setOnClickListener {
            update(enteredPlace!!)
            enteredPlace = null
        }
        bottom_sheet.fav.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val res = check(enteredPlace!!)
                if (res) {
                    remove(enteredPlace!!)
                } else {
                    add(enteredPlace!!)
                }
                status ?: return@launch
                if (!status!!) {
                    fav.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.fav_chosed))
                } else {
                    fav.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.fav))
                }
                status = !status!!
            }
        }
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    markers.forEach(Marker::remove)
                    markers.clear()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                return
            }

        })

    }


    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onStart() {
        super.onStart()
        map.onStart()
    }

    override fun onStop() {
        super.onStop()
        map.onStop()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        map.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map.onSaveInstanceState(outState)
    }


    override fun onItemClick(item: Pollution, position: Int) {
        val dialog = TimeDialog(item)
        GeoDataHelper.pollutionType = MapsHelper.parseName(item.type!!)
        if (GeoDataHelper.loadedMaps[GeoDataHelper.pollutionType]!!.builders.size != 1) {
            dialog.show(supportFragmentManager, "TimeDialog")
            return
        } else {
            GeoDataHelper.updateUI(MapsHelper.parseMaps(item.type!!))
        }

    }

    val Activity.TAG: String
        get() {
            return this::class.simpleName ?: "Anon"
        }
}

val Context.database: ContentDao
    get() = db.userDao()

