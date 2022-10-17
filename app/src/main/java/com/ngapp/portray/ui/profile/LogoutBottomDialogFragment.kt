package com.ngapp.portray.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ngapp.portray.R
import com.ngapp.portray.databinding.BottomDialogFragmentLogoutBinding
import com.ngapp.portray.utils.launchAndCollectIn
import com.ngapp.portray.utils.resetNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogoutBottomDialogFragment : BottomSheetDialogFragment() {

    private var _dialogBinding: BottomDialogFragmentLogoutBinding? = null
    private val dialogBinding get() = _dialogBinding!!
    private val viewModel: ProfileViewModel by viewModels()

    private val logoutResponse =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.webLogoutComplete()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _dialogBinding = BottomDialogFragmentLogoutBinding.inflate(LayoutInflater.from(context))
        val view = dialogBinding.root

        dialogBinding.yesButton.setOnClickListener {
            viewModel.logout()
        }
        dialogBinding.noButton.setOnClickListener {
            dismiss()
        }

        viewModel.logoutPageFlow.launchAndCollectIn(viewLifecycleOwner) {
            logoutResponse.launch(it)
        }

        viewModel.logoutCompletedFlow.launchAndCollectIn(viewLifecycleOwner) {
            findNavController().resetNavGraph(R.navigation.nav_graph)
        }
        return view
    }
}