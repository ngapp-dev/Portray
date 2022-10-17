package com.ngapp.portray.ui.profile.view_pager.user_collections

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.ngapp.portray.data.db.models.collection.Collection
import com.ngapp.portray.databinding.FragmentProfileCollectionListPageBinding
import com.ngapp.portray.ui.profile.ProfileFragmentDirections
import com.ngapp.portray.ui.profile.ProfileViewModel
import com.ngapp.portray.ui.profile.view_pager.user_collections.adapter.UserCollectionListAdapter
import com.ngapp.portray.ui.profile.view_pager.user_liked_photos.UserLikedPhotoListPageFragment
import com.ngapp.portray.ui.profile.view_pager.user_photos.UserPhotoListPageFragment
import com.ngapp.portray.utils.ViewBindingFragment
import com.ngapp.portray.utils.adapter.NewLoadStateAdapter
import com.ngapp.portray.utils.launchAndCollectIn
import com.ngapp.portray.utils.withArguments
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserCollectionListPageFragment() :
    ViewBindingFragment<FragmentProfileCollectionListPageBinding>(FragmentProfileCollectionListPageBinding::inflate) {

    private val viewModel by viewModels<ProfileViewModel>()
    private lateinit var userCollectionListAdapter: UserCollectionListAdapter

    override fun onResume() {
        super.onResume()
        val username = requireArguments().getString(KEY_USERNAME)
        viewModel.getUserCollections(username!!)

        binding.retryButton.setOnClickListener {
            userCollectionListAdapter.retry()
        }

        setUpRecyclerView()

        viewModel.userCollections.launchAndCollectIn(viewLifecycleOwner) { userCollectionList ->
            userCollectionListAdapter.submitData(userCollectionList)
        }
    }


    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
            userCollectionListAdapter = UserCollectionListAdapter(
                onItemSingleClick = { userCollection ->
                    showCollectionDetail(userCollection)
                }
            )
            adapter = userCollectionListAdapter.withLoadStateFooter(
                footer = NewLoadStateAdapter { userCollectionListAdapter.retry() }
            )

            userCollectionListAdapter.addLoadStateListener { loadState ->
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
            ProfileFragmentDirections.actionNavProfileToNavCollectionDetail(
                collection.id,
                collection.title
            )
        findNavController().navigate(action)
    }

    companion object {

        private const val KEY_USERNAME = "username"

        fun newInstance(
            username: String
        ): UserCollectionListPageFragment {
            return UserCollectionListPageFragment().withArguments {
                putString(KEY_USERNAME, username)
            }
        }
    }
}