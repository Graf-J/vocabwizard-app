package com.graf.vocab_wizard_app.api.deck

import com.graf.vocab_wizard_app.data.dto.request.LoginRequestDto
import com.graf.vocab_wizard_app.data.dto.response.AuthResponseDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DeckApi {
    @GET()
    fun all(): Call<String>
}