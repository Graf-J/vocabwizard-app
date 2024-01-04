package com.graf.vocab_wizard_app.viewmodel.statistics

import com.graf.vocab_wizard_app.data.dto.response.StatsResponseDto

sealed class StatsResult() {
    data class SUCCESS(
        val stats: List<StatsResponseDto>
    ): StatsResult()
    data object LOADING: StatsResult()
    data class ERROR(
        val httpCode: Int,
        val message: String
    ) : StatsResult()
}