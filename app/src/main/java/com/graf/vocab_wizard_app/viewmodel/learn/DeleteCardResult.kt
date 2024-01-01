package com.graf.vocab_wizard_app.viewmodel.learn

sealed class DeleteCardResult() {
    data object SUCCESS: DeleteCardResult()
    data object LOADING: DeleteCardResult()
    data class ERROR(
        val httpCode: Int,
        val message: String
    ): DeleteCardResult()
}
