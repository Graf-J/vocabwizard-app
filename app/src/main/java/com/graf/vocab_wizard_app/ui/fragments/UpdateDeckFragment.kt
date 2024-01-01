package com.graf.vocab_wizard_app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.graf.vocab_wizard_app.R
import com.graf.vocab_wizard_app.databinding.FragmentLearnBinding
import com.graf.vocab_wizard_app.databinding.FragmentUpdateDeckBinding

class UpdateDeckFragment : Fragment(R.layout.fragment_update_deck) {
    private var _binding: FragmentUpdateDeckBinding? = null;
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUpdateDeckBinding.inflate(layoutInflater, container, false)

        val id = arguments?.getString("id")
        binding.UpdateDeckTextView.text = id

        return binding.root
    }
}