package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.common.Constants.EXTRA_EXPERT_OPINION
import com.example.myapplication.data.common.ExpertOpinion
import com.example.myapplication.data.common.Pollution
import com.example.myapplication.ui.PollutionRvAdapter
import kotlinx.android.synthetic.main.activity_expert_opinion.*

class ExpertOpinionActivity: AppCompatActivity() {

    private lateinit var opinion: ExpertOpinion
    private val adapter = PollutionRvAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expert_opinion)


        // opinion = ExpertOpinion(conclusion = "Заключение", description = "Описание" )
        opinion = intent.getParcelableExtra(EXTRA_EXPERT_OPINION) ?: ExpertOpinion()

        setUi()
        setListeners()
        rvExpertOpinion.adapter = adapter
        adapter.putArticles(pollutions = opinion.pollutions)
    }

    fun setUi() {

        conclusion_expertOpinion.text = opinion.takeConclusion()
        description_expertOpinion.text = opinion.takeDescription()

        Glide.with(this)
            .load(R.drawable.experts_circle)
            .circleCrop()
            .into(pollutionPhoto_expertOpinion)
    }

    fun setListeners() {
        clickableItem_expertOpinion.setOnClickListener {
            startActivity(
                Intent(this, MapsActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
    }
}
