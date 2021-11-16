package com.tc.hackernews.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.hackernews.R
import com.tc.hackernews.network.Story
import java.util.*

/**
 * Hacker news list adapter
 */
class HackerNewsAdapter(
    private val mLayoutInflater: LayoutInflater,
    private val mMoreClickListener: () -> Unit,
    private val mStoryClickListener: (story: Story) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    /**
     * List of stories
     */
    private var mListOfStories = LinkedList<Story>()
    private var mTempListOfStories = LinkedList<Story>()

    /**
     * True if there is "More" view to be shown in bottom of list to load more stories. False, If no more stories left
     * to load
     */
    private var mShowAddMore = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == R.layout.more_item) {
            return MoreItemViewHolder(
                mLayoutInflater.inflate(viewType, parent, false),
                mMoreClickListener
            )
        }
        return HackerNewsItemViewHolder(
            mLayoutInflater.inflate(viewType, parent, false),
            mStoryClickListener
        )
    }

    override fun getItemCount(): Int {
        return if (mShowAddMore) {
            mListOfStories.size + 1
        } else {
            mListOfStories.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == R.layout.news_item) {
            (holder as HackerNewsItemViewHolder).bind(mListOfStories[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == mListOfStories.size) {
            R.layout.more_item
        } else {
            R.layout.news_item
        }
    }

    /**
     * Add the stories to the current list of stories
     */
    fun addStories(listOfStories: List<Story>?) {
        listOfStories?.let {
            mListOfStories.clear()
            mListOfStories.addAll(it)
            mTempListOfStories.clear()
            mTempListOfStories.addAll(it)
        }
    }

    /**
     * Flag to identify that we need to show "More" view at the bottom of stories list
     */
    fun showAddMore(boolean: Boolean) {
        mShowAddMore = boolean
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    mListOfStories = mTempListOfStories
                } else {
                    val resultList = LinkedList<Story>()
                    for (row in mTempListOfStories) {
                        if (row.storyTitle.toLowerCase(Locale.ROOT)
                                .contains(
                                    constraint.toString().toLowerCase(Locale.ROOT)
                                ) || row.author.toLowerCase(Locale.ROOT)
                                .contains(constraint.toString().toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    mListOfStories = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = mListOfStories
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                mListOfStories = results?.values as LinkedList<Story>
                notifyDataSetChanged()
            }
        }
    }
}