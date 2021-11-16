package com.example.hackernews

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tc.hackernews.network.ApiInterface
import com.tc.hackernews.network.Comment
import com.tc.hackernews.network.Story
import com.tc.hackernews.ui.HackerViewModel
import com.tc.hackernews.ui.home.model.ResourceState
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.DelicateCoroutinesApi
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.lang.Exception
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Rule
    @JvmField
    var testSchedulerRule = RxSchedulerRule()

    @Mock
    lateinit var mApiInterface: ApiInterface

    lateinit var mCompositeDisposable: CompositeDisposable

    lateinit var mHackerViewModel: HackerViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        mCompositeDisposable = CompositeDisposable()
        mHackerViewModel = HackerViewModel(mCompositeDisposable, mApiInterface)
    }

    @Test
    fun testGetStories() {
        val listOfIds = mutableListOf<Long>()
        listOfIds.add(4999L)
        listOfIds.add(5000L)
        Mockito.`when`(mApiInterface.getTopStories()).thenReturn(Single.just(listOfIds))

        val mapOfStories = mutableMapOf<Long, Story>()
        mapOfStories[4999L] = Story(
            "Gowtham",
            "4999",
            Collections.emptyList(),
            7,
            111427,
            "Hello Gowtham",
            "Comic",
            "https://developer.android.com/"
        )
        mapOfStories[5000L] = Story(
            "Raj",
            "5000",
            Collections.emptyList(),
            25,
            111432,
            "Hello Raj",
            "Comic",
            "https://developer.android.com/"
        )
        var loadingShown = false
        Mockito.`when`(mApiInterface.getStoriesData("4999")).thenReturn(Single.just(mapOfStories[4999]))
        Mockito.`when`(mApiInterface.getStoriesData("5000")).thenReturn(Single.just(mapOfStories[5000]))
        mHackerViewModel.getStoryListLiveData().observeForever {
            if (!loadingShown) {
                assertEquals(ResourceState.LOADING, it.status)
                loadingShown = true
            } else {
                assertEquals(ResourceState.SUCCESS, it.status)
                for (story in it.data!!) {
                    assert(mapOfStories[story.id.toLong()] == story)
                }
            }
        }
        mHackerViewModel.getStories()
        assert(loadingShown)
    }

    @DelicateCoroutinesApi
    @Test
    fun testGetMoreStories() {
        val listOfIds = mutableListOf<Long>()
        listOfIds.add(4999L)
        listOfIds.add(5000L)
        Mockito.`when`(mApiInterface.getTopStories()).thenReturn(Single.just(listOfIds))

        val mapOfStories = mutableMapOf<Long, Story>()
        mapOfStories[4999L] = Story(
            "Gowtham",
            "4999",
            Collections.emptyList(),
            12,
            204198,
            "Hello Gowtham",
            "Comic",
            "https://developer.android.com/"
        )
        mapOfStories[5000L] = Story(
            "Raj",
            "5000",
            Collections.emptyList(),
            12,
            204198,
            "Hello Raj",
            "Comic",
            "https://developer.android.com/"
        )

        Mockito.`when`(mApiInterface.getStoriesData("4999")).thenReturn(Single.just(mapOfStories[4999]))
        Mockito.`when`(mApiInterface.getStoriesData("5000")).thenReturn(Single.just(mapOfStories[5000]))

        var loadingShown = false
        mHackerViewModel.getStoryListLiveData().observeForever {
            if (!loadingShown) {
                assertEquals(ResourceState.MORE_LOADING, it.status)
                loadingShown = true
            } else {
                assertEquals(ResourceState.SUCCESS, it.status)
                for (story in it.data!!) {
                    assert(mapOfStories[story.id.toLong()] == story)
                }
            }
        }
        mHackerViewModel.getAddMoreItemLiveData().observeForever {
            assert(!it)
        }
        mHackerViewModel.getMoreStories()
        assert(loadingShown)
    }

    @Test
    fun testLoadingComments() {
        val story = Story(
            "Gowtham",
            "4999",
            Arrays.asList("7000"),
            12,
            213123,
            "Hello Gowtham",
            "Comic",
            "https://developer.android.com/"
        )
        val comment = Comment("by", "7000", "Balu", "This is a test comment", "Comic", 123124L, false)
        Mockito.`when`(mApiInterface.getComment("7000")).thenReturn(Single.just(comment))

        var loadingShown = false
        mHackerViewModel.getCommentsLiveData().observeForever {
            if (!loadingShown) {
                assertEquals(ResourceState.LOADING, it.status)
                loadingShown = true
            } else {
                assert(it.status == ResourceState.SUCCESS)
                assert(it.data?.size == 1)
                assert(it.data?.get(0) == comment)
            }
        }
        mHackerViewModel.getStoryComment(story)
    }

    @Test
    fun testLoadingCommentError() {
        val story = Story(
            "Gowtham",
            "4999",
            Arrays.asList("7000"),
            12,
            213123,
            "Hello Gowtham",
            "Comic",
            "https://developer.android.com/"
        )
        Mockito.`when`(mApiInterface.getComment("7000")).thenReturn(Single.error(Exception("Runitime exception")))
        var loadingShown = false
        mHackerViewModel.getCommentsLiveData().observeForever {
            if (!loadingShown) {
                assertEquals(ResourceState.LOADING, it.status)
                loadingShown = true
            } else {
                assert(it.status == ResourceState.ERROR)
            }
        }
        mHackerViewModel.getStoryComment(story)
    }
}