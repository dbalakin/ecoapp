package com.example.myapplication.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.common.MarkersInfo
import com.example.myapplication.data.getSearchName
import com.example.myapplication.objects.GeoDataHelper
import kotlinx.android.synthetic.main.markers_layout.view.*

class MarkersRecyclerViewAdapter(
    val info: List<MarkersInfo>,
    private val callback: () -> Unit
): RecyclerView.Adapter<MarkersRecyclerViewAdapter.MarkersHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkersHolder {
        context = parent.context
        return MarkersHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.markers_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MarkersHolder, position: Int) {
        with(holder) {
            content.text = info[position].content
            image.setImageResource(context.resources.getIdentifier(info[position].imageName, "drawable", context.packageName))
        }
        holder.itemView.setOnClickListener {
            GeoDataHelper.loadMarkers(getSearchName(info[position].content))
            callback()
        }
    }

    override fun getItemCount() = info.size

    inner class MarkersHolder(view: View): RecyclerView.ViewHolder(view) {
        val image = view.findViewById(R.id.markers_img) as ImageView
        val content = view.findViewById(R.id.markers_content) as TextView

    }
}
