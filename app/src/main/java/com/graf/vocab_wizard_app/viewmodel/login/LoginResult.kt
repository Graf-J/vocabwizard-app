package com.graf.vocab_wizard_app.viewmodel.login

sealed class LoginResult() {
    data class SUCCESS(
        val accessToken: String
    ) : LoginResult()
    data object LOADING: LoginResult()
    data class ERROR(
        val httpCode: Int,
        val message: String
    ) : LoginResult()
}
