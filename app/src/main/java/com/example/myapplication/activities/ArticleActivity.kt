package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.common.BlogArticle
import com.example.myapplication.data.common.Constants.EXTRA_ARTICLE
import kotlinx.android.synthetic.main.activity_article.*

class ArticleActivity: AppCompatActivity() {

    private lateinit var blog: BlogArticle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        blog = intent.getParcelableExtra(EXTRA_ARTICLE) ?: BlogArticle()
        setUi()
        setListeners()
    }

    fun setUi() {
        articleTitle_blogArticle.text = blog.takeTitle()
        articleDate_blogArticle.text = blog.takeDate()
        articleDescription_blogArticle.text = blog.takeDescription()
        articleAuthor_blogArticle.text = blog.takeAuthor()

        Glide.with(this)
            .load(R.drawable.ic_launcher_background)
            .circleCrop()
            .into(articlePhoto_articleBlog)
    }

    fun setListeners() {
        clickableItem_blogArticle.setOnClickListener {
            startActivity(
                Intent(this, BlogActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
    }
}
