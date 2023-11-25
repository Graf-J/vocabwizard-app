package com.graf.vocab_wizard_app.data.dto.response

data class ErrorResponseDto(
    val message: String,
    val error: String,
    val statusCode: String
)