package com.example.myapplication.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.myapplication.R
import com.example.myapplication.data.common.BlogArticle
import com.example.myapplication.data.common.Constants.EXTRA_ARTICLE
import com.example.myapplication.ui.blog.BlogRvAdapter
import com.example.myapplication.ui.fake_data.articles
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.fragment_blog.*
import kotlinx.android.synthetic.main.fragment_blog.favorite_main
import java.util.*

class BlogActivity: AppCompatActivity(), BlogRvAdapter.OnArticleClickListener {

    private val adapter = BlogRvAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_blog)
        rvBlog.adapter = adapter
        adapter.putArticles(articles)

        textBlog_tab.setTextColor(ResourcesCompat.getColor(resources, R.color.checkTab, null))
        imageBlog_tab.setColorFilter(ResourcesCompat.getColor(resources, R.color.checkTab, null))
        setListeners()
    }

    private fun setListeners() {
        favorite_tab.setOnClickListener {
            startActivity(
                Intent(this, FavoriteActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }

        backHome_blog.setOnClickListener {
            startActivity(
                Intent(this, MapsActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
    }


    override fun onClick(article: BlogArticle) {
        startActivity(
            Intent(this, ArticleActivity::class.java)
                .putExtra(EXTRA_ARTICLE, article)
        )
    }
}
