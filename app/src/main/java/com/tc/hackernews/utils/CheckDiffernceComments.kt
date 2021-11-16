package com.tc.hackernews.utils

import androidx.recyclerview.widget.DiffUtil
import com.tc.hackernews.network.Comment

class CheckDiffernceComments(private val oldListOfComments: List<Comment>, private val newListOfComments: List<Comment>) :
    DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldListOfComments[oldItemPosition].id == newListOfComments[newItemPosition].id
    }

    override fun getOldListSize(): Int {
        return oldListOfComments.size
    }

    override fun getNewListSize(): Int {
        return newListOfComments.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldListOfComments[oldItemPosition] == newListOfComments[newItemPosition]
    }

}