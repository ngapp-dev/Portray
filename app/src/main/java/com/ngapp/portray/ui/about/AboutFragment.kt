package com.ngapp.portray.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ngapp.portray.databinding.FragmentAboutBinding
import com.ngapp.portray.utils.ViewBindingFragment

class AboutFragment : ViewBindingFragment<FragmentAboutBinding>(FragmentAboutBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textView.text =
            "The application was made with great difficulty as part of the training task of the Skillbox school"
    }
}