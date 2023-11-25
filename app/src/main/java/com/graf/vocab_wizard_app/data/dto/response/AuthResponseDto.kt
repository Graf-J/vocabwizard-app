package com.graf.vocab_wizard_app.data.dto.response

import com.google.gson.annotations.SerializedName

data class AuthResponseDto(
    @SerializedName("AccessToken")
    val accessToken: String
)
