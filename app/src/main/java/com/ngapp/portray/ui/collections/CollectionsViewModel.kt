package com.ngapp.portray.ui.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ngapp.portray.data.repository.CollectionRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    private val collectionsRepository: CollectionRepositoryImpl
) : ViewModel() {


    private val latestCollections =
        collectionsRepository.getLatestCollections(20).cachedIn(viewModelScope)
    private val searchQuery = MutableStateFlow<String?>(null)

    val latestCollectionsAndSearch
        get() = searchQuery.flatMapLatest { query ->
            if (query.isNullOrEmpty()) {
                latestCollections
            } else {
                collectionsRepository.getSearchCollections(query, 10).cachedIn(viewModelScope)
            }
        }


    fun setQuery(query: String) {
        if (searchQuery.value == query) return
        searchQuery.value = query
    }
}