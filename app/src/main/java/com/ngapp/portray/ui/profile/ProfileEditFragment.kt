package com.ngapp.portray.ui.profile

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.ngapp.portray.R
import com.ngapp.portray.data.db.models.user.User
import com.ngapp.portray.databinding.FragmentProfileEditBinding
import com.ngapp.portray.utils.FetchResult
import com.ngapp.portray.utils.ViewBindingFragment
import com.ngapp.portray.utils.launchAndCollectIn
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileEditFragment :
    ViewBindingFragment<FragmentProfileEditBinding>(FragmentProfileEditBinding::inflate) {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createActionbarMenu()
        bindViewModel()
    }

    private fun bindViewModel() {
        viewModel.user.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                FetchResult.Status.SUCCESS -> {
                    result.data?.let {
                        updateUi(it)
                    }
                }

                FetchResult.Status.ERROR -> {
                    result.message?.let {
                        showError(it)
                    }
                }

                FetchResult.Status.LOADING -> {
                }
            }
        }
    }

    private fun updateUi(user: User) {
        binding.firstNameEditText.setText(user.firstName)
        binding.lastNameEditText.setText(user.lastName)
        binding.bioEditText.setText(user.bio)
        binding.locationEditText.setText(user.location)
        binding.portfolioUrlEditText.setText(user.portfolioUrl)
    }

    private fun showError(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.dismiss)) {
            }.show()
    }

    private fun createActionbarMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_submit_action_bar, menu)
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_submit -> {
                        val firstName = binding.firstNameEditText.text.toString()
                        val lastName = binding.lastNameEditText.text.toString()
                        val portfolioUrl = binding.portfolioUrlEditText.text.toString()
                        val location = binding.locationEditText.text.toString()
                        val bio = binding.bioEditText.text.toString()

                        viewModel.updateUser(
                            firstName,
                            lastName,
                            portfolioUrl,
                            location,
                            bio
                        )

                        viewModel.updateUserSuccessFlow.launchAndCollectIn(viewLifecycleOwner) {
                            findNavController().navigate(ProfileEditFragmentDirections.actionNavProfileEditToNavProfile())
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.CREATED)
    }
}