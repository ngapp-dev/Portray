package com.ngapp.portray.ui.collections

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.ngapp.portray.R
import com.ngapp.portray.data.db.models.collection.Collection
import com.ngapp.portray.databinding.FragmentCollectionsBinding
import com.ngapp.portray.ui.collections.adapter.CollectionListAdapter
import com.ngapp.portray.utils.ViewBindingFragment
import com.ngapp.portray.utils.adapter.NewLoadStateAdapter
import com.ngapp.portray.utils.launchAndCollectIn
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectionsFragment :
    ViewBindingFragment<FragmentCollectionsBinding>(FragmentCollectionsBinding::inflate) {

    private val viewModel by viewModels<CollectionsViewModel>()
    private lateinit var collectionListAdapter: CollectionListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createActionBarMenu()

        binding.retryButton.setOnClickListener {
            collectionListAdapter.retry()
        }

        binding.swiperefresh.setOnRefreshListener {
            collectionListAdapter.refresh()
            binding.swiperefresh.isRefreshing = false
        }

        setUpRecyclerView()

        viewModel.latestCollectionsAndSearch.launchAndCollectIn(viewLifecycleOwner) { collectionList ->
                collectionListAdapter.submitData(collectionList)
        }
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
            collectionListAdapter = CollectionListAdapter(
                onItemSingleClick = { collection ->
                    showCollectionDetail(collection)
                }
            )
            adapter = collectionListAdapter.withLoadStateFooter(
                footer = NewLoadStateAdapter { collectionListAdapter.retry() }
            )

            collectionListAdapter.addLoadStateListener { loadState ->
                println("AppDebug: $loadState")
                binding.recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error
            }

            val manager = LinearLayoutManager(requireContext())
            layoutManager = manager
        }
    }

    private fun showCollectionDetail(collection: Collection) {
//        ActivityOptions.makeSceneTransitionAnimation(
//            requireActivity(),
//            materialCardView,
//            materialCardView.transitionName
//        ).toBundle()
//        val extras = FragmentNavigatorExtras(materialCardView to collection.id)
        val action =
            CollectionsFragmentDirections.actionNavFavouritesToNavCollectionDetail(collection.id, collection.title)
        findNavController().navigate(action)
    }

    private fun createActionBarMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_search_action_bar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_search -> {
                        searchItem(menuItem)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun searchItem(menuItem: MenuItem) {
        val searchView = menuItem.actionView as SearchView

        searchView.apply {
            queryHint = "Search Collections..."

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.setQuery(query ?: "")
                    clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })

            setOnCloseListener {                // Not working
                viewModel.setQuery("")
                true
            }
        }

        menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                viewModel.setQuery("")
                return true
            }
        })
    }
}