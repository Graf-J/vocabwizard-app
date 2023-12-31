package com.graf.vocab_wizard_app.data.dto.request

data class CreateDeckRequestDto(
    val name: String,
    val learningRate: Int,
    val fromLang: String,
    val toLang: String
)
