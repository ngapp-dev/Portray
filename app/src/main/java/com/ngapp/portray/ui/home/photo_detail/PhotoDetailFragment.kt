package com.ngapp.portray.ui.home.photo_detail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.*
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ShareCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.work.*
import com.google.android.material.snackbar.Snackbar
import com.ngapp.portray.R
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.databinding.FragmentPhotoDetailBinding
import com.ngapp.portray.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class PhotoDetailFragment :
    ViewBindingFragment<FragmentPhotoDetailBinding>(FragmentPhotoDetailBinding::inflate) {

    private val args: PhotoDetailFragmentArgs by navArgs()
    private val viewModel by viewModels<PhotoDetailViewModel>()
    private var startFragment = FIRST_START
    private lateinit var photoId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        WorkManager.getInstance(requireContext())
            .getWorkInfosForUniqueWorkLiveData(DOWNLOAD_WORK_ID)
            .observe(viewLifecycleOwner) {
                if (it.isNotEmpty())
                    handleWorkInfo(it.first())
            }

        if (requireArguments().containsKey(PHOTO_ID_KEY)) {
            photoId = arguments?.getString(PHOTO_ID_KEY)!!
            val photoUrl = arguments?.getString(PHOTO_URL_KEY)

            postponeEnterTransition()
            binding.card.transitionName = photoId
            createActionbarMenu(url = photoUrl)

            viewModel.getPhotoByIdNow(photoId)
        } else {
            photoId = args.id

            postponeEnterTransition()
            binding.card.transitionName = photoId
            createActionbarMenu(url = args.url)

            viewModel.getPhotoByIdNow(photoId)
        }

        binding.swiperefresh.setOnRefreshListener {
            viewModel.getPhotoByIdNow(photoId)
        }

        bindViewModel()
    }


    private fun bindViewModel() {
        viewModel.photo.observe(viewLifecycleOwner, Observer { result ->
//        viewModel.photoFlow.launchAndCollectIn(viewLifecycleOwner) { photo ->

            when (result.status) {
                FetchResult.Status.SUCCESS -> {
                    result.data?.let {
                        updateUi(it)
                        binding.swiperefresh.isRefreshing = false
                    }
//                    binding.progressBar.visibility = View.GONE
                }

                FetchResult.Status.ERROR -> {
                    result.message?.let {
                        showError(it)
                        binding.swiperefresh.isRefreshing = false
                    }
//                    binding.progressBar.visibility = View.GONE
                }

                FetchResult.Status.LOADING -> {
//                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        })
    }


    private fun showError(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.dismiss)) {
            }.show()
    }

    private fun likeImage(photo: Photo) {
        viewModel.changeLike(photo.id, photo.likedByUser, photo.likes)
        viewModel.likeFlow.launchAndCollectIn(viewLifecycleOwner) { result ->
            when (result.status) {
                FetchResult.Status.SUCCESS -> {
                    result.data?.let {
                        if (!it.likedByUser) {
                            Timber.e("False")
                            binding.likeImageView.setImageResource(R.drawable.ic_like_filled)
                            binding.likesTextView.text = (photo.likes.toInt() + 1).toString()
                        } else {
                            Timber.e("True")
                            binding.likeImageView.setImageResource(R.drawable.ic_like_outline)
                            binding.likesTextView.text = (photo.likes.toInt() - 1).toString()
                        }
                    }
                }

                FetchResult.Status.ERROR -> {
                    result.message?.let {
                        showError(it)
                    }
                }
                FetchResult.Status.LOADING -> TODO()
            }
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

    private fun downloadPhoto(photo: Photo, fileName: String) {
        binding.downloadTextView.setOnClickListener {
            val ixId = photo.links.downloadLocation?.substringAfter("ixid=")
            viewModel.triggerDownload(photo.id, ixId!!)
            viewModel.saveImage(photo.id, photo.urls.raw!!, fileName)
            startFragment = ANOTHER_START
        }
//        viewModel.loadingFlow.launchAndCollectIn(viewLifecycleOwner) { setLoading(it) }
//        viewModel.toastLiveData.launchAndCollectIn(viewLifecycleOwner) { toast(it) }
    }

    private fun createActionbarMenu(url: String?) {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_share_action_bar, menu)
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_share -> {

                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TITLE, getString(R.string.portray_share))
                            putExtra(
                                Intent.EXTRA_TEXT,
                                getString(R.string.portray_share) + "\n$url"
                            )
                            type = "text/plain"
                        }

                        val shareIntent = Intent.createChooser(sendIntent, null)
                        startActivity(shareIntent)

                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.CREATED)
    }

    private fun updateUi(photo: Photo) {

        binding.photoImageView.load(photo.urls.regular ?: "", null, true) {
            startPostponedEnterTransition()
        }

        enterTransition = Fade(Visibility.MODE_IN).apply {
            excludeTarget(android.R.id.statusBarBackground, true)
            excludeTarget(android.R.id.navigationBarBackground, true)
            excludeTarget(R.id.toolbar, true)
        }

        binding.avatarImageView.load(photo.user.profileImage?.small ?: "")
        binding.authorTextView.text = photo.user.name
        binding.accountTextView.text = "@${photo.user.username}"
        binding.likesTextView.text = photo.likes


        if (photo.likedByUser) {
            binding.likeImageView.setImageResource(R.drawable.ic_like_filled)
        } else {
            binding.likeImageView.setImageResource(R.drawable.ic_like_outline)
        }

        binding.likeImageView.setOnClickListener {
            likeImage(photo)
        }

        binding.photoImageView.setOnClickListener(object : DoubleClickListener() {
            override fun onDoubleClick() {
                likeImage(photo)
            }

            override fun onSingleClick() {
            }
        })

        if (photo.location?.country != null) {
            binding.locationTextView.text =
                "${photo.location?.country} ${photo.location?.city}"
            binding.locationIconImageView.isGone = false
            binding.locationTextView.isGone = false
        }

        if (photo.tags.toString() == "[]") {
            binding.hashtagsTextView.isGone = true
        } else {
            photo.tags?.forEach {
                binding.hashtagsTextView.text = "#${it.title}"
            }
            binding.hashtagsTextView.isGone = false
        }

        setText(
            requireContext(),
            binding.madeWithTextView,
            R.string.made_with,
            photo.exif?.make
        )
        setText(
            requireContext(),
            binding.modelTextView,
            R.string.camera_model,
            photo.exif?.model
        )
        setText(
            requireContext(),
            binding.exposureTextView,
            R.string.exposure,
            photo.exif?.exposureTime
        )
        setText(
            requireContext(),
            binding.apertureTextView,
            R.string.aperture,
            photo.exif?.aperture
        )
        setText(
            requireContext(),
            binding.focalLengthTextView,
            R.string.focal_length,
            photo.exif?.focalLength
        )
        setText(
            requireContext(),
            binding.IsoTextView,
            R.string.iso,
            photo.exif?.iso
        )
        setText(
            requireContext(),
            binding.aboutUsernameTextView,
            R.string.about_user,
            photo.user.username
        )
        setText(
            requireContext(),
            binding.aboutUserTextView,
            R.string.user_bio,
            photo.user.bio
        )
        if (photo.downloads != null) {
            setText(
                requireContext(),
                binding.downloadsNumberTextView,
                R.string.number_downloads,
                photo.downloads.toString()
            )
        }


        downloadPhoto(photo = photo, fileName = "Portray-${photo.id}.jpg")
    }

    companion object {
        private const val DOWNLOAD_WORK_ID = "download work"
        private const val FIRST_START = 0
        private const val ANOTHER_START = 1

        private const val PHOTO_ID_KEY = "PHOTO_ID_KEY"
        private const val PHOTO_URL_KEY = "PHOTO_URL_KEY"
    }

}
