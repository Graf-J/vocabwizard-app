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
import com.graf.vocab_wizard_app.data.dto.request.ImportDeckRequestDto
import com.graf.vocab_wizard_app.databinding.FragmentImportDeckBinding
import com.graf.vocab_wizard_app.viewmodel.importdeck.ImportDeckResult
import com.graf.vocab_wizard_app.viewmodel.importdeck.ImportDeckViewModel

class ImportDeckFragment : Fragment(R.layout.fragment_import_deck) {
    private var _binding: FragmentImportDeckBinding? = null
    private val binding get() = _binding!!
    private val importDeckViewModel: ImportDeckViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentImportDeckBinding.inflate(layoutInflater, container, false)

        addListeners()

        return binding.root
    }

    private fun addListeners() {
        addWordChangedListener()
        addSubmitClickListener()
    }

    private fun addWordChangedListener() {
        binding.deckIdTextInput.addTextChangedListener {
            validateDeckId()
        }
    }

    private fun addSubmitClickListener() {
        binding.submitButton.setOnClickListener {
            val isDeckIdValid = validateDeckId()
            if (isDeckIdValid) {
                val deckId = binding.deckIdTextInput.text.toString()
                importDeck(deckId)
            }
        }
    }

    private fun validateDeckId(): Boolean {
        val input = binding.deckIdTextInput
        val layout = binding.deckIdLayout

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

    private fun importDeck(deckId: String) {
        importDeckViewModel.importDeckLiveData.observe(viewLifecycleOwner) { importDeckResult ->
            when(importDeckResult) {
                is ImportDeckResult.INITIAL -> {}
                is ImportDeckResult.LOADING -> {
                    // Disable
                    binding.submitButton.isEnabled = false
                    binding.deckIdLayout.isEnabled = false

                    binding.errorText.visibility = View.INVISIBLE
                    binding.errorText.text = ""
                }
                is ImportDeckResult.ERROR -> {
                    // Enable
                    binding.submitButton.isEnabled = true
                    binding.deckIdLayout.isEnabled = true
                    // Toast
                    binding.errorText.visibility = View.VISIBLE
                    binding.errorText.text = translateErrorMessage(importDeckResult.message)
                }
                is ImportDeckResult.SUCCESS -> {
                    // Enable
                    binding.submitButton.isEnabled = true
                    binding.deckIdLayout.isEnabled = true
                    // Reset Input
                    binding.errorText.text = ""

                    view?.let {
                        Navigation.findNavController(it).navigate(R.id.action_importDeckFragment_to_deckOverviewFragment)
                    }
                }
                else -> {}
            }
        }

        importDeckViewModel.importDeck(ImportDeckRequestDto(deckId))
    }

    private fun translateErrorMessage(message: String): String {
        return if (message == "API not reachable") {
            getString(R.string.api_not_reachable)
        } else {
            message
        }
    }
}