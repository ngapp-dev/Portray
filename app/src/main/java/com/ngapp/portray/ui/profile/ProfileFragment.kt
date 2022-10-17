package com.ngapp.portray.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.ngapp.portray.R
import com.ngapp.portray.data.db.models.user.User
import com.ngapp.portray.databinding.FragmentProfileBinding
import com.ngapp.portray.ui.SettingsActivity
import com.ngapp.portray.ui.profile.adapter.ProfileViewPagerAdapter
import com.ngapp.portray.utils.FetchResult
import com.ngapp.portray.utils.ViewBindingFragment
import com.ngapp.portray.utils.load
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment :
    ViewBindingFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel by viewModels<ProfileViewModel>()

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val inflater = TransitionInflater.from(requireContext())
//        enterTransition = inflater.inflateTransition(R.transition.fade)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swiperefresh.setOnRefreshListener {
            viewModel.refresh()
            binding.swiperefresh.isRefreshing = false
        }

        bindViewModel()
        createActionbarMenu()
    }

    private fun bindViewModel() {
        viewModel.user.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                FetchResult.Status.SUCCESS -> {
                    result.data?.let {
                        updateUi(it)
                        binding.swiperefresh.isRefreshing = false
                    }
                    binding.progressBar.visibility = View.GONE
                }

                FetchResult.Status.ERROR -> {
                    result.message?.let {
                        val message = it
                        showError(it)
                        binding.swiperefresh.isRefreshing = false
                    }
                    binding.progressBar.visibility = View.GONE
                }

                FetchResult.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }

//        viewModel.loadingFlow.launchAndCollectIn(viewLifecycleOwner) { setLoading(it) }
//        viewModel.toastFlow.launchAndCollectIn(viewLifecycleOwner) { toast(it) }
//        viewModel.userProfileFlow.launchAndCollectIn(viewLifecycleOwner) { user ->
//            setupAdapters(user)
//
//            binding.nameTextView.text = "${user.firstName} ${user.lastName}"
//            setText(requireContext(), binding.userNameTextView, R.string.username, user.username)
//            binding.bioTextView.text = user.bio
//            binding.locationTextView.text = user.location
//            binding.emailTextView.text = user.portfolioUrl
//            binding.downloadsTextView.text = user.downloads.toString()
//
//        }
//        viewModel.userPublicProfileFlow.launchAndCollectIn(viewLifecycleOwner) { userPublicProfile ->
//            binding.avatarImageView.load(
//                userPublicProfile.profileImage?.medium ?: "",
//                R.drawable.ic_user_placeholder
//            )
//        }
    }

    private fun showError(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.dismiss)) {
            }.show()
    }

    private fun updateUi(user: User) {
        setupAdapters(user)
        binding.avatarImageView.load(
            user.profileImage?.medium ?: "",
            R.drawable.ic_user_placeholder
        )
        binding.nameTextView.text = "${user.firstName} ${user.lastName}"
        binding.userNameTextView.text = "@${user.username}"
        binding.bioTextView.text = user.bio
        binding.locationTextView.text = user.location
        binding.emailTextView.text = user.portfolioUrl
        binding.downloadsTextView.text = user.downloads.toString()

        binding.locationTextView.setOnClickListener {
            createLocationIntent(user)
        }
        binding.emailTextView.setOnClickListener {
            createBrowsingIntent(user)
        }
        binding.editProfileButton.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionNavProfileToNavProfileEdit()
            )
        }
    }

    private fun setupAdapters(user: User) {
        val adapter = ProfileViewPagerAdapter(user.username, requireActivity())
        binding.feedViewPager.adapter = adapter

        TabLayoutMediator(binding.feedTabLayout, binding.feedViewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "${user.totalPhotos}\n" +
                        when (user.totalPhotos) {
                            1 -> {
                                getString(R.string.nav_photos_one)
                            }
                            2, 3, 4 -> {
                                getString(R.string.nav_photos_two_four)
                            }
                            else -> {
                                getString(R.string.nav_photos)
                            }
                        }

                1 -> tab.text = "${user.totalLikes}\n" + getString(R.string.nav_likes)
                2 -> tab.text =
                    "${user.totalCollections}\n" +
                            when (user.totalCollections) {
                                1 -> {
                                    getString(R.string.nav_user_collections_one)
                                }
                                2, 3, 4 -> {
                                    getString(R.string.nav_user_collections_two_four)
                                }
                                else -> {
                                    getString(R.string.nav_user_collections)
                                }
                            }
            }
        }.attach()
    }

    private fun createActionbarMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_profile_action_bar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_settings -> {
                        val intent = Intent(requireContext(), SettingsActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.action_logout -> {
                        findNavController().navigate(ProfileFragmentDirections.actionNavProfileToLogoutBottomDialogFragment())
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun createLocationIntent(user: User) {
        val url = "yandexmaps://maps.yandex.ru/?pt=0,0&z=12&text=${user.location}&l=map"
        val intentYandex = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intentYandex.setPackage("ru.yandex.yandexmaps")

        val uriGoogle = "geo:0,0?q=${user.location}"
        val intentGoogle = Intent(Intent.ACTION_VIEW, Uri.parse(uriGoogle))
        intentGoogle.setPackage("com.google.android.apps.maps")

        val title = getString(R.string.select_application)
        val chooserIntent = Intent.createChooser(intentGoogle, title)
        val arr = arrayOfNulls<Intent>(1)
        arr[0] = intentYandex
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arr)
        startActivity(chooserIntent)
    }

    private fun createBrowsingIntent(user: User) {
        val url = user.portfolioUrl
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(Intent.createChooser(intent, "Browse with"))
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.contentGroup.isVisible = isLoading.not()
    }
}