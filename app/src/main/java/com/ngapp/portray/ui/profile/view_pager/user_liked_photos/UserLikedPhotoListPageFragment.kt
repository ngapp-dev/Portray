package com.ngapp.portray.ui.profile.view_pager.user_liked_photos

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.ngapp.portray.R
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.databinding.FragmentProfilePhotoListPageBinding
import com.ngapp.portray.ui.home.photo_detail.PhotoDetailFragment
import com.ngapp.portray.ui.profile.ProfileFragmentDirections
import com.ngapp.portray.ui.profile.ProfileViewModel
import com.ngapp.portray.ui.profile.view_pager.user_liked_photos.adapter.UserLikedPhotoListAdapter
import com.ngapp.portray.ui.profile.view_pager.user_photos.UserPhotoListPageFragment
import com.ngapp.portray.utils.ViewBindingFragment
import com.ngapp.portray.utils.adapter.NewLoadStateAdapter
import com.ngapp.portray.utils.launchAndCollectIn
import com.ngapp.portray.utils.toast
import com.ngapp.portray.utils.withArguments
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class UserLikedPhotoListPageFragment() :
    ViewBindingFragment<FragmentProfilePhotoListPageBinding>(FragmentProfilePhotoListPageBinding::inflate) {

    private val viewModel by viewModels<ProfileViewModel>()
    private lateinit var userLikedPhotoListAdapter: UserLikedPhotoListAdapter
    private var startFragment = FIRST_START

    override fun onResume() {
        super.onResume()

        WorkManager.getInstance(requireContext())
            .getWorkInfosForUniqueWorkLiveData(DOWNLOAD_WORK_ID)
            .observe(viewLifecycleOwner) {
                if (it.isNotEmpty())
                    handleWorkInfo(it.first())
            }

        val username = requireArguments().getString(KEY_USERNAME)
        viewModel.getUserLikedPhotos(username!!)

        binding.retryButton.setOnClickListener {
            userLikedPhotoListAdapter.retry()
        }

        setUpRecyclerView()

        viewModel.userLikedPhotos.launchAndCollectIn(viewLifecycleOwner) { userLikedPhotoList ->
            userLikedPhotoListAdapter.submitData(userLikedPhotoList)
        }
    }


    private fun handleWorkInfo(workInfo: WorkInfo) {
        Timber.d("handleWorkInfo new state = ${workInfo.state}")
        val isFinished = workInfo.state.isFinished

//        binding.progressBar.isVisible = isFinished.not()
//        binding.contentGroup.isVisible = isFinished

        when (workInfo.state) {
            WorkInfo.State.FAILED -> {
                if (startFragment != FIRST_START) {
                    toast("Download error")
                    startFragment = ANOTHER_START
                }
            }
            WorkInfo.State.SUCCEEDED -> {
                if (startFragment != FIRST_START) {
                    toast("Download completed successfully")
                    Snackbar.make(binding.root, "Download success", Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.open)) {

                        }.show()
                    startFragment = ANOTHER_START
                }
            }
            else -> {

            }
        }
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
            userLikedPhotoListAdapter = UserLikedPhotoListAdapter(
                onItemSingleClick = { photo, materialCardView ->
                    showPhotoDetail(photo, materialCardView)
                },
                onItemDoubleClick = { likeImage(it) },
                onLikeImageViewClick = { likeImage(it) },
                onDownloadClick = { }
            )
            adapter = userLikedPhotoListAdapter.withLoadStateFooter(
                footer = NewLoadStateAdapter { userLikedPhotoListAdapter.retry() }
            )

            userLikedPhotoListAdapter.addLoadStateListener { loadState ->
                println("AppDebug: $loadState")
                binding.recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error
            }

            val manager = LinearLayoutManager(requireContext())
            layoutManager = manager
//            val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//            manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
//            layoutManager = manager
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
        private const val FIRST_START = 0
        private const val ANOTHER_START = 1
        private const val DOWNLOAD_WORK_ID = "download work"

        fun newInstance(
            username: String
        ): UserLikedPhotoListPageFragment {
            return UserLikedPhotoListPageFragment().withArguments {
                putString(KEY_USERNAME, username)
            }
        }
    }

}