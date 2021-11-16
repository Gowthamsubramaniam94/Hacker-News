package com.tc.hackernews

import com.tc.hackernews.di.DaggerHackerNewsComponent
import com.tc.hackernews.di.HackerNewsComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

open class Application : DaggerApplication() {

    lateinit var mHackerComponent: HackerNewsComponent

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        initComponent()
        return mHackerComponent
    }

    protected open fun initComponent() {
        mHackerComponent = DaggerHackerNewsComponent.builder().bindApp(this).build()
    }

}