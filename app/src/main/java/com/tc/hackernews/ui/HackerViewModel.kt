package com.tc.hackernews.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tc.hackernews.network.ApiInterface
import com.tc.hackernews.network.Comment
import com.tc.hackernews.network.Story
import com.tc.hackernews.ui.home.model.Resource
import com.tc.hackernews.ui.home.model.ResourceState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*
import javax.inject.Inject

/**
 * Gowtham Raj
 * Created 12/11/2021
 * Hacker viewmodel containing business logic for hacker news
 */

class HackerViewModel @Inject constructor(
    private val compositeDisposable: CompositeDisposable,
    private val hackerApi: ApiInterface
) : ViewModel() {

    companion object {
        const val MAX_STORIES_CHUNK_SIZE = 20L
    }

    // List of stories live data
    val mStoryLiveData: MutableLiveData<Resource<List<Story>>> = MutableLiveData()

    // List of stories live data
    private val mCommentsLiveData: MutableLiveData<Resource<List<Comment>>> = MutableLiveData()

    // Live data to notify if there is more stories to load
    private val mAddMoreItemLiveData: MutableLiveData<Boolean> = MutableLiveData()

    private val mStorySelectedLiveData: MutableLiveData<Story> = MutableLiveData()

    private val listOfNewsId = LinkedList<Long>()

    private val listOfStories = LinkedList<Story>()

    private val listOfComments = LinkedList<Comment>()

    private var commentDisposable = CompositeDisposable();

    // Get all the Story IDs from the server initially then processing all the stories data from server based on the story ID.
    @DelicateCoroutinesApi
    fun getStories() {
        if (mStoryLiveData.value != null && mStoryLiveData.value?.status == ResourceState.LOADING) {
            return
        }
        listOfStories.clear()
        mStoryLiveData.postValue(Resource(ResourceState.LOADING, null))
        compositeDisposable.add(
            hackerApi.getTopStories()
                .doOnSuccess { listOfNewsId.addAll(it) }
                .toObservable()
                .flatMap { Observable.fromIterable(it) }
                .fetchStoriesFromObservable()
                .toList()
                .subscribe(getListOfStoriesObserver(), getThrowable()))
    }

    // Get the ID from the listOfNewsId and fetch stories from server
    @DelicateCoroutinesApi
    fun getMoreStories() {
        mStoryLiveData.postValue(Resource(ResourceState.MORE_LOADING, null))
        val subList = LinkedList<Long>()
        subList.addAll(listOfNewsId.subList(0, listOfNewsId.size))
        compositeDisposable.add(
            Observable.fromIterable(subList)
                .fetchStoriesFromObservable()
                .toList()
                .subscribe(getListOfStoriesObserver(), getThrowable())
        )
    }

    // Set selected story
    fun setSelectedStory(story: Story) {
        this.mStorySelectedLiveData.postValue(story)
    }

    // Load comments from API for Story item
    fun getStoryComment(story: Story) {
        commentDisposable.dispose()

        //Clear the previous comments
        commentDisposable = CompositeDisposable()
        listOfComments.clear()

        mCommentsLiveData.postValue(Resource(ResourceState.LOADING, null))
        //If there are no comments then simply show empty view
        if (story.kids == null || story.kids!!.isEmpty()) {
            mCommentsLiveData.postValue(Resource(ResourceState.HIDE_LOADING, listOfComments))
            return
        }
        commentDisposable.add(
            Observable.fromIterable(story.kids)
                .flatMap { hackerApi.getComment(it).toObservable() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("Comment Loaded:", "${it.text}")
                    listOfComments.add(it)
                    mCommentsLiveData.postValue(
                        Resource(
                            ResourceState.MORE_LOADING,
                            listOfComments
                        )
                    )
                }, {
                    Log.e(it.toString(), "Error in loading comments")
                    mCommentsLiveData.postValue(Resource(ResourceState.ERROR, null))
                }, {
                    mCommentsLiveData.postValue(
                        Resource(
                            ResourceState.HIDE_LOADING,
                            listOfComments
                        )
                    )
                })
        )
    }

    // Observer for stories list
    @DelicateCoroutinesApi
    private fun getListOfStoriesObserver(): (t: List<Story>) -> Unit {
        return {
            listOfStories.addAll(it)

            mStoryLiveData.postValue(Resource(ResourceState.SUCCESS, listOfStories))
            mAddMoreItemLiveData.postValue(!listOfNewsId.isEmpty())
        }
    }

    private fun getThrowable(): ((t: Throwable) -> Unit)? {
        return {
            Log.e(it.toString(), "Error")
            mStoryLiveData.postValue(Resource(ResourceState.ERROR, null))
        }
    }

    // Extension function to fetch the stories from server with max chunk size
    private fun <T> Observable<T>.fetchStoriesFromObservable(): Observable<Story> {
        return take(MAX_STORIES_CHUNK_SIZE)
            .getStoryFromServer(hackerApi)
            .doOnNext {
                listOfNewsId.remove(it.id.toLong())
                Log.d("Story Fetched :", it.storyTitle)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    //Load stories from server
    private fun <T> Observable<T>.getStoryFromServer(hackerApi: ApiInterface): Observable<Story> {
        return concatMap { hackerApi.getStoriesData(it.toString()).toObservable() }
    }

    // Clearing all data
    override fun onCleared() {
        super.onCleared()
        listOfNewsId.clear()
        compositeDisposable.dispose()
    }

    // Load more data
    fun getAddMoreItemLiveData(): LiveData<Boolean> {
        return mAddMoreItemLiveData
    }

    //Live data to observe list of stories
    fun getStoryListLiveData(): LiveData<Resource<List<Story>>> {

        return mStoryLiveData
    }

    // Comments section live data
    fun getCommentsLiveData(): LiveData<Resource<List<Comment>>> {
        return mCommentsLiveData
    }

    // Get the story live data which was selected
    fun getSelectedStoryLiveData(): LiveData<Story> {
        return mStorySelectedLiveData
    }

   // Get list of already loaded comments
    fun getListOfComments(): List<Comment> {
        return listOfComments
    }

}
