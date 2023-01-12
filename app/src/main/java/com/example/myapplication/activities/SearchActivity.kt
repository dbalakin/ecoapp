package com.example.myapplication.activities

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.all
import com.example.myapplication.data.common.Constants
import com.example.myapplication.data.sql_helper.FavoriteInfo
import com.example.myapplication.objects.GeoDataHelper
import com.example.myapplication.objects.retrofit.QueryHelper
import com.example.myapplication.objects.retrofit.data.PlaceInfo
import com.example.myapplication.ui.MarkersRecyclerViewAdapter
import com.example.myapplication.ui.favorite.FavoriteRecyclerAdapter
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.*


@ObsoleteCoroutinesApi
class SearchActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var contentRecyclerView: RecyclerView
    lateinit var searchEditText: EditText
    private val searchScope = CoroutineScope(Dispatchers.Main)
    private lateinit var contentRecyclerViewAdapter: FavoriteRecyclerAdapter
    private lateinit var favoriteCopy: MutableList<FavoriteInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recyclerView = findViewById(R.id.search_rv)
        contentRecyclerView = findViewById(R.id.content_rv)
        searchEditText = findViewById(R.id.et_search)

        recyclerView.apply {
            adapter = MarkersRecyclerViewAdapter(Constants.menuItems) {
                onBackPressed()
                finish()
            }
            layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.HORIZONTAL, false)
        }

        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            (recyclerView.layoutManager as LinearLayoutManager).orientation
        )

        recyclerView.addItemDecoration(dividerItemDecoration)

        contentRecyclerViewAdapter = FavoriteRecyclerAdapter {
            val intent = Intent()
            val data = contentRecyclerViewAdapter.content[it]
            intent.putExtra("point", LatLng(data.lat, data.lng))
            setResult(Activity.RESULT_OK, intent)
            finish()
            onBackPressed()
        }


        contentRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = contentRecyclerViewAdapter
        }

        CoroutineScope(Dispatchers.Main).launch {
            favoriteCopy = all().await()
            contentRecyclerViewAdapter.content = favoriteCopy
            contentRecyclerViewAdapter.notifyDataSetChanged()
        }

        searchEditText.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                makeRequest(searchEditText.text.toString())
            }
            true
        }
    }

    private fun makeRequest(new: String) = flowOf(new)
        .map(QueryHelper::makeRequest)
        .map(Deferred<PlaceInfo>::await)
        .onEach {
            updateUI(it)
        }
        .launchIn(searchScope)

    private fun updateUI(response: PlaceInfo) {
        contentRecyclerViewAdapter.updateContent(
            response.predictions.map {
                val geoCoder = Geocoder(GeoDataHelper.context, Locale("RU"))
                val addr = geoCoder.getFromLocationName(it.description, 1)[0]
                FavoriteInfo(
                    it.description,
                    it.description.split(',')[0],
                    null,
                    addr.latitude, addr.longitude
                )
            }.toMutableList()
        )
    }

    override fun onDestroy() {
        searchScope.cancel()
        super.onDestroy()
    }
}
