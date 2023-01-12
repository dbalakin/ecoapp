package com.example.myapplication.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.MapsHelper
import com.example.myapplication.data.common.Pollution
import com.example.myapplication.objects.GeoDataHelper

class TimeDialog(val pollution: Pollution) : DialogFragment() {

    lateinit var dialog: AlertDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val view = requireActivity().layoutInflater.inflate(R.layout.time_pick_dialog, null)
            val timeRecyclerView: RecyclerView = view.findViewById(R.id.time_pick_recycler)
            timeRecyclerView.apply {
                adapter = ChooseTimeAdapter(pollution, MapsHelper.getTimes(pollution.type!!))
                layoutManager = LinearLayoutManager(context)
            }
            builder.setView(view)
            dialog = builder.create()
            dialog
        } ?: throw IllegalStateException("Activity not null")
    }

    private inner class ChooseTimeAdapter(val pollution: Pollution, val contentList: List<String>) :
        RecyclerView.Adapter<ChooseTimeAdapter.ChooseTimeViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseTimeViewHolder {
            return ChooseTimeViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.time_pick_tile, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ChooseTimeViewHolder, position: Int) {
            holder.content.text = contentList[position]
            holder.content.setOnClickListener {
                GeoDataHelper.updateUI(
                    MapsHelper.parseMaps(pollution.type + "|${contentList[position]}")
                )
                GeoDataHelper.pollutionType = "air${position + 1}"
                this@TimeDialog.dialog.hide()
            }
        }

        override fun getItemCount() = contentList.size

        inner class ChooseTimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val content: TextView = view.findViewById(R.id.content_time_tile)
        }
    }
}
