package com.graf.vocab_wizard_app.ui.fragments

import CardsResult
import CardsViewModel
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Configuration
import android.media.AudioManager.STREAM_MUSIC
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.graf.vocab_wizard_app.R
import com.graf.vocab_wizard_app.data.dto.request.ConfidenceRequestDto
import com.graf.vocab_wizard_app.databinding.FragmentLearnBinding
import com.graf.vocab_wizard_app.ui.MainActivity
import com.graf.vocab_wizard_app.viewmodel.learn.ConfidenceResult

class LearnFragment : Fragment(R.layout.fragment_learn) {
    private var _binding: FragmentLearnBinding? = null
    private val binding get() = _binding!!

    private val cardsViewModel: CardsViewModel by viewModels()

    private var flipAnimator: ValueAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentLearnBinding.inflate(layoutInflater, container, false)

        initializeFlipAnimator()
        addListeners()
        loadCards()
        observeConfidence()

        return binding.root
    }

    private fun addListeners() {
        addCardClickListener()
        addEasyButtonListener()
        addGoodButtonListener()
        addHardButtonListener()
        addRepeatButtonListener()
        addAudioButtonListener()
    }

    private fun addCardClickListener() {
        binding.cardFront.setOnClickListener {
            toggleButtons(true)
            flipAnimator!!.start()
            cardsViewModel.currentRotationAngle = if (cardsViewModel.isFront) 0f else 180f
        }
    }

    private fun addEasyButtonListener() {
        binding.easyButton.setOnClickListener {
            handleNonRepeat("easy")
        }
    }

    private fun addGoodButtonListener() {
        binding.goodButton.setOnClickListener {
            handleNonRepeat("good")
        }
    }

    private fun addHardButtonListener() {
        binding.hardButton.setOnClickListener {
            handleNonRepeat("hard")
        }
    }

    private fun addRepeatButtonListener() {
        binding.repeatButton.setOnClickListener {
            moveFirstCardToEnd()
            updateCardUI()
            toggleButtons(false)
            flipAnimator!!.reverse()
        }
    }

    private fun handleNonRepeat(label: String) {
        updateConfidence(label)
        removeFirstCard()
        if (cardsViewModel.cards.size != 0) {
            updateCardUI()
            toggleButtons(false)
            flipAnimator!!.reverse()
        }
    }

    private fun addAudioButtonListener() {
        binding.audioButton.setOnClickListener {
            if (isNetworkAvailable()) {
                try {
                    if (cardsViewModel.mediaPlayer.isPlaying) {
                        cardsViewModel.mediaPlayer.stop()
                        cardsViewModel.mediaPlayer.reset()
                    }

                    // Set audio stream type and data source
                    cardsViewModel.mediaPlayer.setAudioStreamType(STREAM_MUSIC)
                    cardsViewModel.mediaPlayer.setDataSource(cardsViewModel.audioLink)

                    // Set up completion listener
                    cardsViewModel.mediaPlayer.setOnCompletionListener {
                        cardsViewModel.mediaPlayer.reset()
                    }

                    val playbackParams = cardsViewModel.mediaPlayer.playbackParams
                    playbackParams.speed = 0.75f
                    cardsViewModel.mediaPlayer.playbackParams = playbackParams

                    // Prepare and start the media player
                    cardsViewModel.mediaPlayer.prepare()
                    cardsViewModel.mediaPlayer.start()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(requireContext(), requireView().context.getString(R.string.noInternetConnection), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCards() {
        // Check if cards are already loaded (helpful when changing orientation)
        if (cardsViewModel.cards.isEmpty()) {
            getCards(arguments?.getString("id")!!)
        } else {
            updateCardUI()
            flipAnimator!!.currentPlayTime = cardsViewModel.currentRotationAngle.toLong()
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun observeConfidence() {
        this.cardsViewModel.confidenceLiveData.observe(viewLifecycleOwner) { confidenceResult ->
            when(confidenceResult) {
                is ConfidenceResult.INITIAL -> { }
                is ConfidenceResult.LOADING -> {
                    enableButtons(false)
                }
                is ConfidenceResult.ERROR -> {
                    if (confidenceResult.httpCode == 403) {
                        view?.let {
                            Navigation.findNavController(it).navigate(R.id.action_deckOverviewFragment_to_loginFragment)
                        }
                    } else {
                        Toast.makeText(MainActivity.activityContext(), confidenceResult.message, Toast.LENGTH_SHORT).show()
                    }
                    enableButtons(true)
                    tryNavigateBack()
                }
                is ConfidenceResult.SUCCESS -> {
                    enableButtons(true)
                    tryNavigateBack()
                }
            }
        }
    }

    private fun updateConfidence(confidence: String) {
        val deckId = arguments?.getString("id")!!
        val cardId = cardsViewModel.cards[0].id
        val payload = ConfidenceRequestDto(confidence)

        cardsViewModel.updateCardConfidence(payload, deckId, cardId)
    }

    private fun getCards(deckId: String) {
        this.cardsViewModel.cardsLiveData.observe(viewLifecycleOwner) { cardsResult ->
            when(cardsResult) {
                is CardsResult.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.cardFront.visibility = View.INVISIBLE
                    binding.cardBack.visibility = View.INVISIBLE
                }
                is CardsResult.ERROR -> {
                    if (cardsResult.httpCode == 403) {
                        view?.let {
                            Navigation.findNavController(it).navigate(R.id.action_deckOverviewFragment_to_loginFragment)
                        }
                    } else {
                        Toast.makeText(MainActivity.activityContext(), cardsResult.message, Toast.LENGTH_SHORT).show()
                    }

                    binding.progressBar.visibility = View.INVISIBLE
                    binding.cardFront.visibility = View.VISIBLE
                }
                is CardsResult.SUCCESS -> {
                    cardsViewModel.cards = cardsResult.cards.toMutableList()
                    updateCardUI()

                    binding.progressBar.visibility = View.INVISIBLE
                    binding.cardFront.visibility = View.VISIBLE
                }
                else -> {}
            }
        }

        cardsViewModel.getLearnCards(deckId)
    }

    private fun updateCardUI() {
        // Prevent issue if signal transit time of Confidence is slower than forced user input to navigate back
        if(tryNavigateBack()) {
            return
        }

        // Update Card in the middle of the animation (after 500ms)
        Handler(Looper.getMainLooper()).postDelayed({
            // Front Card
            binding.word.text = cardsViewModel.cards[0].word

            // Back Card
            binding.translation.text = cardsViewModel.cards[0].translation

            if (cardsViewModel.cards[0].audioLink != null) {
                binding.audioButton.isEnabled = true
                cardsViewModel.audioLink = cardsViewModel.cards[0].audioLink
            } else {
                binding.audioButton.isEnabled = false
            }

            binding.phonetic.text = cardsViewModel.cards[0].phonetic
            binding.definitions.text = buildString {
                for (definition in cardsViewModel.cards[0].definitions) {
                    append("- $definition\n")
                }
            }
            binding.examples.text = buildString {
                for (example in cardsViewModel.cards[0].examples) {
                    append("- $example\n")
                }
            }
            binding.synonyms.text = buildString {
                for (synonym in cardsViewModel.cards[0].synonyms) {
                    append("- $synonym\n")
                }
            }
            binding.antonyms.text = buildString {
                for (antonym in cardsViewModel.cards[0].antonyms) {
                    append("- $antonym\n")
                }
            }
        }, 500)
    }

    private fun initializeFlipAnimator() {
        flipAnimator = createFlipAnimator()
        if (cardsViewModel.isFront) {
            binding.cardFront.visibility = View.VISIBLE
            binding.cardBack.visibility = View.GONE
        } else {
            toggleButtons(true)
            binding.cardFront.visibility = View.GONE
            binding.cardBack.visibility = View.VISIBLE
            flipAnimator!!.start()
        }
    }

    private fun createFlipAnimator(): ValueAnimator {
        val animator = ValueAnimator.ofFloat(cardsViewModel.currentRotationAngle, cardsViewModel.currentRotationAngle + 180f)
        animator.duration = 1000
        animator.interpolator = AccelerateDecelerateInterpolator()

        val distance = 8000.0f
        binding.cardFront.cameraDistance = distance
        binding.cardBack.cameraDistance = distance

        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            binding.cardFront.rotationY = if (cardsViewModel.isFront) value else 180 - value
            binding.cardBack.rotationY = if (cardsViewModel.isFront) 180 - value else value

            // Adjust scaling of the Text
            val scale = if (value >= 90f) -1f else 1f
            binding.cardBack.scaleX = scale

            if (value >= 90f) {
                if (cardsViewModel.isFront) {
                    cardsViewModel.isFront = false
                    binding.cardFront.visibility = View.GONE
                    binding.cardBack.visibility = View.VISIBLE
                }
            } else {
                if (!cardsViewModel.isFront) {
                    cardsViewModel.isFront = true
                    binding.cardBack.visibility = View.GONE
                    binding.cardFront.visibility = View.VISIBLE
                }
            }
        }

        return animator
    }

    private fun removeFirstCard() {
        cardsViewModel.cards = cardsViewModel.cards.drop(1).toMutableList()

        if (cardsViewModel.cards.size != 0) {
            updateCardUI()
        }
    }

    private fun moveFirstCardToEnd() {
        // Gets executed for Repeat-Operation
        val fistCard = cardsViewModel.cards.removeAt(0)
        cardsViewModel.cards.add(fistCard)

        updateCardUI()
    }

    private fun toggleButtons(setVisible: Boolean) {
        if (setVisible) {
            // Wait 1s for Flip-Animation to finish before showing Buttons
            Handler(Looper.getMainLooper()).postDelayed({
                binding.easyButton.visibility = View.VISIBLE
                binding.goodButton.visibility = View.VISIBLE
                binding.hardButton.visibility = View.VISIBLE
                binding.repeatButton.visibility = View.VISIBLE
            }, 1000)
        } else {
            binding.easyButton.visibility = View.INVISIBLE
            binding.goodButton.visibility = View.INVISIBLE
            binding.hardButton.visibility = View.INVISIBLE
            binding.repeatButton.visibility = View.INVISIBLE
        }
    }

    private fun enableButtons(enable: Boolean) {
        binding.easyButton.isEnabled = enable
        binding.goodButton.isEnabled = enable
        binding.hardButton.isEnabled = enable
        binding.repeatButton.isEnabled = enable
    }

    private fun tryNavigateBack(): Boolean {
        // If there are no more cards the user gets navigated back to the Deck-Overview
        if (cardsViewModel.cards.size == 0) {
            view?.let {
                Navigation.findNavController(it).navigate(R.id.action_learnFragment_to_deckOverviewFragment)
            }
            return true
        }
        return false
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val networkInfo = connectivityManager?.activeNetworkInfo
        return networkInfo?.isConnected == true
    }
}