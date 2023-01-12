package com.example.myapplication.ui

import android.app.Activity
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.activities.ArticleActivity
import com.example.myapplication.activities.MapsActivity
import com.example.myapplication.data.common.Pollution
import kotlinx.android.synthetic.main.main_pollution.view.*
import kotlinx.android.synthetic.main.rv_blog_article1.view.*
import kotlinx.android.synthetic.main.main_pollution.view.*

class MainPollutionAdapter(val clickListener: ItemClickListener) : RecyclerView.Adapter<MainPollutionAdapter.MainViewHolder>() {

    private val mPollutions = arrayListOf<Pollution>()
    public var views = arrayListOf<MenuView.ItemView>()

    fun putArticles(pollutions: List<Pollution>) {
        mPollutions.clear()
        mPollutions.addAll(pollutions)
    }

    override fun getItemCount() = mPollutions.size

    override fun getItemViewType(position: Int) = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.main_pollution, parent, false)
        return MainViewHolder(itemView = view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(mPollutions[position], clickListener)
    }


    inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val type = itemView.pollutionType_rv
        private val dop = itemView.pollutionDop_rv
        private val icon = itemView.pollutionIcon_rv
        private val color1 = itemView.pollutionColor1_rv
        private val color2 = itemView.pollutionColor2_rv
        private val all = itemView.all
        fun bind(item: Pollution, action: ItemClickListener) {
            if(mPollutions[adapterPosition].takeType() == "Радиационное") {
                type.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9F)
                dop.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7F)

                /*val param = all.layoutParams as ViewGroup.MarginLayoutParams
                param.setMargins(R.dimen.nerad, R.dimen.nerad, R.dimen.rad, R.dimen.nerad)
                all.layoutParams = param*/
            }
            itemView.setOnClickListener{
                action.onItemClick(item, adapterPosition)
            }
            icon.setImageResource(mPollutions[adapterPosition].takeIcon())
            color1.setImageResource(mPollutions[adapterPosition].takeColor())

            Glide.with(color2.context)
                    .load(R.drawable.circle)
                    .fitCenter()
                    .circleCrop()
                    .into(color2)
            type.text = mPollutions[adapterPosition].takeType()
            dop.text = mPollutions[adapterPosition].takeDop()
        }
    }

}
interface ItemClickListener{
    fun onItemClick(item: Pollution, position: Int){}
}
