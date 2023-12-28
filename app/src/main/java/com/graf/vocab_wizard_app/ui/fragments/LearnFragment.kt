package com.graf.vocab_wizard_app.ui.fragments

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
import com.graf.vocab_wizard_app.R
import com.graf.vocab_wizard_app.databinding.FragmentDeckOverviewBinding
import com.graf.vocab_wizard_app.databinding.FragmentLearnBinding

class LearnFragment : Fragment(R.layout.fragment_learn) {
    private var _binding: FragmentLearnBinding? = null
    private val binding get() = _binding!!

    private lateinit var cardFront: View
    private lateinit var cardBack: View
    private var flipAnimator: ValueAnimator? = null
    private var isFront = true
    private val mediaPlayer = MediaPlayer()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentLearnBinding.inflate(layoutInflater, container, false)

        initializeAnimator()
        addListeners()

        return binding.root
    }

    private fun addListeners() {
        addNextButtonListener()
        addEasyButtonListener()
        addGoodButtonListener()
        addHardButtonListener()
        addRepeatButtonListener()
        addAudioButtonListener()
    }

    private fun addNextButtonListener() {
        binding.cardFront.setOnClickListener {
            toggleButtons()
            flipAnimator!!.start()
        }
    }

    private fun addEasyButtonListener() {
        binding.easyButton.setOnClickListener {
            toggleButtons()
            flipAnimator!!.reverse()
        }
    }

    private fun addGoodButtonListener() {
        binding.goodButton.setOnClickListener {
            toggleButtons()
            flipAnimator!!.reverse()
        }
    }

    private fun addHardButtonListener() {
        binding.hardButton.setOnClickListener {
            toggleButtons()
            flipAnimator!!.reverse()
        }
    }

    private fun addRepeatButtonListener() {
        binding.repeatButton.setOnClickListener {
            toggleButtons()
            flipAnimator!!.reverse()
        }
    }

    private fun addAudioButtonListener() {
        binding.audioButton.setOnClickListener {
            val audioUrl = "https://api.dictionaryapi.dev/media/pronunciations/en/strong-uk.mp3"

            try {

                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                }

                // Set audio stream type and data source
                mediaPlayer.setAudioStreamType(STREAM_MUSIC)
                mediaPlayer.setDataSource(audioUrl)

                // Set up completion listener
                mediaPlayer.setOnCompletionListener {
                    mediaPlayer.reset()
                    Toast.makeText(requireContext(), "Audio finished playing.", Toast.LENGTH_SHORT).show()
                }

                val playbackParams = mediaPlayer.playbackParams
                playbackParams.speed = 0.75f // 1.0f is normal speed, adjust as needed
                mediaPlayer.playbackParams = playbackParams

                // Prepare and start the media player
                mediaPlayer.prepare()
                mediaPlayer.start()

                Toast.makeText(requireContext(), "Audio started playing..", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
}