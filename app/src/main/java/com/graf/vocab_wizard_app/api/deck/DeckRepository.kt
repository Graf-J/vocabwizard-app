package com.graf.vocab_wizard_app.api.deck

import com.graf.vocab_wizard_app.api.interceptor.JwtInterceptor
import com.graf.vocab_wizard_app.config.AppConfig
import com.graf.vocab_wizard_app.data.dto.request.LoginRequestDto
import com.graf.vocab_wizard_app.data.dto.response.AuthResponseDto
import com.graf.vocab_wizard_app.data.dto.response.DeckResponseDto
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.Callback
import retrofit2.converter.gson.GsonConverterFactory

class DeckRepository {
    private val deckApi: DeckApi

    init {
        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val jwtInterceptor = JwtInterceptor()

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(jwtInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(AppConfig.SERVER_URL + "/decks/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        deckApi = retrofit.create(DeckApi::class.java)
    }

    fun all(callback: Callback<List<DeckResponseDto>>) {
        deckApi.all().enqueue((callback))
    }
}