package com.graf.vocab_wizard_app.data.dto.response

data class ErrorArrayResponseDto(
    val message: List<String>,
    val error: String,
    val statusCode: String
)