package com.example.myapplication.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.common.Pollution
import kotlinx.android.synthetic.main.rv_blog_article1.view.*
import kotlinx.android.synthetic.main.rv_pollution.view.*

class PollutionRvAdapter : RecyclerView.Adapter<PollutionRvAdapter.ExpertOpinionViewHolder>() {

    private val mPollutions = arrayListOf<Pollution>()

    fun putArticles(pollutions: List<Pollution>) {
        mPollutions.clear()
        mPollutions.addAll(pollutions)
    }

    override fun getItemCount() = mPollutions.size

    override fun getItemViewType(position: Int) = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpertOpinionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_pollution, parent, false)
        return ExpertOpinionViewHolder(itemView = view)
    }

    override fun onBindViewHolder(holder: ExpertOpinionViewHolder, position: Int) {
        holder.bind()
    }


    inner class ExpertOpinionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val type = itemView.pollutionType_rv
        private val icon = itemView.pollutionIcon_rv

        fun bind() {
            type.text = mPollutions[adapterPosition].takeType()


            Glide.with(icon.context)
                .load(R.drawable.ic_launcher_background)
                .fitCenter()
                .circleCrop()
                .into(icon)
        }
    }
}
