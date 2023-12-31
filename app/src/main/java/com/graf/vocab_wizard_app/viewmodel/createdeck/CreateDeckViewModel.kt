package com.graf.vocab_wizard_app.viewmodel.createdeck

import CreateDeckResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.graf.vocab_wizard_app.api.deck.DeckRepository
import com.graf.vocab_wizard_app.data.dto.request.CreateDeckRequestDto
import com.graf.vocab_wizard_app.data.dto.response.CreateDeckResponseDto
import com.graf.vocab_wizard_app.data.dto.response.ErrorArrayResponseDto
import com.graf.vocab_wizard_app.data.dto.response.ErrorResponseDto
import com.graf.vocab_wizard_app.ui.adapter.DropdownAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateDeckViewModel : ViewModel() {
    private val _createDeckLiveData: MutableLiveData<CreateDeckResult> = MutableLiveData(CreateDeckResult.LOADING)
    val createDeckLiveData: LiveData<CreateDeckResult> = _createDeckLiveData

    lateinit var fromListAdapter: DropdownAdapter
    lateinit var toListAdapter: DropdownAdapter

    private val deckRepository: DeckRepository = DeckRepository()

    fun createDeck(payload: CreateDeckRequestDto) {
        _createDeckLiveData.postValue(CreateDeckResult.LOADING)
        val cb: Callback<CreateDeckResponseDto> = object : Callback<CreateDeckResponseDto> {
            override fun onResponse(
                call: Call<CreateDeckResponseDto>,
                response: Response<CreateDeckResponseDto>
            ) {
                if (response.isSuccessful) {
                    val createDeckResult = response.body()
                    createDeckResult?.let {
                        _createDeckLiveData.postValue(CreateDeckResult.SUCCESS(it))
                    } ?: run {
                        _createDeckLiveData.postValue(CreateDeckResult.ERROR(response.code(), "Null object"))
                    }
                } else {
                    val gson = Gson()
                    var responseJsonString = response.errorBody()!!.charStream().readText()
                    try {
                        val type = object : TypeToken<ErrorArrayResponseDto>(){}.type
                        val errorResponse: ErrorArrayResponseDto? = gson.fromJson(responseJsonString, type)
                        _createDeckLiveData.postValue(
                            CreateDeckResult.ERROR(response.code(),
                            errorResponse?.message?.get(0) ?: "Unknown Error"
                        ))
                    } catch (e: JsonParseException) {
                        try {
                            val type = object : TypeToken<ErrorResponseDto>(){}.type
                            val errorResponse: ErrorResponseDto = gson.fromJson(responseJsonString, type)
                            _createDeckLiveData.postValue(CreateDeckResult.ERROR(response.code(), errorResponse.message))
                        } catch (e: JsonParseException) {
                            _createDeckLiveData.postValue(CreateDeckResult.ERROR(response.code(), "Unknown Error"))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CreateDeckResponseDto>, t: Throwable) {
                _createDeckLiveData.postValue(CreateDeckResult.ERROR(-1, "API not reachable"))
            }
        }

        // Call API
        deckRepository.createDeck(payload, cb)
    }
}