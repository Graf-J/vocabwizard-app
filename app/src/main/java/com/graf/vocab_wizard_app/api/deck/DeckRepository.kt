package com.graf.vocab_wizard_app.api.deck

import com.graf.vocab_wizard_app.api.interceptor.JwtInterceptor
import com.graf.vocab_wizard_app.config.AppConfig
import com.graf.vocab_wizard_app.data.dto.request.ConfidenceRequestDto
import com.graf.vocab_wizard_app.data.dto.request.CreateCardRequestDto
import com.graf.vocab_wizard_app.data.dto.request.CreateDeckRequestDto
import com.graf.vocab_wizard_app.data.dto.request.LoginRequestDto
import com.graf.vocab_wizard_app.data.dto.response.AuthResponseDto
import com.graf.vocab_wizard_app.data.dto.response.CardResponseDto
import com.graf.vocab_wizard_app.data.dto.response.CreateCardResponseDto
import com.graf.vocab_wizard_app.data.dto.response.CreateDeckResponseDto
import com.graf.vocab_wizard_app.data.dto.response.DeckResponseDto
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
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

    fun getAllDecks(callback: Callback<List<DeckResponseDto>>) {
        deckApi.getAllDecks().enqueue(callback)
    }

    fun deleteDeck(deckId: String, callback: Callback<ResponseBody>) {
        deckApi.deleteDeck(deckId).enqueue(callback)
    }

    fun getLearnCards(deckId: String, callback: Callback<List<CardResponseDto>>) {
        deckApi.getLearnCards(deckId).enqueue(callback)
    }

    fun updateCardConfidence(payload: ConfidenceRequestDto, deckId: String, cardId: String, callback: Callback<ResponseBody>) {
        deckApi.updateCardConfidence(payload, deckId, cardId).enqueue(callback)
    }

    fun createDeck(payload: CreateDeckRequestDto, callback: Callback<CreateDeckResponseDto>) {
        deckApi.createDeck(payload).enqueue(callback)
    }

    fun createCard(payload: CreateCardRequestDto, deckId: String, callback: Callback<CreateCardResponseDto>) {
        deckApi.createCard(payload, deckId).enqueue(callback)
    }

    fun deleteCard(deckId: String, cardId: String, callback: Callback<ResponseBody>) {
        deckApi.deleteCard(deckId, cardId).enqueue(callback)
    }
}