package com.tc.hackernews.di

import com.tc.hackernews.network.ApiInterface
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ProviderModule {

    @Provides
    @Singleton
    fun provideHackerNewsApi(): ApiInterface {
        return Retrofit.Builder()
            .baseUrl("https://hacker-news.firebaseio.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }

    @Provides
    fun provideDisposable(): CompositeDisposable {
        return CompositeDisposable()
    }
}