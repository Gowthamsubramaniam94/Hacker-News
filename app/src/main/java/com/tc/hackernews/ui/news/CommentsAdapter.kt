package com.tc.hackernews.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hackernews.R
import com.tc.hackernews.utils.CheckDiffernceComments
import com.tc.hackernews.network.Comment
import java.util.*

class CommentsAdapter(private val inflater: LayoutInflater) : RecyclerView.Adapter<CommentsViewHolder>() {

    private val comments: LinkedList<Comment> = LinkedList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        return CommentsViewHolder(inflater.inflate(R.layout.comments_item_view, parent, false))
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        comments[position].let { holder.bind(it) }
    }

    fun setListOfComments(comment: List<Comment>) {
        val calculateDiff = DiffUtil.calculateDiff(CheckDiffernceComments(this.comments, comment))
        this.comments.clear()
        this.comments.addAll(comment)
        calculateDiff.dispatchUpdatesTo(this)
    }

}