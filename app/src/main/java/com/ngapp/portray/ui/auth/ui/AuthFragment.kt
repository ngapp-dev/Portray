package com.ngapp.portray.ui.auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ngapp.portray.databinding.FragmentAuthBinding
import com.ngapp.portray.utils.*
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse

@AndroidEntryPoint
class AuthFragment : ViewBindingFragment<FragmentAuthBinding>(FragmentAuthBinding::inflate) {

    private val viewModel: AuthViewModel by viewModels()

    private val getAuthResponse =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val dataIntent = it.data ?: return@registerForActivityResult
            handleAuthResponseIntent(dataIntent)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.checkAccessToken()
        viewModel.accessTokenFlow.launchAndCollectIn(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                saveAuthUser()
            } else {
                bindViewModel()
            }
        }
    }

    private fun bindViewModel() {

        binding.signInWithUnsplashButton.setOnClickListener { viewModel.openLoginPage() }

        viewModel.openAuthPageFlow.launchAndCollectIn(viewLifecycleOwner) {
            openAuthPage(it)
        }
        viewModel.toastFlow.launchAndCollectIn(viewLifecycleOwner) {
            toast(it)
        }
        viewModel.authSuccessFlow.launchAndCollectIn(viewLifecycleOwner) {
            saveAuthUser()
        }
    }

    private fun updateIsLoading(isLoading: Boolean) = with(binding) {
        binding.signInWithUnsplashButton.isVisible = !isLoading
        binding.loginProgress.isVisible = isLoading
    }

    private fun openAuthPage(intent: Intent) {
        getAuthResponse.launch(intent)
    }

    private fun handleAuthResponseIntent(intent: Intent) {
        // пытаемся получить ошибку из ответа. null - если все ок
        val exception = AuthorizationException.fromIntent(intent)
        // пытаемся получить запрос для обмена кода на токен, null - если произошла ошибка
        val tokenExchangeRequest = AuthorizationResponse.fromIntent(intent)
            ?.createTokenExchangeRequest()
        when {
            // авторизация завершались ошибкой
            exception != null -> viewModel.onAuthCodeFailed(exception)
            // авторизация прошла успешно, меняем код на токен
            tokenExchangeRequest != null ->
                viewModel.onAuthCodeReceived(tokenExchangeRequest)
        }
    }

    private fun saveAuthUser() {
        viewModel.saveAuthUser()
        viewModel.loadingFlow.launchAndCollectIn(viewLifecycleOwner) { status ->
            updateIsLoading(status)
        }
        viewModel.saveAuthUserSuccessFlow.launchAndCollectIn(viewLifecycleOwner) {
            findNavController().navigate(AuthFragmentDirections.actionNavAuthToNavHome())
        }
    }

}