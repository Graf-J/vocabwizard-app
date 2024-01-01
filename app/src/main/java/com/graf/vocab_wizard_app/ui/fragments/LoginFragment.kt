package com.graf.vocab_wizard_app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.graf.vocab_wizard_app.R
import com.graf.vocab_wizard_app.data.dto.request.LoginRequestDto
import com.graf.vocab_wizard_app.databinding.FragmentLoginBinding
import com.graf.vocab_wizard_app.viewmodel.login.LoginResult
import com.graf.vocab_wizard_app.viewmodel.login.LoginViewModel

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null;
    private val binding get() = _binding!!;
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        addListeners()

        return binding.root
    }

    private fun addListeners() {
        addRegisterNavigationClickListener()
        addNameChangedListener()
        addPasswordChangedListener()
        addSubmitClickListener()
    }

    private fun addRegisterNavigationClickListener() {
        binding.registerNowTextView.setOnClickListener {
            view?.let {
                Navigation.findNavController(it)
                    .navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }

    private fun addNameChangedListener() {
        binding.loginNameTextInput.addTextChangedListener {
            validateName()
        }
    }

    private fun addPasswordChangedListener() {
        binding.loginPasswordTextInput.addTextChangedListener {
            validatePassword()
        }
    }

    private fun addSubmitClickListener() {
        binding.submitLoginButton.setOnClickListener {
            val isNameValid = validateName()
            val isPasswordValid = validatePassword()
            // Log in if neither Name nor Password is empty
            if (isNameValid && isPasswordValid) {
                val name = binding.loginNameTextInput.text.toString()
                val password = binding.loginPasswordTextInput.text.toString()
                login(name, password)
            }
        }
    }

    private fun validateName(): Boolean {
        val input = binding.loginNameTextInput
        val layout = binding.loginNameLayout

        return when {
            input.text.toString().trim().isEmpty() -> {
                layout.error = getString(R.string.required)
                false
            }
            else -> {
                layout.error = null
                true
            }
        }
    }

    private fun validatePassword(): Boolean {
        val input = binding.loginPasswordTextInput
        val layout = binding.loginPasswordLayout

        return when {
            input.text.toString().trim().isEmpty() -> {
                layout.error = getString(R.string.required)
                false
            }
            else -> {
                layout.error = null
                true
            }
        }
    }

    private fun login(name: String, password: String) {
        loginViewModel.loginLiveData.observe(viewLifecycleOwner) {
            when(it) {
                is LoginResult.LOADING -> {
                    // Disable Button
                    binding.submitLoginButton.isEnabled = false
                    // Reset Error Message
                    binding.loginErrorText.visibility = View.INVISIBLE
                    binding.loginErrorText.text = ""
                }
                is LoginResult.ERROR -> {
                    // Show Error Message
                    binding.loginErrorText.visibility = View.VISIBLE
                    binding.loginErrorText.text = translateErrorMessage(it.message)

                    // Enable Button
                    binding.submitLoginButton.isEnabled = true

                    // Clear back stack to reset navigation history
                    view?.let { innerIt ->
                        val navController = Navigation.findNavController(innerIt)
                        navController.popBackStack(R.id.loginFragment, false)
                    }
                }
                is LoginResult.SUCCESS -> {
                    // Enable Button
                    binding.submitLoginButton.isEnabled = true
                    // Navigate to Deck Overview
                    view?.let {innerIt ->
                        val navController = Navigation.findNavController(innerIt)
                        val currentDestination = navController.currentDestination?.id

                        if (currentDestination == R.id.loginFragment) {
                            navController.navigate(R.id.action_loginFragment_to_deckOverviewFragment)
                        }
                    }
                }
                else -> {}
            }
        }

        loginViewModel.login(LoginRequestDto(name, password))
    }

    private fun translateErrorMessage(message: String): String {
        return if (message == "Username or Password is not valid") {
            getString(R.string.login_failed)
        } else if (message == "API not reachable") {
            getString(R.string.api_not_reachable)
        } else {
            message
        }
    }
}