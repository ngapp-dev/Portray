package com.ngapp.portray.ui.collections.collection_detail

import android.app.ActivityOptions
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.card.MaterialCardView
import com.ngapp.portray.R
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.databinding.FragmentHomeBinding
import com.ngapp.portray.ui.MainActivity
import com.ngapp.portray.ui.collections.collection_detail.adapter.CollectionPhotoListAdapter
import com.ngapp.portray.utils.adapter.NewLoadStateAdapter
import com.ngapp.portray.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectionDetailFragment :
    ViewBindingFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val args: CollectionDetailFragmentArgs by navArgs()
    private val viewModel by viewModels<CollectionDetailViewModel>()
    private lateinit var collectionPhotoListAdapter: CollectionPhotoListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPhotoListByCollectionId(args.id)
        if (!args.title.isNullOrEmpty()) {
            (requireActivity() as MainActivity).supportActionBar?.title = args.title
        }

        binding.retryButton.setOnClickListener {
            collectionPhotoListAdapter.retry()
        }
        setUpRecyclerView()
        viewModel.photoListByCollectionId.launchAndCollectIn(viewLifecycleOwner) { photoList ->
            photoList.let {
                collectionPhotoListAdapter.submitData(it)
            }
        }
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
            collectionPhotoListAdapter = CollectionPhotoListAdapter(
                onItemSingleClick = { photo, materialCardView ->
                    showPhotoDetail(photo, materialCardView)
                },
                onItemDoubleClick = { likeImage(it) },
                onLikeImageViewClick = { likeImage(it) }
            )
            adapter = collectionPhotoListAdapter.withLoadStateFooter(
                footer = NewLoadStateAdapter { collectionPhotoListAdapter.retry() }
            )

            collectionPhotoListAdapter.addLoadStateListener { loadState ->
                println("AppDebug: $loadState")
                binding.recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error
            }
            val spacing = resources.getDimensionPixelSize(R.dimen.margin8) / 2;
            setPadding(spacing, spacing, spacing, spacing);
            addItemDecoration(object : RecyclerView.ItemDecoration() {
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
        val action =
            CollectionDetailFragmentDirections.actionNavCollectionDetailToNavPhotoDetail2(photo.id, photo.urls.regular)
        findNavController().navigate(action, extras)
    }

    private fun likeImage(photo: Photo) {
        viewModel.changeLike(photoId = photo.id, likedByUser = photo.likedByUser, likes = photo.likes)
    }
}