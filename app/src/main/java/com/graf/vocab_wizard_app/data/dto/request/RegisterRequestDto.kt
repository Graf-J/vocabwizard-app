package com.graf.vocab_wizard_app.data.dto.request

data class RegisterRequestDto(
    val name: String,
    val password: String,
    val passwordConfirmation: String
)