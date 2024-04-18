package com.ran.dicodingstory.data.remote.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ran.dicodingstory.data.local.entity.StoryResponseItem
import com.ran.dicodingstory.data.local.room.StoryDatabase
import com.ran.dicodingstory.data.remote.request.UserBody
import com.ran.dicodingstory.data.remote.response.DefaultResponse
import com.ran.dicodingstory.data.remote.response.Story
import com.ran.dicodingstory.data.remote.retrofit.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Call

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class QuoteRemoteMediatorTest {

    private var mockApi: ApiService = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            "fake_token",
            mockDb,
            mockApi,
        )
        val pagingState = PagingState<Int, StoryResponseItem>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }



    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}

class FakeApiService : ApiService {
    override fun register(body: UserBody): Call<DefaultResponse> {
        TODO("Not yet implemented")
    }

    override fun login(body: UserBody): Call<DefaultResponse> {
        TODO("Not yet implemented")
    }

    override fun getStoriesWithLocation(token: String, location: Int): Call<DefaultResponse> {
        TODO("Not yet implemented")
    }

    override fun postStory(
        token: String,
        photo: MultipartBody.Part,
        description: RequestBody?,
        lat: RequestBody?,
        lon: RequestBody?
    ): Call<DefaultResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllStory(
        token: String,
        page: Int?,
        size: Int?,
        location: Int?
    ): DefaultResponse {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                i.toString(),
                "name + $i",
                "description $i",
                "photoUrl $i",
                "createdAt $i",
                0.0,
                0.0
            )
            items.add(story)
        }
        val listStory = items.toList()
        return DefaultResponse(
            error = false,
            message = "Success",
            listStory = listStory.subList((page!!.minus(1)) * size!!, (page - 1) * size + size),
            story = null,
            loginResult = null
        )
    }
}
