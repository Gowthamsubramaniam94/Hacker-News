package com.tc.hackernews.ui.news

import android.os.Build
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.hackernews.R
import com.tc.hackernews.network.Comment
import com.tc.hackernews.ui.home.getByTimeAgo
import java.util.*

/**
 * Comments view holder to be displayed on news description view
 */
class CommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val mCommentsTextView: TextView = itemView.findViewById(R.id.comment_text_view)

    private val mAuthorTextView: TextView = itemView.findViewById(R.id.author_text_view)

    private val mCurrentCalendar = Calendar.getInstance()

    /**
     * Bind the comments on view
     */
    fun bind(comment: Comment) {
        if (!comment.isDeleted) {
            Log.d("Binding Comment: %s", comment.text!!)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mCommentsTextView.text = Html.fromHtml(comment.text, Html.FROM_HTML_MODE_LEGACY)
            } else {
                mCommentsTextView.text = HtmlCompat.fromHtml(comment.text!!,HtmlCompat.FROM_HTML_MODE_LEGACY)
            }

            mAuthorTextView.text = String.format("%s %s", comment.author, mCurrentCalendar.getByTimeAgo(comment.time))
        } else {
            Log.d("Binding Comment: %s", "Comment is Deleted")
            mCommentsTextView.text = mAuthorTextView.context.getString(R.string.comment_is_deleted)
            mAuthorTextView.text = ""
        }
    }
}