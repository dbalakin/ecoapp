package com.example.myapplication.ui.blog

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.common.BlogArticle
import kotlinx.android.synthetic.main.rv_blog_article1.view.*

class BlogRvAdapter(
    private val articleListener: OnArticleClickListener
): RecyclerView.Adapter<BlogRvAdapter.ArticleViewHolder>() {

    private val mArticles = arrayListOf<BlogArticle>()

    fun putArticles(blogArticles: ArrayList<BlogArticle>) {
        mArticles.clear()
        mArticles.addAll(blogArticles)
    }

    override fun getItemCount() = mArticles.size

    override fun getItemViewType(position: Int): Int {
        if (position % 2 == 0)
            return 1
        else
            return 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                if (viewType == 1)
                    R.layout.rv_blog_article1
                else
                    R.layout.rv_blog_article2,
                parent,
                false
            )
        return ArticleViewHolder(itemView = view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind()
    }


    inner class ArticleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val textDate = itemView.articleDate_rvBlog
        private val textTitle = itemView.articleTitle_rvBlog
        private val textAuthor = itemView.articleAuthor_rvBlog
        private val imageArticle = itemView.articlePhoto_rvBlog
        private val photoView = itemView.articlePhotoView_rvBlog
        private val clickableLayout = itemView.transparentClickableItem_rvBlog

        init {
            clickableLayout.setOnClickListener {
                articleListener.onClick(article = mArticles[adapterPosition])
            }
        }

        fun bind() {
            if (itemViewType == 1 && adapterPosition == 0)
                photoView.visibility = INVISIBLE
            else
                photoView.visibility = VISIBLE

            textDate.text = mArticles[adapterPosition].takeDate()
            textTitle.text = mArticles[adapterPosition].takeTitle()
            textAuthor.text = mArticles[adapterPosition].takeAuthor()

            if (itemViewType == 1) {
                Glide.with(textDate)
                    .load(R.drawable.article1)
                    .fitCenter()
                    .circleCrop()
                    .into(imageArticle)
            } else if (itemViewType == 2) {
                Glide.with(textDate)
                    .load(R.drawable.article2)
                    .fitCenter()
                    .circleCrop()
                    .into(imageArticle)
            }
        }
    }

    interface OnArticleClickListener {
        fun onClick(article: BlogArticle)
    }
}