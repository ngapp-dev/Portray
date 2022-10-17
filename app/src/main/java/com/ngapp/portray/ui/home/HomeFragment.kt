package com.ngapp.portray.ui.home

import android.app.ActivityOptions
import android.graphics.Rect
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
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.ngapp.portray.R
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.databinding.FragmentHomeBinding
import com.ngapp.portray.ui.home.adapters.HomePhotoListAdapter
import com.ngapp.portray.utils.ViewBindingFragment
import com.ngapp.portray.utils.adapter.NewLoadStateAdapter
import com.ngapp.portray.utils.launchAndCollectIn
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class HomeFragment :
    ViewBindingFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var homePhotoListAdapter: HomePhotoListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.e("Fragment home start")
        createActionBarMenu()
        binding.retryButton.setOnClickListener {
            homePhotoListAdapter.retry()
        }
        setUpRecyclerView()

        binding.swiperefresh.setOnRefreshListener {
            homePhotoListAdapter.refresh()
            binding.swiperefresh.isRefreshing = false
        }

        viewModel.latestPhotosAndSearch.launchAndCollectIn(viewLifecycleOwner) { photoList ->
            homePhotoListAdapter.submitData(photoList)
        }
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
            homePhotoListAdapter = HomePhotoListAdapter(
                onItemSingleClick = { photo, materialCardView ->
                    showPhotoDetail(photo, materialCardView)
                },
                onItemDoubleClick = { likeImage(it) },
                onLikeImageViewClick = { likeImage(it) }
            )
            adapter = homePhotoListAdapter.withLoadStateFooter(
                footer = NewLoadStateAdapter { homePhotoListAdapter.retry() }
            )

            homePhotoListAdapter.addLoadStateListener { loadState ->
                println("AppDebug: $loadState")
                binding.recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error
            }

            val spacing = resources.getDimensionPixelSize(R.dimen.margin8) / 2;
            setPadding(spacing, spacing, spacing, spacing);
            addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect[spacing, spacing, spacing] = spacing
                }
            })
            val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            layoutManager = manager
        }
    }

    private fun showPhotoDetail(photo: Photo, materialCardView: MaterialCardView) {
        ActivityOptions.makeSceneTransitionAnimation(
            requireActivity(),
            materialCardView,
            materialCardView.transitionName
        ).toBundle()
        val extras = FragmentNavigatorExtras(materialCardView to photo.id)
        val action = HomeFragmentDirections.actionHomeNewFragmentToNavPhotoDetail(
            photo.id,
            photo.urls.regular
        )
        findNavController().navigate(action, extras)
    }

    private fun likeImage(photo: Photo) {
        viewModel.changeLike(
            photoId = photo.id,
            likedByUser = photo.likedByUser,
            likes = photo.likes
        )
//        viewModel.likedFlow.launchAndCollectIn(viewLifecycleOwner){
//            if (it) {
//                homePhotoListAdapter.refresh()
//            }
//        }

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

    private fun showError(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.dismiss)) {
            }.show()
    }

    private fun searchItem(menuItem: MenuItem) {
        val searchView = menuItem.actionView as SearchView

        searchView.apply {
            queryHint = context.getString(R.string.search_photos_hint)

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