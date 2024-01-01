package com.graf.vocab_wizard_app.viewmodel.deckoverview

sealed class DeleteDeckResult() {
    data object SUCCESS: DeleteDeckResult()
    data object LOADING: DeleteDeckResult()
    data class ERROR(
        val httpCode: Int,
        val message: String
    ) : DeleteDeckResult()
}
