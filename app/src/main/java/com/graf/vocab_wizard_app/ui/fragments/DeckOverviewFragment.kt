package com.graf.vocab_wizard_app.ui.fragments

import DecksResult
import com.graf.vocab_wizard_app.viewmodel.deckoverview.DecksViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.graf.vocab_wizard_app.R
import com.graf.vocab_wizard_app.databinding.FragmentDeckOverviewBinding
import com.graf.vocab_wizard_app.ui.adapter.DeckAdapter
import com.graf.vocab_wizard_app.viewmodel.deckoverview.DeleteDeckResult
import com.graf.vocab_wizard_app.viewmodel.deckoverview.ReverseDeckResult


class DeckOverviewFragment : Fragment(R.layout.fragment_deck_overview) {
    private var _binding: FragmentDeckOverviewBinding? = null
    private val binding get() = _binding!!
    private val decksViewModel: DecksViewModel by viewModels()

    private lateinit var deckAdapter: DeckAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentDeckOverviewBinding.inflate(layoutInflater, container, false)

        // Prevent to Jump Back to Login
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { }
        addListeners()
        addObservers()
        getDecks()

        return binding.root
    }

    private fun addListeners() {
        addLogoutListener()
        addAddDeckListener()
        addImportDeckListener()
        addRefreshListener()
    }

    private fun addObservers() {
        observeDecks()
        observeDeleteDeck()
        observeReverseDeck()
    }

    private fun addRefreshListener() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            getDecks()
        }
    }

    private fun addAddDeckListener() {
        binding.addDeckButton.setOnClickListener {
            view?.let {
                Navigation.findNavController(it).navigate(R.id.action_deckOverviewFragment_to_createDeckFragment)
            }
        }
    }

    private fun addImportDeckListener() {
        binding.importDeckButton.setOnClickListener {
            view?.let {
                Navigation.findNavController(it).navigate(R.id.action_deckOverviewFragment_to_importDeckFragment)
            }
        }
    }

    private fun addLogoutListener() {
        binding.navigateToLoginButton.setOnClickListener {
            // Remove AccessToken
            val sharedPref = requireContext().getSharedPreferences("Auth", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                remove("AccessToken")
                apply()
            }
            // Navigate Back
            view?.let {
                Navigation.findNavController(it).navigate(R.id.action_deckOverviewFragment_to_loginFragment)
            }
        }
    }

    private fun observeDecks() {
        this.decksViewModel.decksLiveData.observe(viewLifecycleOwner) { decksResult ->
            when(decksResult) {
                is DecksResult.LOADING -> {
                    binding.swipeRefreshLayout.isRefreshing = true
                }
                is DecksResult.ERROR -> {
                    if (decksResult.httpCode == 403 || decksResult.httpCode == 401) {
                        view?.let {
                            Navigation.findNavController(it).navigate(R.id.action_deckOverviewFragment_to_loginFragment)
                        }
                    } else if (decksResult.message == "API not reachable") {
                        view?.let {
                            Navigation.findNavController(it).navigate(R.id.action_deckOverviewFragment_to_loginFragment)
                        }
                    } else {
                        Toast.makeText(requireContext(), decksResult.message, Toast.LENGTH_SHORT).show()
                    }

                    binding.swipeRefreshLayout.isRefreshing = false
                }
                is DecksResult.SUCCESS -> {
                    binding.decksRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                    deckAdapter = DeckAdapter(decksResult.decks, decksViewModel, view)
                    binding.decksRecyclerView.adapter = deckAdapter

                    if (decksResult.decks.isEmpty()) {
                        binding.noDecksAvailableTextView.visibility = View.VISIBLE
                    }

                    binding.swipeRefreshLayout.isRefreshing = false
                }

                else -> {}
            }
        }
    }

    private fun observeDeleteDeck() {
        this.decksViewModel.deleteDeckLiveData.observe(viewLifecycleOwner) { deleteDeckResult ->
            when (deleteDeckResult) {
                is DeleteDeckResult.ERROR -> {
                    Toast.makeText(requireContext(), deleteDeckResult.message, Toast.LENGTH_SHORT).show()
                    // If Delete-Operation fails, fetch all the decks again to be up to date
                    getDecks()
                }
                is DeleteDeckResult.SUCCESS -> {
                    if (deckAdapter.itemCount == 0) {
                        binding.noDecksAvailableTextView.visibility = View.VISIBLE
                    }
                }
                else -> {}
            }
        }
    }

    private fun observeReverseDeck() {
        this.decksViewModel.reverseDeckLiveData.observe(viewLifecycleOwner) { reverseDeckResult ->
            when (reverseDeckResult) {
                is ReverseDeckResult.ERROR -> {
                    if (decksViewModel.reverseError) {
                        Toast.makeText(requireContext(), reverseDeckResult.message, Toast.LENGTH_SHORT).show()
                        decksViewModel.reverseError = false
                    }
                }
                is ReverseDeckResult.SUCCESS -> {
                    getDecks()
                }
                else -> {}
            }
        }
    }

    private fun getDecks() {
        decksViewModel.getAllDecks()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        decksViewModel.decksLiveData.removeObservers(viewLifecycleOwner)

        _binding = null
    }
}