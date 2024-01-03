package com.graf.vocab_wizard_app.viewmodel.importdeck

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.graf.vocab_wizard_app.api.deck.DeckRepository
import com.graf.vocab_wizard_app.data.dto.request.ImportDeckRequestDto
import com.graf.vocab_wizard_app.data.dto.response.ErrorArrayResponseDto
import com.graf.vocab_wizard_app.data.dto.response.ErrorResponseDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ImportDeckViewModel: ViewModel() {
    private val _importDeckLiveData: MutableLiveData<ImportDeckResult> = MutableLiveData()
    val importDeckLiveData: LiveData<ImportDeckResult> = _importDeckLiveData

    private val deckRepository: DeckRepository = DeckRepository()

    fun importDeck(payload: ImportDeckRequestDto) {
        _importDeckLiveData.postValue(ImportDeckResult.LOADING)
        val cb: Callback<ResponseBody> = object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    _importDeckLiveData.postValue(ImportDeckResult.SUCCESS)
                } else {
                    val gson = Gson()
                    var responseJsonString = response.errorBody()!!.charStream().readText()
                    try {
                        val type = object : TypeToken<ErrorArrayResponseDto>(){}.type
                        val errorResponse: ErrorArrayResponseDto? = gson.fromJson(responseJsonString, type)
                        _importDeckLiveData.postValue(
                            ImportDeckResult.ERROR(response.code(),
                                errorResponse?.message?.get(0) ?: "Unknown Error"
                            ))
                    } catch (e: JsonParseException) {
                        try {
                            val type = object : TypeToken<ErrorResponseDto>(){}.type
                            val errorResponse: ErrorResponseDto = gson.fromJson(responseJsonString, type)
                            _importDeckLiveData.postValue(ImportDeckResult.ERROR(response.code(), errorResponse.message))
                        } catch (e: JsonParseException) {
                            _importDeckLiveData.postValue(ImportDeckResult.ERROR(response.code(), "Unknown Error"))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _importDeckLiveData.postValue(ImportDeckResult.ERROR(-1, "API not reachable"))
            }
        }

        // Call API
        deckRepository.importDeck(payload, cb)
    }
}