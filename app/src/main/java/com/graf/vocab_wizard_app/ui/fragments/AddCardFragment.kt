package com.graf.vocab_wizard_app.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.graf.vocab_wizard_app.R
import com.graf.vocab_wizard_app.data.dto.request.CreateCardRequestDto
import com.graf.vocab_wizard_app.databinding.FragmentAddCardBinding
import com.graf.vocab_wizard_app.ui.MainActivity
import com.graf.vocab_wizard_app.viewmodel.addword.AddCardResult
import com.graf.vocab_wizard_app.viewmodel.addword.AddCardViewModel

class AddCardFragment : Fragment(R.layout.fragment_add_card) {
    private var _binding: FragmentAddCardBinding? = null
    private val binding get() = _binding!!
    private val addCardViewModel: AddCardViewModel by viewModels()

    private var cardAddedRecently = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddCardBinding.inflate(layoutInflater, container, false)

        displayLanguageImage()
        observeAddWord()
        addListeners()

        return binding.root
    }

    @SuppressLint("DiscouragedApi")
    private fun displayLanguageImage() {
        val fromLang = requireArguments().getString("fromLang")
        val resourceId = resources.getIdentifier(fromLang!!, "drawable", requireContext().packageName)
        binding.fromLangImage.setImageResource(resourceId)
    }

    private fun addListeners() {
        addWordChangedListener()
        addSubmitClickListener()
    }

    private fun addWordChangedListener() {
        binding.wordTextInput.addTextChangedListener {
            if (!cardAddedRecently) {
                validateWord()
            }
            cardAddedRecently = false
        }
    }

    private fun addSubmitClickListener() {
        binding.submitWordButton.setOnClickListener {
            val isWordValid = validateWord()
            if (isWordValid) {
                val word = binding.wordTextInput.text.toString()
                addWord(word)
            }
        }
    }

    private fun validateWord(): Boolean {
        val input = binding.wordTextInput
        val layout = binding.wordLayout

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

    private fun addWord(word: String) {
        cardAddedRecently = true
        addCardViewModel.addCard(CreateCardRequestDto(word), requireArguments().getString("id")!!)
    }

    private fun observeAddWord() {
        addCardViewModel.addCardLiveData.observe(viewLifecycleOwner) {
            when(it) {
                is AddCardResult.INITIAL -> {}
                is AddCardResult.LOADING -> {
                    // Disable
                    binding.submitWordButton.isEnabled = false
                    binding.wordLayout.isEnabled = false

                    binding.errorText.visibility = View.INVISIBLE
                    binding.errorText.text = ""
                }
                is AddCardResult.ERROR -> {
                    // Enable
                    binding.submitWordButton.isEnabled = true
                    binding.wordLayout.isEnabled = true
                    // Toast
                    binding.errorText.visibility = View.VISIBLE
                    binding.errorText.text = translateErrorMessage(it.message)
                }
                is AddCardResult.SUCCESS -> {
                    // Enable
                    binding.submitWordButton.isEnabled = true
                    binding.wordLayout.isEnabled = true
                    // Reset Input
                    binding.wordTextInput.setText("")
                    // Toast
                    Toast.makeText(MainActivity.activityContext(), getString(R.string.word_added), Toast.LENGTH_SHORT).show()
                }
                else -> {}
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