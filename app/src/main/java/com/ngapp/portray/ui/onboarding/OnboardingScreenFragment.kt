package com.ngapp.portray.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ngapp.portray.databinding.FragmentOnboardingPageBinding
import com.ngapp.portray.utils.ViewBindingFragment
import com.ngapp.portray.utils.launchAndCollectIn
import com.ngapp.portray.utils.withArguments
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingScreenFragment :
    ViewBindingFragment<FragmentOnboardingPageBinding>(FragmentOnboardingPageBinding::inflate) {

    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = requireArguments().getLong(KEY_ID)
        viewModel.loadOnboardingScreenById(id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun bindViewModel() {
        viewModel.currentOnboardingScreenStateFlow.launchAndCollectIn(viewLifecycleOwner) { onboardingScreen ->
            onboardingScreen?.let {
                binding.imageView.setImageResource(onboardingScreen?.image!!)
                binding.titleTextView.text = onboardingScreen.title
                binding.descriptionTextView.text = onboardingScreen.descriptionEn
            }
        }
    }

    companion object {
        private const val KEY_ID = "ID"

        fun newInstance(
            id: Long
        ): OnboardingScreenFragment {
            return OnboardingScreenFragment().withArguments {
                putLong(KEY_ID, id)
            }
        }
    }
}