package com.tc.hackernews.ui.news

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hackernews.R
import com.tc.hackernews.network.Comment
import com.tc.hackernews.network.Story
import com.tc.hackernews.ui.home.getByTimeAgo
import com.tc.hackernews.ui.home.model.ResourceState
import com.tc.hackernews.ui.HackerViewModel
import dagger.android.support.DaggerFragment
import java.util.*import kotlinx.android.synthetic.main.news_fragment.*
import javax.inject.Inject

class NewsFragment : DaggerFragment() {

    @Inject
    lateinit var mVMFactory: ViewModelProvider.Factory

    private lateinit var mHackerViewModel: HackerViewModel

    private lateinit var mCommentsAdapter: CommentsAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.news_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mHackerViewModel = ViewModelProvider(this, mVMFactory)[HackerViewModel::class.java]
        initCommentsRecyclerView()
        mHackerViewModel.getCommentsLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                ResourceState.LOADING -> showLoading()
                ResourceState.MORE_LOADING -> {
                    showComments(it.data)
                    comments_loading_container.visibility = View.VISIBLE
                }
                ResourceState.ERROR -> showErrorScreen()
                ResourceState.HIDE_LOADING -> {
                    showComments(it.data)
                    hideLoading()
                }
                else -> hideCommentsContainer()
            }
        })

        mHackerViewModel.getSelectedStoryLiveData().observe(viewLifecycleOwner, Observer {
            updateViews(it)
        })
    }

    private fun hideLoading() {
        //Check if data was loaded previously but due to config change it should be loaded again from view model
        if (mCommentsAdapter.itemCount == 0 && mHackerViewModel.getListOfComments()?.size != 0) {
            showComments(mHackerViewModel.getListOfComments())
        } else if (mHackerViewModel.getListOfComments()?.size == 0) {
            no_comments_textview.visibility = View.VISIBLE
        }

        comments_loading_container.visibility = View.GONE
    }

    private fun showErrorScreen() {
        if (mCommentsAdapter.itemCount > 0) {
            hideCommentsContainer()
        }
    }

    /**
     * Show comments list
     */
    private fun showComments(data: List<Comment>?) {
        mCommentsAdapter.setListOfComments(data!!)

        if (comments_list_container.visibility != View.VISIBLE) {
            comments_list_container.visibility = View.VISIBLE
        }
    }

    /**
     * Show loading screen
     */
    private fun showLoading() {
        comments_list_container.visibility = View.GONE
        comments_loading_container.visibility = View.VISIBLE
        no_comments_textview.visibility = View.GONE
    }

    /**
     * Hide comments container and show no comments view
     */
    private fun hideCommentsContainer() {
        comments_list_container.visibility = View.GONE
        comments_loading_container.visibility = View.GONE
        no_comments_textview.visibility = View.VISIBLE
    }

    /**
     * Initialize comments recycler view
     */
    private fun initCommentsRecyclerView() {
        mCommentsAdapter = CommentsAdapter(LayoutInflater.from(this.activity))
        val layoutManager = LinearLayoutManager(this.activity, RecyclerView.VERTICAL, false)
        comments_recycler_view.adapter = mCommentsAdapter
        comments_recycler_view.layoutManager = layoutManager
        comments_recycler_view.isNestedScrollingEnabled = false
        val dividerItemDecoration = DividerItemDecoration(this.activity, layoutManager.orientation)
        comments_recycler_view.addItemDecoration(dividerItemDecoration)
    }

    /**
     * Refresh the comments view
     */
    fun refresh(story: Story) {
        initCommentsRecyclerView()
        //Load the comments
        mHackerViewModel.setSelectedStory(story)
        mHackerViewModel.getStoryComment(story)
    }

    private fun updateViews(story: Story) {
        story_title_text_view.text = story.storyTitle
        val kidsSize: Int = story.kids?.size ?: 0
        val hours = Calendar.getInstance().getByTimeAgo(story.storyTime)
        story_description_text_view.text = resources.getString(
            R.string.description,
            hours,
            story.author,
            kidsSize,
            resources.getQuantityString(R.plurals.comments_plural, kidsSize)
        )
    }
}