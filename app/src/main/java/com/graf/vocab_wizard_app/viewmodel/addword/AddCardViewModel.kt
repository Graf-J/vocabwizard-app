package com.graf.vocab_wizard_app.viewmodel.addword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.graf.vocab_wizard_app.api.deck.DeckRepository
import com.graf.vocab_wizard_app.data.dto.request.CreateCardRequestDto
import com.graf.vocab_wizard_app.data.dto.response.CreateCardResponseDto
import com.graf.vocab_wizard_app.data.dto.response.ErrorArrayResponseDto
import com.graf.vocab_wizard_app.data.dto.response.ErrorResponseDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCardViewModel : ViewModel() {
    private val _addCardLiveData: MutableLiveData<AddCardResult> = MutableLiveData()
    val addCardLiveData: LiveData<AddCardResult> = _addCardLiveData

    private val deckRepository: DeckRepository = DeckRepository()

    fun addCard(payload: CreateCardRequestDto, deckId: String) {
        _addCardLiveData.postValue(AddCardResult.LOADING)
        val cb: Callback<CreateCardResponseDto> = object : Callback<CreateCardResponseDto> {
            override fun onResponse(
                call: Call<CreateCardResponseDto>,
                response: Response<CreateCardResponseDto>
            ) {
                if (response.isSuccessful) {
                    _addCardLiveData.postValue(AddCardResult.SUCCESS)
                } else {
                    val gson = Gson()
                    var responseJsonString = response.errorBody()!!.charStream().readText()
                    try {
                        val type = object : TypeToken<ErrorArrayResponseDto>(){}.type
                        val errorResponse: ErrorArrayResponseDto? = gson.fromJson(responseJsonString, type)
                        _addCardLiveData.postValue(
                            AddCardResult.ERROR(response.code(),
                                errorResponse?.message?.get(0) ?: "Unknown Error"
                            ))
                    } catch (e: JsonParseException) {
                        try {
                            val type = object : TypeToken<ErrorResponseDto>(){}.type
                            val errorResponse: ErrorResponseDto = gson.fromJson(responseJsonString, type)
                            _addCardLiveData.postValue(AddCardResult.ERROR(response.code(), errorResponse.message))
                        } catch (e: JsonParseException) {
                            _addCardLiveData.postValue(AddCardResult.ERROR(response.code(), "Unknown Error"))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CreateCardResponseDto>, t: Throwable) {
                _addCardLiveData.postValue(AddCardResult.ERROR(-1, "API not reachable"))
            }
        }

        // Call API
        deckRepository.createCard(payload, deckId, cb)
    }
}