package com.graf.vocab_wizard_app.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.graf.vocab_wizard_app.R
import com.graf.vocab_wizard_app.databinding.FragmentDeckOverviewBinding

class DeckOverviewFragment : Fragment(R.layout.fragment_deck_overview) {
    private var _binding: FragmentDeckOverviewBinding? = null;
    private val binding get() = _binding!!;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDeckOverviewBinding.inflate(layoutInflater, container, false)

        binding.navigateToLoginButton.setOnClickListener {
            view?.let {
                Navigation.findNavController(it).navigate(R.id.action_deckOverviewFragment_to_loginFragment)
            }
        }

        return binding.root
    }
}