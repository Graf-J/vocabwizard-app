package com.graf.vocab_wizard_app.data.dto.response

import com.google.gson.annotations.SerializedName

data class CardResponseDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("word")
    val word: String,

    @SerializedName("translation")
    val translation: String,

    @SerializedName("phonetic")
    val phonetic: String?,

    @SerializedName("audioLink")
    val audioLink: String?,

    @SerializedName("definitions")
    val definitions: List<String>,

    @SerializedName("examples")
    val examples: List<String>,

    @SerializedName("synonyms")
    val synonyms: List<String>,

    @SerializedName("antonyms")
    val antonyms: List<String>,
)

