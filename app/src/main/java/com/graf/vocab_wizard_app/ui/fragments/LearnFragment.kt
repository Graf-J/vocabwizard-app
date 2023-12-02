package com.graf.vocab_wizard_app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.graf.vocab_wizard_app.R
import com.graf.vocab_wizard_app.databinding.FragmentDeckOverviewBinding
import com.graf.vocab_wizard_app.databinding.FragmentLearnBinding

class LearnFragment : Fragment(R.layout.fragment_learn) {
    private var _binding: FragmentLearnBinding? = null;
    private val binding get() = _binding!!;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentLearnBinding.inflate(layoutInflater, container, false)

        val id = arguments?.getString("id")
        binding.LearnTextView.text = id

        return binding.root
    }
}