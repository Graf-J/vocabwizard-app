package com.graf.vocab_wizard_app.viewmodel.updatedeck

import com.graf.vocab_wizard_app.data.dto.response.UpdateDeckResponseDto

sealed class UpdateDeckResult() {
    data class SUCCESS(
        val deck: UpdateDeckResponseDto
    ): UpdateDeckResult()
    data object LOADING: UpdateDeckResult()
    data class ERROR(
        val httpCode: Int,
        val message: String
    ): UpdateDeckResult()
}