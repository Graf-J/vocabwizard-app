package com.graf.vocab_wizard_app.ui.fragments

import com.graf.vocab_wizard_app.viewmodel.createdeck.CreateDeckResult
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.graf.vocab_wizard_app.R
import com.graf.vocab_wizard_app.data.dto.request.CreateDeckRequestDto
import com.graf.vocab_wizard_app.databinding.FragmentCreateDeckBinding
import com.graf.vocab_wizard_app.ui.adapter.DropdownAdapter
import com.graf.vocab_wizard_app.viewmodel.createdeck.CreateDeckViewModel

class CreateDeckFragment : Fragment(R.layout.fragment_create_deck) {
    private var _binding: FragmentCreateDeckBinding? = null
    private val binding get() = _binding!!
    private val createDeckViewModel: CreateDeckViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCreateDeckBinding.inflate(layoutInflater, container, false)

        binding.slider.value = 10f
        initializeDropoutMenus()
        addListeners()

        return binding.root
    }

    private fun initializeDropoutMenus() {
        val fromLanguages = arrayListOf("EN", "DE", "ES", "FR", "IT")
        val toLanguages = arrayListOf("EN", "DE", "ES", "FR", "IT")

        val fromListAdapter = DropdownAdapter(requireContext(), R.layout.item_dropdown, fromLanguages)
        binding.fromLanguageAutoCompleteTextView.setAdapter(fromListAdapter)

        val toListAdapter = DropdownAdapter(requireContext(), R.layout.item_dropdown, toLanguages)
        binding.toLanguageAutoCompleteTextView.setAdapter(toListAdapter)
    }

    private fun addListeners() {
        addNameChangedListener()
        addFromLanguageChangedListener()
        addToLanguageChangedListener()
        addSubmitClickListener()
    }

    private fun addNameChangedListener() {
        binding.deckNameTextInput.addTextChangedListener {
            validateDeckName()
        }
    }

    private fun addFromLanguageChangedListener() {
        binding.fromLanguageAutoCompleteTextView.addTextChangedListener {
            processFromLanguage()
        }
    }

    private fun addToLanguageChangedListener() {
        binding.toLanguageAutoCompleteTextView.addTextChangedListener {
            processToLanguage()
        }
    }

    private fun addSubmitClickListener() {
        binding.submitButton.setOnClickListener {
            val isNameValid = validateDeckName()
            val isFromDropdownValid = validateFromDropdown()
            val isToDropdownValid = validateToDropdown()

            if (isNameValid && isFromDropdownValid && isToDropdownValid) {
                val name = binding.deckNameTextInput.text.toString()
                val learningRate = binding.slider.value.toInt()
                val fromLanguage = binding.fromLanguageAutoCompleteTextView.text.toString()
                val toLanguage = binding.toLanguageAutoCompleteTextView.text.toString()

                createDeck(name, learningRate, fromLanguage, toLanguage)
            }
        }
    }

    private fun validateDeckName(): Boolean {
        val input = binding.deckNameTextInput
        val layout = binding.deckNameLayout

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

    private fun processFromLanguage() {
        validateFromDropdown()

        val fromLanguage = binding.fromLanguageAutoCompleteTextView.text.toString()
        val toLanguage = binding.toLanguageAutoCompleteTextView.text.toString()

        if (fromLanguage != "EN" && toLanguage != "EN") {
            binding.toLanguageAutoCompleteTextView.setText("EN", false)
        }
        else if (fromLanguage == toLanguage) {
            binding.toLanguageAutoCompleteTextView.setText("", false)
        }
    }

    private fun processToLanguage() {
        validateToDropdown()

        val toLanguage = binding.toLanguageAutoCompleteTextView.text.toString()
        val fromLanguage = binding.fromLanguageAutoCompleteTextView.text.toString()

        if (fromLanguage != "EN" && toLanguage != "EN") {
            binding.fromLanguageAutoCompleteTextView.setText("EN", false)
        }
        else if (fromLanguage == toLanguage) {
            binding.fromLanguageAutoCompleteTextView.setText("", false)
        }
    }

    private fun validateFromDropdown(): Boolean {
        val fromInput = binding.fromLanguageAutoCompleteTextView
        val fromLayout = binding.fromLanguageAutoCompleteLayout

        return when {
            fromInput.text.toString().trim().isEmpty() -> {
                fromLayout.error = getString(R.string.required)
                false
            }
            else -> {
                fromLayout.error = null
                true
            }
        }
    }

    private fun validateToDropdown(): Boolean {
        val toInput = binding.toLanguageAutoCompleteTextView
        val toLayout = binding.toLanguageAutoCompleteLayout

        return when {
            toInput.text.toString().trim().isEmpty() -> {
                toLayout.error = getString(R.string.required)
                false
            }
            else -> {
                toLayout.error = null
                true
            }
        }
    }

    private fun createDeck(name: String, learningRate: Int, fromLang: String, toLang: String) {
        createDeckViewModel.createDeckLiveData.observe(viewLifecycleOwner) { it ->
            when(it) {
                is CreateDeckResult.LOADING -> {
                    binding.submitButton.isEnabled = false

                    binding.errorText.visibility = View.INVISIBLE
                    binding.errorText.text = ""
                }
                is CreateDeckResult.ERROR -> {
                    binding.errorText.visibility = View.VISIBLE
                    binding.errorText.text = translateErrorMessage(it.message)

                    binding.submitButton.isEnabled = true
                }
                is CreateDeckResult.SUCCESS -> {
                    binding.submitButton.isEnabled = true

                    view?.let {
                        Navigation.findNavController(it).navigate(R.id.action_createDeckFragment_to_deckOverviewFragment)
                    }
                }
                else -> {}
            }
        }

        createDeckViewModel.createDeck(CreateDeckRequestDto(name, learningRate, fromLang.lowercase(), toLang.lowercase()))
    }
    
    private fun translateErrorMessage(message: String): String {
        return if (message == "API not reachable") {
            getString(R.string.api_not_reachable)
        } else {
            message
        }
    }
}