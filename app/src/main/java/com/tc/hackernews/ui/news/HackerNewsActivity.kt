package com.tc.hackernews.ui.news

import android.os.Bundle
import com.example.hackernews.R
import com.tc.hackernews.network.Story
import dagger.android.support.DaggerAppCompatActivity

class HackerNewsActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news_activity)
        if (savedInstanceState == null) {
            val newsFragment =
                supportFragmentManager.findFragmentById(R.id.story_detail_fragment) as NewsFragment
            intent.getBundleExtra(BUNDLE)?.getParcelable<Story>(STORY)?.let {
                newsFragment.refresh(it)
            }
        }
    }

    companion object {
        const val BUNDLE = "bundle"
        const val STORY = "story"
    }
}