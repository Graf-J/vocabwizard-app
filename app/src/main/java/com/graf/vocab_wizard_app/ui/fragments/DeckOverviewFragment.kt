package com.graf.vocab_wizard_app.ui.fragments

import DecksResult
import DecksViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.graf.vocab_wizard_app.R
import com.graf.vocab_wizard_app.databinding.FragmentDeckOverviewBinding
import com.graf.vocab_wizard_app.ui.adapter.DeckAdapter


class DeckOverviewFragment : Fragment(R.layout.fragment_deck_overview) {
    private var _binding: FragmentDeckOverviewBinding? = null;
    private val binding get() = _binding!!;
    private val decksViewModel: DecksViewModel by viewModels()

    private lateinit var deckAdapter: DeckAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentDeckOverviewBinding.inflate(layoutInflater, container, false)

        binding.navigateToLoginButton.setOnClickListener {
            view?.let {
                Navigation.findNavController(it).navigate(R.id.action_deckOverviewFragment_to_loginFragment)
            }
        }

        // Prevent to Jump Back to Login
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { }

        // TODO: Add Refresh Functionality

        this.decksViewModel.decksLiveData.observe(viewLifecycleOwner) { decksResult ->
            when(decksResult) {
                is DecksResult.LOADING -> {
                    binding.decksSpinner.visibility = View.VISIBLE
                }
                is DecksResult.ERROR -> {
                    Log.d("Graf", decksResult.httpCode.toString())
                    // TODO: Show Error Message to User if something goes wrong
                    if (decksResult.httpCode == 403) {
                        view?.let {
                            Navigation.findNavController(it).navigate(R.id.action_deckOverviewFragment_to_loginFragment)
                        }
                    }

                    binding.decksSpinner.visibility = View.INVISIBLE
                }
                is DecksResult.SUCCESS -> {
                    Log.d("Graf", decksResult.decks.toString())
                    binding.decksRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                    deckAdapter = DeckAdapter(decksResult.decks, view)
                    binding.decksRecyclerView.adapter = deckAdapter

                    binding.decksSpinner.visibility = View.INVISIBLE
                }

                else -> {}
            }
        }

        decksViewModel.getAllDecks()

        return binding.root
    }
}