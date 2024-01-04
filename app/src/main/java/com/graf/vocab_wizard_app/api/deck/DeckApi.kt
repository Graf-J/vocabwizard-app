package com.graf.vocab_wizard_app.api.deck

import com.graf.vocab_wizard_app.data.dto.request.ConfidenceRequestDto
import com.graf.vocab_wizard_app.data.dto.request.CreateCardRequestDto
import com.graf.vocab_wizard_app.data.dto.request.CreateDeckRequestDto
import com.graf.vocab_wizard_app.data.dto.request.ImportDeckRequestDto
import com.graf.vocab_wizard_app.data.dto.request.UpdateDeckRequestDto
import com.graf.vocab_wizard_app.data.dto.response.CardResponseDto
import com.graf.vocab_wizard_app.data.dto.response.CreateCardResponseDto
import com.graf.vocab_wizard_app.data.dto.response.CreateDeckResponseDto
import com.graf.vocab_wizard_app.data.dto.response.DeckResponseDto
import com.graf.vocab_wizard_app.data.dto.response.StatsResponseDto
import com.graf.vocab_wizard_app.data.dto.response.UpdateDeckResponseDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DeckApi {
    @GET("/decks")
    fun getAllDecks(): Call<List<DeckResponseDto>>

    @POST("/decks")
    fun createDeck(@Body payload: CreateDeckRequestDto): Call<CreateDeckResponseDto>

    @POST("import")
    fun importDeck(@Body paylaod: ImportDeckRequestDto): Call<ResponseBody>

    @PUT("{DeckId}")
    fun updateDeck(
        @Body payload: UpdateDeckRequestDto,
        @Path("DeckId") deckId: String
    ): Call<UpdateDeckResponseDto>

    @PATCH("{DeckId}/swap")
    fun reverseDeck(@Path("DeckId") deckId: String): Call<ResponseBody>

    @DELETE("{DeckId}")
    fun deleteDeck(@Path("DeckId") deckId: String): Call<ResponseBody>

    @GET("{DeckId}/cards/learn")
    fun getLearnCards(@Path("DeckId") deckId: String): Call<List<CardResponseDto>>

    @GET("{DeckId}/stats")
    fun getStats(@Path("DeckId") deckId: String): Call<List<StatsResponseDto>>

    @POST("{DeckId}/cards")
    fun createCard(
        @Body payload: CreateCardRequestDto,
        @Path("DeckId") deckId: String
    ): Call<CreateCardResponseDto>

    @PATCH("{DeckId}/cards/{CardId}/confidence")
    fun updateCardConfidence(
        @Body payload: ConfidenceRequestDto,
        @Path("DeckId") deckId: String,
        @Path("CardId") cardId: String
    ): Call<ResponseBody>

    @DELETE("{DeckId}/cards/{CardId}")
    fun deleteCard(
        @Path("DeckId") deckId: String,
        @Path("CardId") cardId: String
    ): Call<ResponseBody>
}