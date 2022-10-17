package com.ngapp.portray.ui.onboarding

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.view.size
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.ngapp.portray.R
import com.ngapp.portray.databinding.FragmentOnboardingBinding
import com.ngapp.portray.ui.onboarding.adapter.OnboardingAdapter
import com.ngapp.portray.utils.ViewBindingFragment
import com.ngapp.portray.utils.haveM
import com.ngapp.portray.utils.launchAndCollectIn
import com.ngapp.portray.utils.toast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OnboardingFragment :
    ViewBindingFragment<FragmentOnboardingBinding>(FragmentOnboardingBinding::inflate) {

    private val viewModel by viewModels<OnboardingViewModel>()
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        val firstStart = sharedPreferences.getBoolean("firstStartBoarding", true)
        if (!firstStart) {
            getStarted()
        } else {
        viewModel.createOnboardingScreens()
        viewModel.loadOnboardingScreens()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val value = TypedValue()
                requireContext().theme.resolveAttribute(android.R.attr.colorPrimary, value, true)

                if (position < binding.viewPager.size + 1) {
                    binding.nextButton.text = getString(R.string.onboarding_next)
                    binding.nextButton.background.setTint(Color.TRANSPARENT)
                    binding.nextButton.setTextColor(value.data)
                    binding.nextButton.setOnClickListener {
                        binding.viewPager.setCurrentItem(position + 1, true)
                    }
                } else {
                    binding.nextButton.setBackgroundColor(value.data)
                    binding.nextButton.text = getString(R.string.onboarding_get_started)
//                    binding.nextButton.background.setTint(requireContext().getColor(R.color.blue))
                    if (haveM()) {
                        binding.nextButton.setTextColor(requireContext().getColor(R.color.white))
                    }
                    binding.nextButton.setOnClickListener {
                        getStarted()
                    }
                }
            }
        })
    }

    private fun bindViewModel() {
        viewModel.onboardingScreensStateFlow.launchAndCollectIn(viewLifecycleOwner) { onboardingScreens ->
            onboardingScreens?.let {
                val adapter = OnboardingAdapter(this@OnboardingFragment, onboardingScreens)
                binding.viewPager.adapter = adapter
                binding.viewPager.offscreenPageLimit = 1
                binding.dotsIndicator.attachTo(binding.viewPager)
                binding.skipButton.setOnClickListener {
                    getStarted()
                }
            }
        }

        viewModel.toastFlow.launchAndCollectIn(viewLifecycleOwner) {
            toast(it)
        }
    }

    private fun getStarted() {
        sharedPreferences.edit().putBoolean("firstStartBoarding", false).apply()
        findNavController().navigate(OnboardingFragmentDirections.actionNavOnboardingToNavAuth())
    }

    companion object {
        private const val PREFERENCES_NAME = "my_preferences"

    }
}