package com.graf.vocab_wizard_app.api.deck

import com.graf.vocab_wizard_app.data.dto.request.LoginRequestDto
import com.graf.vocab_wizard_app.data.dto.response.AuthResponseDto
import com.graf.vocab_wizard_app.data.dto.response.DeckResponseDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DeckApi {
    @GET("/decks")
    fun all(): Call<List<DeckResponseDto>>
}