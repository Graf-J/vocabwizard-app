package com.graf.vocab_wizard_app.viewmodel.importdeck

sealed class ImportDeckResult() {
    data object INITIAL: ImportDeckResult()
    data object SUCCESS: ImportDeckResult()
    data object LOADING: ImportDeckResult()
    data class ERROR(
        val httpCode: Int,
        val message: String
    ): ImportDeckResult()
}