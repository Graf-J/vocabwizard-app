package com.graf.vocab_wizard_app.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.graf.vocab_wizard_app.R
import com.graf.vocab_wizard_app.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null;
    private val binding get() = _binding!!;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        addListeners()

        return binding.root
    }

    private fun addListeners() {
        addRegisterNavigationClickListener()
        addSubmitClickListener()
        addUsernameChangedListener()
        addPasswordChangedListener()
    }

    private fun addRegisterNavigationClickListener() {
        binding.registerNowTextView.setOnClickListener {
            view?.let {
                Navigation.findNavController(it)
                    .navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }

    private fun addUsernameChangedListener() {
        binding.loginNameTextInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validateName(binding.loginNameTextInput, binding.loginNameLayout)
            }
        })
    }

    private fun addPasswordChangedListener() {
        binding.loginPasswordTextInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validatePassword(binding.loginPasswordTextInput, binding.loginPasswordLayout)
            }
        })
    }

    private fun addSubmitClickListener() {
        binding.submitLoginButton.setOnClickListener {
            val isUsernameValid = validateName(binding.loginNameTextInput, binding.loginNameLayout)
            val isPasswordValid = validatePassword(binding.loginPasswordTextInput, binding.loginPasswordLayout)

            if (isUsernameValid && isPasswordValid) {
                Log.d("Graf", "Login Success!")
            } else {
                Log.d("Graf", "Login Error!")
            }
        }
    }

    private fun validateName(edName: EditText, edNameL: TextInputLayout): Boolean {
        return when {
            edName.text.toString().trim().isEmpty() -> {
                edName.error = "Required"
                false
            }
            else -> {
                edName.error = null
                true
            }
        }
    }

    private fun validatePassword(edName: EditText, edNameL: TextInputLayout): Boolean {
        return when {
            edName.text.toString().trim().isEmpty() -> {
                edName.error = "Required"
                false
            }
            else -> {
                edName.error = null
                true
            }
        }
    }
}