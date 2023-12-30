package com.graf.vocab_wizard_app.viewmodel.register

sealed class RegisterResult() {
    data class SUCCESS(
        val accessToken: String
    ) : RegisterResult()
    data object LOADING: RegisterResult()
    data class ERROR(
        val httpCode: Int,
        val message: String
    ) : RegisterResult()
}
