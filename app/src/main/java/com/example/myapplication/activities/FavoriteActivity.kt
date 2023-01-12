package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.all
import com.example.myapplication.data.remove
import com.example.myapplication.data.sql_helper.FavoriteInfo
import com.example.myapplication.objects.GeoDataHelper
import com.example.myapplication.ui.favorite.DragHelper
import com.example.myapplication.ui.favorite.FavoriteRecyclerAdapter
import kotlinx.android.synthetic.main.activity_favorite.*
import kotlinx.android.synthetic.main.fragment_blog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class FavoriteActivity: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: FavoriteRecyclerAdapter

    var content: MutableList<FavoriteInfo> by Delegates.observable(mutableListOf()) { _, _, newValue ->
        recyclerViewAdapter.content = newValue
        recyclerViewAdapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        recyclerView = favorite_recycler
        recyclerViewAdapter = FavoriteRecyclerAdapter {
            GeoDataHelper.moveCamera(content[it])
            onBackPressed()
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@FavoriteActivity)
            adapter = recyclerViewAdapter
        }

        val dragHelper = DragHelper {
            remove(content[it])
            content.removeAt(it)
            recyclerViewAdapter.notifyDataSetChanged()
        }

        val itemTouchHelper = ItemTouchHelper(dragHelper)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        CoroutineScope(Dispatchers.Main).launch {
            content = all().await()
        }

        textFavourite_tab.setTextColor(ResourcesCompat.getColor(resources, R.color.checkTab, null))
        imageFavourite_tab.setColorFilter(ResourcesCompat.getColor(resources, R.color.checkTab, null))
        setListeners()
    }

    private fun setListeners() {
        blog_tab.setOnClickListener {
            startActivity(
                Intent(this, BlogActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }

        backHome_favourite.setOnClickListener {
            startActivity(
                Intent(this, MapsActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
    }
}
