package com.example.myapplication.ui.favorite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.sql_helper.FavoriteInfo
import kotlinx.android.synthetic.main.favorite_tile.view.*

class FavoriteRecyclerAdapter(
    var content: MutableList<FavoriteInfo> = mutableListOf(),
    private val updateMap: (Int) -> Unit
) : RecyclerView.Adapter<FavoriteRecyclerAdapter.FavoriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        return FavoriteViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.favorite_tile, parent, false))
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        with(holder) {
            place.text = content[position].place
            city.text = content[position].address
            image.setOnClickListener { updateMap(position) }
        }
    }

    override fun getItemCount() = content.size

    fun updateContent(newContent: MutableList<FavoriteInfo>) {
        content = newContent
        notifyDataSetChanged()
    }

    fun clear() {
        content.clear()
        notifyDataSetChanged()
    }

    inner class FavoriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val place: TextView = view.favorite_adr
        val city: TextView = view.favorite_city
        val image: ImageView = view.favorite_image
    }
}
