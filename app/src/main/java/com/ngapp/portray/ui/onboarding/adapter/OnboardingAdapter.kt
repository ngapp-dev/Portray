package com.ngapp.portray.ui.onboarding.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ngapp.portray.data.db.models.onboading_screen.OnboardingScreen
import com.ngapp.portray.ui.onboarding.OnboardingScreenFragment

class OnboardingAdapter(
    private val fragment: Fragment,
    private var onboardingScreens: List<OnboardingScreen>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = onboardingScreens.size

    override fun createFragment(position: Int): Fragment {
        val onboardingScreen: OnboardingScreen = onboardingScreens[position]

        return OnboardingScreenFragment.newInstance(
            id = onboardingScreen.id
        )
    }
}