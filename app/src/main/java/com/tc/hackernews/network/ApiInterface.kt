package com.tc.hackernews.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterface {

    @GET("v0/topstories.json")
    fun getTopStories(): Single<List<Long>>

    @GET("/v0/item/{article_id}.json")
    fun getStoriesData(@Path("article_id") id: String): Single<Story>

    @GET("/v0/item/{comment_id}.json")
    fun getComment(@Path("comment_id") id: String): Single<Comment>
}