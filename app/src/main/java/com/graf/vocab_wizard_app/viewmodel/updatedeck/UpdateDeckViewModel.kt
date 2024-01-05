package com.graf.vocab_wizard_app.viewmodel.updatedeck

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.graf.vocab_wizard_app.api.deck.DeckRepository
import com.graf.vocab_wizard_app.data.dto.request.UpdateDeckRequestDto
import com.graf.vocab_wizard_app.data.dto.response.ErrorArrayResponseDto
import com.graf.vocab_wizard_app.data.dto.response.ErrorResponseDto
import com.graf.vocab_wizard_app.data.dto.response.UpdateDeckResponseDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateDeckViewModel : ViewModel() {
    private val _updateDeckLiveData: MutableLiveData<UpdateDeckResult> = MutableLiveData()
    val updateDeckLiveData: LiveData<UpdateDeckResult> = _updateDeckLiveData

    private val deckRepository: DeckRepository = DeckRepository()

    fun updateDeck(payload: UpdateDeckRequestDto, deckId: String) {
        _updateDeckLiveData.postValue(UpdateDeckResult.LOADING)
        val cb: Callback<UpdateDeckResponseDto> = object : Callback<UpdateDeckResponseDto> {
            override fun onResponse(
                call: Call<UpdateDeckResponseDto>,
                response: Response<UpdateDeckResponseDto>
            ) {
                if (response.isSuccessful) {
                    val createDeckResult = response.body()
                    createDeckResult?.let {
                        _updateDeckLiveData.postValue(UpdateDeckResult.SUCCESS(it))
                    } ?: run {
                        _updateDeckLiveData.postValue(UpdateDeckResult.ERROR(response.code(), "Null object"))
                    }
                } else {
                    val gson = Gson()
                    var responseJsonString = response.errorBody()!!.charStream().readText()
                    try {
                        val type = object : TypeToken<ErrorArrayResponseDto>(){}.type
                        val errorResponse: ErrorArrayResponseDto? = gson.fromJson(responseJsonString, type)
                        _updateDeckLiveData.postValue(
                            UpdateDeckResult.ERROR(response.code(),
                                errorResponse?.message?.get(0) ?: "Unknown Error"
                            ))
                    } catch (e: JsonParseException) {
                        try {
                            val type = object : TypeToken<ErrorResponseDto>(){}.type
                            val errorResponse: ErrorResponseDto = gson.fromJson(responseJsonString, type)
                            _updateDeckLiveData.postValue(UpdateDeckResult.ERROR(response.code(), errorResponse.message))
                        } catch (e: JsonParseException) {
                            _updateDeckLiveData.postValue(UpdateDeckResult.ERROR(response.code(), "Unknown Error"))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<UpdateDeckResponseDto>, t: Throwable) {
                _updateDeckLiveData.postValue(UpdateDeckResult.ERROR(-1, "API not reachable"))
            }
        }

        // Call API
        deckRepository.updateDeck(payload, deckId, cb)
    }
}