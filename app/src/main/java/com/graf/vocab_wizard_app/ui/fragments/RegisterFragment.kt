package com.graf.vocab_wizard_app.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.graf.vocab_wizard_app.R
import com.graf.vocab_wizard_app.data.dto.request.LoginRequestDto
import com.graf.vocab_wizard_app.data.dto.request.RegisterRequestDto
import com.graf.vocab_wizard_app.databinding.FragmentRegisterBinding
import com.graf.vocab_wizard_app.viewmodel.login.LoginResult
import com.graf.vocab_wizard_app.viewmodel.register.RegisterResult
import com.graf.vocab_wizard_app.viewmodel.register.RegisterViewModel

class RegisterFragment : Fragment(R.layout.fragment_register) {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)

        addListeners()

        return binding.root
    }

    private fun addListeners() {
        addLoginNavigationClickListener()
        addNameChangedListener()
        addPasswordChangedListener()
        addConfirmPasswordChangedListener()
        addSubmitClickListener()
    }

    private fun addLoginNavigationClickListener() {
        binding.loginNowTextView.setOnClickListener {
            view?.let {
                Navigation.findNavController(it).navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }

    private fun addNameChangedListener() {
        binding.registerNameTextInput.addTextChangedListener {
            validateName()
        }
    }

    private fun addPasswordChangedListener() {
        binding.registerPasswordTextInput.addTextChangedListener {
            validatePassword()
        }
    }

    private fun addConfirmPasswordChangedListener() {
        binding.registerPasswordConfirmTextInput.addTextChangedListener {
            validateConfirmPassword()
        }
    }

    private fun validateName(): Boolean {
        val input = binding.registerNameTextInput
        val layout = binding.registerNameLayout

        return when {
            (input.text.toString().trim().length < 4) -> {
                layout.error = getString(R.string.min_length_4)
                false
            }
            else -> {
                layout.error = null
                true
            }
        }
    }

    private fun validatePassword(): Boolean {
        val input = binding.registerPasswordTextInput
        val layout = binding.registerPasswordLayout

        return when {
            (input.text.toString().trim().length < 6) -> {
                layout.error = getString(R.string.min_length_6)
                false
            }
            (
                !"\\d".toRegex().containsMatchIn(input.text.toString()) ||
                !"[!@#$%^&*(),.?\":{}|<>]".toRegex().containsMatchIn(input.text.toString())
            ) -> {
                layout.error = getString(R.string.password_strength)
                false
            }
            else -> {
                layout.error = null
                true
            }
        }
    }

    private fun validateConfirmPassword(): Boolean {
        val input = binding.registerPasswordConfirmTextInput
        val layout = binding.registerPasswordConfirmLayout

        return when {
            (input.text.toString() != binding.registerPasswordTextInput.text.toString()) -> {
                layout.error = getString(R.string.no_password_match)
                false
            }
            else -> {
                layout.error = null
                true
            }
        }
    }

    private fun addSubmitClickListener() {
        binding.submitRegisterButton.setOnClickListener {
            val isNameValid = validateName()
            val isPasswordValid = validatePassword()
            val isConfirmPasswordValid = validateConfirmPassword()

            if (isNameValid && isPasswordValid && isConfirmPasswordValid) {
                val name = binding.registerNameTextInput.text.toString()
                val password = binding.registerPasswordTextInput.text.toString()
                val confirmPassword = binding.registerPasswordConfirmTextInput.text.toString()

                register(name, password, confirmPassword)
            }
        }
    }

    private fun register(name: String, password: String, confirmPassword: String) {
        registerViewModel.registerLiveData.observe(viewLifecycleOwner) {
            when(it) {
                is RegisterResult.LOADING -> {
                    // Disable Button
                    binding.submitRegisterButton.isEnabled = false
                    // Reset Error Message
                    binding.registerErrorText.visibility = View.INVISIBLE
                    binding.registerErrorText.text = ""
                }
                is RegisterResult.ERROR -> {
                    // Show Error Message
                    binding.registerErrorText.visibility = View.VISIBLE
                    binding.registerErrorText.text = translateErrorMessage(it.message)
                    // Enable Button
                    binding.submitRegisterButton.isEnabled = true

                    // Clear back stack to reset navigation history
                    view?.let { innerIt ->
                        val navController = Navigation.findNavController(innerIt)
                        navController.popBackStack(R.id.registerFragment, false)
                    }
                }
                is RegisterResult.SUCCESS -> {
                    // Enable Button
                    binding.submitRegisterButton.isEnabled = true
                    // Navigate to Deck Overview
                    view?.let {innerIt ->
                        val navController = Navigation.findNavController(innerIt)
                        val currentDestination = navController.currentDestination?.id

                        if (currentDestination == R.id.registerFragment) {
                            navController.navigate(R.id.action_registerFragment_to_deckOverviewFragment)
                        }
                    }
                }
                else -> {}
            }
        }

        registerViewModel.register(RegisterRequestDto(name, password, confirmPassword))
    }

    private fun translateErrorMessage(message: String): String {
        return if (message == "API not reachable") {
            getString(R.string.api_not_reachable)
        } else {
            message
        }
    }
}