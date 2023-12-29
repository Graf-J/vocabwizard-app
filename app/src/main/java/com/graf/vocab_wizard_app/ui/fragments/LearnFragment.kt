package com.graf.vocab_wizard_app.ui.fragments

import CardsResult
import CardsViewModel
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.media.AudioManager
import android.media.AudioManager.STREAM_MUSIC
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.graf.vocab_wizard_app.R
import com.graf.vocab_wizard_app.data.dto.request.ConfidenceRequestDto
import com.graf.vocab_wizard_app.data.dto.response.CardResponseDto
import com.graf.vocab_wizard_app.databinding.FragmentDeckOverviewBinding
import com.graf.vocab_wizard_app.databinding.FragmentLearnBinding
import com.graf.vocab_wizard_app.ui.MainActivity
import com.graf.vocab_wizard_app.viewmodel.learn.ConfidenceResult

class LearnFragment : Fragment(R.layout.fragment_learn) {
    private var _binding: FragmentLearnBinding? = null
    private val binding get() = _binding!!

    private val cardsViewModel: CardsViewModel by viewModels()

    private lateinit var cardFront: View
    private lateinit var cardBack: View
    private var flipAnimator: ValueAnimator? = null
    private var isFront = true
    private val mediaPlayer = MediaPlayer()

    private var cards = mutableListOf<CardResponseDto>()
    private var audioLink: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentLearnBinding.inflate(layoutInflater, container, false)

        initializeAnimator()
        addListeners()

        getCards(arguments?.getString("id")!!)
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
            toggleButtons()
            flipAnimator!!.start()
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
            toggleButtons()
            flipAnimator!!.reverse()
        }
    }

    private fun handleNonRepeat(label: String) {
        updateConfidence(label)
        removeFirstCard()
        if (cards.size != 0) {
            updateCardUI()
            toggleButtons()
            flipAnimator!!.reverse()
        }
    }

    private fun addAudioButtonListener() {
        binding.audioButton.setOnClickListener {
            try {

                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                }

                // Set audio stream type and data source
                mediaPlayer.setAudioStreamType(STREAM_MUSIC)
                mediaPlayer.setDataSource(audioLink)

                // Set up completion listener
                mediaPlayer.setOnCompletionListener {
                    mediaPlayer.reset()
                }

                val playbackParams = mediaPlayer.playbackParams
                playbackParams.speed = 0.75f // 1.0f is normal speed, adjust as needed
                mediaPlayer.playbackParams = playbackParams

                // Prepare and start the media player
                mediaPlayer.prepare()
                mediaPlayer.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
        val cardId = cards[0].id
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
                    this.cards = cardsResult.cards.toMutableList()
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
            binding.word.text = cards[0].word

            // Back Card
            binding.translation.text = cards[0].translation

            if (cards[0].audioLink != null) {
                binding.audioButton.isEnabled = true
                audioLink = cards[0].audioLink
            } else {
                binding.audioButton.isEnabled = false
            }

            binding.phonetic.text = cards[0].phonetic
            binding.definitions.text = buildString {
                for (definition in cards[0].definitions) {
                    append("- $definition\n")
                }
            }
            binding.examples.text = buildString {
                for (example in cards[0].examples) {
                    append("- $example\n")
                }
            }
            binding.synonyms.text = buildString {
                for (synonym in cards[0].synonyms) {
                    append("- $synonym\n")
                }
            }
            binding.antonyms.text = buildString {
                for (antonym in cards[0].antonyms) {
                    append("- $antonym\n")
                }
            }
        }, 500)
    }

    private fun initializeAnimator() {
        cardFront = binding.cardFront
        cardBack = binding.cardBack

        // Set initial visibility
        cardFront.visibility = View.VISIBLE
        cardBack.visibility = View.GONE

        flipAnimator = createFlipAnimator()
    }

    private fun createFlipAnimator(): ValueAnimator {
        val animator = ValueAnimator.ofFloat(0f, 180f)
        animator.duration = 1000
        animator.interpolator = AccelerateDecelerateInterpolator()

        val distance = 8000.0f
        cardFront.cameraDistance = distance
        cardBack.cameraDistance = distance

        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            cardFront.rotationY = if (isFront) value else 180 - value
            cardBack.rotationY = if (isFront) 180 - value else value

            // Adjust scaling of the Text
            val scale = if (value >= 90f) -1f else 1f
            cardBack.scaleX = scale

            if (value >= 90f) {
                if (isFront) {
                    isFront = false
                    cardFront.visibility = View.GONE
                    cardBack.visibility = View.VISIBLE
                }
            } else {
                if (!isFront) {
                    isFront = true
                    cardBack.visibility = View.GONE
                    cardFront.visibility = View.VISIBLE
                }
            }
        }

        return animator
    }

    private fun removeFirstCard() {
        cards = cards.drop(1).toMutableList()

        if (cards.size != 0) {
            updateCardUI()
        }
    }

    private fun moveFirstCardToEnd() {
        val fistCard = cards.removeAt(0)
        cards.add(fistCard)

        updateCardUI()
    }

    private fun toggleButtons() {
        if (!isFront) {
            binding.easyButton.visibility = View.INVISIBLE
            binding.goodButton.visibility = View.INVISIBLE
            binding.hardButton.visibility = View.INVISIBLE
            binding.repeatButton.visibility = View.INVISIBLE
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.easyButton.visibility = View.VISIBLE
                binding.goodButton.visibility = View.VISIBLE
                binding.hardButton.visibility = View.VISIBLE
                binding.repeatButton.visibility = View.VISIBLE
            }, 1000)
        }
    }

    private fun enableButtons(enable: Boolean) {
        binding.easyButton.isEnabled = enable
        binding.goodButton.isEnabled = enable
        binding.hardButton.isEnabled = enable
        binding.repeatButton.isEnabled = enable
    }

    private fun tryNavigateBack(): Boolean {
        if (cards.size == 0) {
            view?.let {
                Navigation.findNavController(it).navigate(R.id.action_learnFragment_to_deckOverviewFragment)
            }
            return true
        }
        return false
    }
}