package com.graf.vocab_wizard_app.viewmodel.learn

sealed class ConfidenceResult {
    data object INITIAL : ConfidenceResult()
    data object SUCCESS : ConfidenceResult()
    data object LOADING : ConfidenceResult()
    data class ERROR(
        val httpCode: Int,
        val message: String
    ) : ConfidenceResult()
}
