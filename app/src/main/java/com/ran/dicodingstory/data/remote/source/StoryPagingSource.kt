package com.ran.dicodingstory.data.remote.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ran.dicodingstory.data.remote.response.Story
import com.ran.dicodingstory.data.remote.retrofit.ApiService
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class StoryPagingSource(private val token: String, private val apiService: ApiService,private val callback: (e: Exception)->Unit) : PagingSource<Int, Story>() {
    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        val page = params.key ?: 1
        val size = params.loadSize
        return try {
            val response = apiService.getAllStory(token = "Bearer $token", page = page, size = size)
            val stories = response.listStory
            LoadResult.Page(
                data = stories!!,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (stories.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            callback(e)
            LoadResult.Error(e)
        } catch (e: HttpException) {
            callback(e)
            LoadResult.Error(e)
        }
    }

}