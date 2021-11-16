package com.tc.hackernews.ui.home

import android.content.Intent
import android.os.Bundle
import com.example.hackernews.R
import com.tc.hackernews.network.Story
import com.tc.hackernews.ui.news.HackerNewsActivity
import com.tc.hackernews.ui.news.NewsFragment
import dagger.android.support.DaggerAppCompatActivity

/**
 * Main activity displaying the hacker news
 */
class HackerNewsListActivity : DaggerAppCompatActivity(), StorySelectionListener {

    private var mIsTwoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hacker_news)
        mIsTwoPane = supportFragmentManager.findFragmentById(R.id.story_detail_fragment) != null
    }

    override fun onStorySelected(story: Story) {
        if (mIsTwoPane) {
            (supportFragmentManager.findFragmentById(R.id.story_detail_fragment) as NewsFragment).refresh(story)
        } else {
            val intent = Intent(this, HackerNewsActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(HackerNewsActivity.STORY, story)
            intent.putExtra(HackerNewsActivity.BUNDLE, bundle)
            startActivity(intent)
        }
    }
}

interface StorySelectionListener {
    fun onStorySelected(story: Story)
}