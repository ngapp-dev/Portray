package com.ngapp.portray.ui.profile.view_pager.user_photos

import android.provider.Settings.Secure.putString
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.card.MaterialCardView
import com.ngapp.portray.R
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.databinding.FragmentProfilePhotoListPageBinding
import com.ngapp.portray.ui.profile.ProfileFragmentDirections
import com.ngapp.portray.ui.profile.ProfileViewModel
import com.ngapp.portray.ui.profile.view_pager.user_photos.adapter.UserPhotoListAdapter
import com.ngapp.portray.utils.ViewBindingFragment
import com.ngapp.portray.utils.adapter.NewLoadStateAdapter
import com.ngapp.portray.utils.launchAndCollectIn
import com.ngapp.portray.utils.withArguments
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserPhotoListPageFragment(
) :
    ViewBindingFragment<FragmentProfilePhotoListPageBinding>(FragmentProfilePhotoListPageBinding::inflate) {

    private val viewModel by viewModels<ProfileViewModel>()
    private lateinit var userPhotoListAdapter: UserPhotoListAdapter

    override fun onResume() {
        super.onResume()
        val username = requireArguments().getString(KEY_USERNAME)
        viewModel.getUserPhotos(username!!)

        binding.retryButton.setOnClickListener {
            userPhotoListAdapter.retry()
        }

        setUpRecyclerView()

        viewModel.userPhotos.launchAndCollectIn(viewLifecycleOwner) { userPhotoList ->
            if (userPhotoListAdapter.itemCount > 0) {
                binding.emptyListTextView.text = getString(R.string.list_not_empty)
                userPhotoListAdapter.submitData(userPhotoList)
            } else {
                binding.emptyListTextView.text = getString(R.string.list_empty)
            }
        }
    }


    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
            userPhotoListAdapter = UserPhotoListAdapter(
                onItemSingleClick = { photo, materialCardView ->
                    showPhotoDetail(photo, materialCardView)
                },
                onItemDoubleClick = { likeImage(it) },
                onLikeImageViewClick = { likeImage(it) }
            )
            adapter = userPhotoListAdapter.withLoadStateFooter(
                footer = NewLoadStateAdapter { userPhotoListAdapter.retry() }
            )
            userPhotoListAdapter.addLoadStateListener { loadState ->
                println("AppDebug: $loadState")
                binding.recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error
            }

            val manager = LinearLayoutManager(requireContext())
            layoutManager = manager
        }
    }

    private fun showPhotoDetail(photo: Photo, materialCardView: MaterialCardView) {
//        ActivityOptions.makeSceneTransitionAnimation(
//            requireActivity(),
//            materialCardView,
//            materialCardView.transitionName
//        ).toBundle()
//        val extras = FragmentNavigatorExtras(materialCardView to photo.id)
        val action =
            ProfileFragmentDirections.actionNavProfileToNavPhotoDetail(photo.id, photo.urls.regular)
        findNavController().navigate(action)
    }

    private fun likeImage(photo: Photo) {
        val username = requireArguments().getString(KEY_USERNAME)
        viewModel.changeLike(
            photoId = photo.id,
            likedByUser = photo.likedByUser,
            username = username!!,
            likes = photo.likes
        )
    }

    companion object {

        private const val KEY_USERNAME = "username"

        fun newInstance(
            username: String
        ): UserPhotoListPageFragment {
            return UserPhotoListPageFragment().withArguments {
                putString(KEY_USERNAME, username)
            }
        }
    }

}