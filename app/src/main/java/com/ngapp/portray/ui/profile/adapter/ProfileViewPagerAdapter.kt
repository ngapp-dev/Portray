package com.ngapp.portray.ui.profile.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ngapp.portray.ui.MainActivity
import com.ngapp.portray.ui.auth.ui.AuthFragment
import com.ngapp.portray.ui.profile.view_pager.user_collections.UserCollectionListPageFragment
import com.ngapp.portray.ui.profile.view_pager.user_liked_photos.UserLikedPhotoListPageFragment
import com.ngapp.portray.ui.profile.view_pager.user_photos.UserPhotoListPageFragment

class ProfileViewPagerAdapter(
    private val username: String,
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UserPhotoListPageFragment.newInstance(username)
            1 -> UserLikedPhotoListPageFragment.newInstance(username)
            2 -> UserCollectionListPageFragment.newInstance(username)
            else -> AuthFragment()
        }
    }
}