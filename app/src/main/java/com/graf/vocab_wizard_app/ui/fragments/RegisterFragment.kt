package com.graf.vocab_wizard_app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.graf.vocab_wizard_app.R
import com.graf.vocab_wizard_app.databinding.FragmentLoginBinding
import com.graf.vocab_wizard_app.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment(R.layout.fragment_register) {
    private var _binding: FragmentRegisterBinding? = null;
    private val binding get() = _binding!!;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)

        binding.navigateToLoginButton.setOnClickListener {
            view?.let {
                Navigation.findNavController(it).navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }

        return binding.root
    }
}