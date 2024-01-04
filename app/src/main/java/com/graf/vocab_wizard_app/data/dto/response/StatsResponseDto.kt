package com.graf.vocab_wizard_app.data.dto.response

import com.google.gson.annotations.SerializedName

data class StatsResponseDto (
    @SerializedName("stage")
    val stage: Int,

    @SerializedName("count")
    val count: Int,
)