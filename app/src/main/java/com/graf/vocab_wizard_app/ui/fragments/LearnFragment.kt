package com.graf.vocab_wizard_app.ui.fragments

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.fragment.app.Fragment
import com.graf.vocab_wizard_app.R
import com.graf.vocab_wizard_app.databinding.FragmentDeckOverviewBinding
import com.graf.vocab_wizard_app.databinding.FragmentLearnBinding

class LearnFragment : Fragment(R.layout.fragment_learn) {
    private var _binding: FragmentLearnBinding? = null
    private val binding get() = _binding!!

    private lateinit var cardFront: View
    private lateinit var cardBack: View
    private lateinit var flipBtn: Button
    private var flipAnimator: ValueAnimator? = null
    private var isFront = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentLearnBinding.inflate(layoutInflater, container, false)

        // Flip
        cardFront = binding.cardFront
        cardBack = binding.cardBack
        flipBtn = binding.flipBtn

        // Set initial visibility
        cardFront.visibility = View.VISIBLE
        cardBack.visibility = View.GONE

        flipAnimator = createFlipAnimator()

        flipBtn.setOnClickListener {
            if (!flipAnimator!!.isRunning) {
                if (isFront) {
                    flipAnimator!!.start()
                } else {
                    flipAnimator!!.reverse()
                }
            }
        }

        return binding.root
    }

    private fun createFlipAnimator(): ValueAnimator {
        val animator = ValueAnimator.ofFloat(0f, 180f)
        animator.duration = 1000
        animator.interpolator = AccelerateDecelerateInterpolator()

        // Setzen Sie die Kameraentfernung für die Perspektive
        val distance = 8000.0f
        cardFront.cameraDistance = distance
        cardBack.cameraDistance = distance

        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            cardFront.rotationY = if (isFront) value else 180 - value
            cardBack.rotationY = if (isFront) 180 - value else value

            // Hier passen wir die Skalierung des Texts auf der Rückseite an
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