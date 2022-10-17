package com.ngapp.portray.ui.home.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ngapp.portray.data.Api
import com.ngapp.portray.data.db.models.photo.Photo
import retrofit2.HttpException
import java.io.IOException

class SearchPhotoPagingSource(
    private val api: Api,
    private val query: String
) : PagingSource<Int, Photo>() {

    private val initialPage = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {

        val position = params.key ?: initialPage

        return try {
            val response = api.getSearchPhotos(query, position, params.loadSize)
            LoadResult.Page(
                data = response.result,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (response.result.isEmpty()) null else position + 1
            )

        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}