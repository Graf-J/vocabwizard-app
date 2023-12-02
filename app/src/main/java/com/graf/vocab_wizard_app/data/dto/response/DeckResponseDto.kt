package com.graf.vocab_wizard_app.data.dto.response

import com.google.gson.annotations.SerializedName

data class DeckResponseDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("learningRate")
    val learningRate: Int,

    @SerializedName("fromLang")
    val fromLang: String,

    @SerializedName("toLang")
    val toLang: String,

    @SerializedName("newCardCount")
    val newCardCount: Int,

    @SerializedName("oldCardCount")
    val oldCardCount: Int
)

