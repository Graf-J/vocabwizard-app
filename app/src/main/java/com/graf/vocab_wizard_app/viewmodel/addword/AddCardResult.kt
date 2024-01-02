package com.graf.vocab_wizard_app.viewmodel.addword

sealed class AddCardResult() {
    data object INITIAL: AddCardResult()
    data object SUCCESS: AddCardResult()
    data object LOADING: AddCardResult()
    data class ERROR(
        val httpCode: Int,
        val message: String
    ): AddCardResult()
}
