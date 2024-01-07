package com.graf.vocab_wizard_app.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.graf.vocab_wizard_app.R
import com.graf.vocab_wizard_app.data.dto.response.StatsResponseDto
import com.graf.vocab_wizard_app.databinding.FragmentStatisticsBinding
import com.graf.vocab_wizard_app.viewmodel.statistics.StatsResult
import com.graf.vocab_wizard_app.viewmodel.statistics.StatsViewModel

class StatisticsFragment : Fragment(R.layout.fragment_statistics) {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val statsViewModel: StatsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(layoutInflater, container, false)

        getStats()

        return binding.root
    }

    private fun getStats() {
        this.statsViewModel.statsLiveData.observe(viewLifecycleOwner) { statsResult ->
            when(statsResult) {
                is StatsResult.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.pieChart.visibility = View.INVISIBLE
                    binding.noCardsAvailableTextView.visibility = View.INVISIBLE
                }
                is StatsResult.ERROR -> {
                    Toast.makeText(requireContext(), translateErrorMessage(statsResult.message), Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.noCardsAvailableTextView.visibility = View.INVISIBLE
                }
                is StatsResult.SUCCESS -> {
                    if (statsResult.stats.isEmpty()) {
                        binding.noCardsAvailableTextView.visibility = View.VISIBLE
                    } else {
                        displayChart(statsResult.stats)
                        binding.pieChart.visibility = View.VISIBLE
                    }

                    binding.progressBar.visibility = View.INVISIBLE
                }

                else -> {}
            }
        }

        statsViewModel.getStats(requireArguments().getString("id")!!)
    }

    private fun displayChart(stats: List<StatsResponseDto>) {
        val pieChart = binding.pieChart

        val entries = ArrayList<PieEntry>()

        val sortedStats = stats.sortedBy { it.stage }
        for (stat in sortedStats) {
            entries.add(PieEntry(stat.count.toFloat(), stageToLabel(stat.stage)))
        }

        val pieDataSet = PieDataSet(entries, "Subjects")
        pieDataSet.colors = getColors()

        val pieData = PieData(pieDataSet)
        pieChart.data = pieData

        pieChart.legend.isEnabled = false
        pieChart.description.isEnabled = false
        pieChart.animateY(1000)
        pieChart.invalidate()
    }

    private fun getColors(): List<Int> {
        return listOf(
            Color.parseColor("#f54242"),
            Color.parseColor("#f56f42"),
            Color.parseColor("#f59c42"),
            Color.parseColor("#f5c942"),
            Color.parseColor("#f5f542"),
            Color.parseColor("#c8f542"),
            Color.parseColor("#9cf542"),
            Color.parseColor("#66f542"),
            Color.parseColor("#26c962")
        )
    }

    private fun translateErrorMessage(message: String): String {
        return if (message == "API not reachable") {
            getString(R.string.api_not_reachable)
        } else {
            message
        }
    }

    private fun stageToLabel(number: Int): String {
        return when (number) {
            0 -> getString(R.string.very_bad)
            1 -> getString(R.string.bad)
            2 -> getString(R.string.fair)
            3 -> getString(R.string.mediocre)
            4 -> getString(R.string.okay)
            5 -> getString(R.string.good)
            6 -> getString(R.string.very_good)
            7 -> getString(R.string.excellent)
            8 -> getString(R.string.amazing)
            else -> ""
        }
    }
}