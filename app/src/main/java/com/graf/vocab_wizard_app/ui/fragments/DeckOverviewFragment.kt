package com.graf.vocab_wizard_app.ui.fragments

import DecksResult
import DecksViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.graf.vocab_wizard_app.R
import com.graf.vocab_wizard_app.databinding.FragmentDeckOverviewBinding
import com.graf.vocab_wizard_app.ui.MainActivity
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

        // TODO: Remove this function
        binding.navigateToLoginButton.setOnClickListener {
            view?.let {
                Navigation.findNavController(it).navigate(R.id.action_deckOverviewFragment_to_loginFragment)
            }
        }

        // Prevent to Jump Back to Login
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { }

        addRefreshListener()

        observeDecks()
        getDecks()

        return binding.root
    }

    private fun addRefreshListener() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            getDecks()
        }
    }

    private fun observeDecks() {
        this.decksViewModel.decksLiveData.observe(viewLifecycleOwner) { decksResult ->
            when(decksResult) {
                is DecksResult.LOADING -> {
                    binding.swipeRefreshLayout.isRefreshing = true
                }
                is DecksResult.ERROR -> {
                    if (decksResult.httpCode == 403) {
                        view?.let {
                            Navigation.findNavController(it).navigate(R.id.action_deckOverviewFragment_to_loginFragment)
                        }
                    } else {
                        Toast.makeText(MainActivity.activityContext(), decksResult.message, Toast.LENGTH_SHORT).show()
                    }

                    binding.swipeRefreshLayout.isRefreshing = false
                }
                is DecksResult.SUCCESS -> {
                    binding.decksRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                    deckAdapter = DeckAdapter(decksResult.decks, view)
                    binding.decksRecyclerView.adapter = deckAdapter

                    binding.swipeRefreshLayout.isRefreshing = false
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