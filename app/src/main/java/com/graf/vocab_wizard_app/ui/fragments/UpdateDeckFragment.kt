package com.graf.vocab_wizard_app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.graf.vocab_wizard_app.R
import com.graf.vocab_wizard_app.data.dto.request.UpdateDeckRequestDto
import com.graf.vocab_wizard_app.databinding.FragmentUpdateDeckBinding
import com.graf.vocab_wizard_app.viewmodel.updatedeck.UpdateDeckResult
import com.graf.vocab_wizard_app.viewmodel.updatedeck.UpdateDeckViewModel

class UpdateDeckFragment : Fragment(R.layout.fragment_update_deck) {
    private var _binding: FragmentUpdateDeckBinding? = null
    private val binding get() = _binding!!
    private val updateDeckViewModel: UpdateDeckViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUpdateDeckBinding.inflate(layoutInflater, container, false)

        insertDeckData()
        displayLanguageImages()
        addListeners()

        return binding.root
    }

    private fun insertDeckData() {
        binding.deckNameTextInput.setText(requireArguments().getString("name")!!)
        binding.slider.value = requireArguments().getInt("learningRate").toFloat()
    }

    private fun displayLanguageImages() {
        val fromLang = requireArguments().getString("fromLang")
        val fromLangResourceId = resources.getIdentifier(fromLang!!, "drawable", requireContext().packageName)
        binding.fromLangImage.setImageResource(fromLangResourceId)

        val toLang = requireArguments().getString("toLang")
        val toLangResourceId = resources.getIdentifier(toLang!!, "drawable", requireContext().packageName)
        binding.toLangImage.setImageResource(toLangResourceId)
    }

    private fun addListeners() {
        addNameChangedListener()
        addFromLanguageListener()
        addToLanguageListener()
        addSubmitClickListener()
    }

    private fun addNameChangedListener() {
        binding.deckNameTextInput.addTextChangedListener {
            validateDeckName()
        }
    }

    private fun addFromLanguageListener() {
        binding.fromLangImage.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.lang_change_forbidden), Toast.LENGTH_SHORT).show()
        }
    }

    private fun addToLanguageListener() {
        binding.toLangImage.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.lang_change_forbidden), Toast.LENGTH_SHORT).show()
        }
    }

    private fun addSubmitClickListener() {
        binding.submitButton.setOnClickListener {
            val name = binding.deckNameTextInput.text.toString()
            val learningRate = binding.slider.value.toInt()
            val prevName = requireArguments().getString("name")!!
            val prevLearningRate = requireArguments().getInt("learningRate")!!

            val isNameValid = validateDeckName()
            val dataChanged = name != prevName || learningRate != prevLearningRate

            if (isNameValid) {
                // Check if User changed Data
                if (dataChanged) {
                    updateDeck(name, learningRate)
                } else {
                    view?.let {
                        Navigation.findNavController(it).navigate(R.id.action_updateDeckFragment_to_deckOverviewFragment)
                    }
                }
            }
        }
    }

    private fun updateDeck(name: String, learningRate: Int) {
        updateDeckViewModel.updateDeckLiveData.observe(viewLifecycleOwner) { it ->
            when(it) {
                is UpdateDeckResult.LOADING -> {
                    binding.submitButton.isEnabled = false
                    binding.deckNameLayout.isEnabled = false

                    binding.errorText.visibility = View.INVISIBLE
                    binding.errorText.text = ""
                }
                is UpdateDeckResult.ERROR -> {
                    binding.errorText.visibility = View.VISIBLE
                    binding.errorText.text = translateErrorMessage(it.message)

                    binding.submitButton.isEnabled = true
                    binding.deckNameLayout.isEnabled = true
                }
                is UpdateDeckResult.SUCCESS -> {
                    binding.submitButton.isEnabled = true
                    binding.deckNameLayout.isEnabled = true

                    view?.let {
                        Navigation.findNavController(it).navigate(R.id.action_updateDeckFragment_to_deckOverviewFragment)
                    }
                }
                else -> {}
            }
        }

        updateDeckViewModel.updateDeck(UpdateDeckRequestDto(name, learningRate), requireArguments().getString("id")!!)
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

    private fun translateErrorMessage(message: String): String {
        return if (message == "API not reachable") {
            getString(R.string.api_not_reachable)
        } else {
            message
        }
    }
}