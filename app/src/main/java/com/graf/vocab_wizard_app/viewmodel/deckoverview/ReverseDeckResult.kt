package com.graf.vocab_wizard_app.viewmodel.deckoverview

sealed class ReverseDeckResult() {
    data object SUCCESS: ReverseDeckResult()
    data object LOADING: ReverseDeckResult()
    data class ERROR(
        val httpCode: Int,
        val message: String
    ) : ReverseDeckResult()
}

