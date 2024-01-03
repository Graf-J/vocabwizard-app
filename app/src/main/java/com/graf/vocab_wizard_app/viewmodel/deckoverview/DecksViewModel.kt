package com.graf.vocab_wizard_app.viewmodel.deckoverview

import DecksResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.graf.vocab_wizard_app.api.deck.DeckRepository
import com.graf.vocab_wizard_app.data.dto.response.DeckResponseDto
import com.graf.vocab_wizard_app.data.dto.response.ErrorResponseDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DecksViewModel : ViewModel() {
    private val _decksLiveData: MutableLiveData<DecksResult> = MutableLiveData(DecksResult.LOADING)
    val decksLiveData: LiveData<DecksResult> = _decksLiveData

    private val _deleteDeckLiveData: MutableLiveData<DeleteDeckResult> = MutableLiveData()
    val deleteDeckLiveData: LiveData<DeleteDeckResult> = _deleteDeckLiveData

    private val _reverseDeckLiveData: MutableLiveData<ReverseDeckResult> = MutableLiveData()
    val reverseDeckLiveData: LiveData<ReverseDeckResult> = _reverseDeckLiveData

    var reverseError = false

    private val deckRepository: DeckRepository = DeckRepository()

    fun getAllDecks() {
        _decksLiveData.postValue(DecksResult.LOADING)
        val cb: Callback<List<DeckResponseDto>> = object : Callback<List<DeckResponseDto>> {
            override fun onResponse(
                call: Call<List<DeckResponseDto>>,
                response: Response<List<DeckResponseDto>>
            ) {
                if (response.isSuccessful) {
                    val decksResult = response.body()
                    decksResult?.let {
                        _decksLiveData.postValue(DecksResult.SUCCESS(it))
                    } ?: run {
                        _decksLiveData.postValue(DecksResult.ERROR(response.code(), "Null object"))
                    }
                } else {
                    val gson = Gson()
                    try {
                        val type = object : TypeToken<ErrorResponseDto>(){}.type
                        val errorResponse: ErrorResponseDto? = gson.fromJson(response.errorBody()!!.charStream(), type)
                        _decksLiveData.postValue(
                            DecksResult.ERROR(
                                response.code(),
                                errorResponse?.message ?: "Unknown Error"
                            )
                        )
                    } catch (e: JsonParseException) {
                        _decksLiveData.postValue(
                            DecksResult.ERROR(
                                response.code(),
                                "Unknown Error"
                            )
                        )
                    }
                }
            }

            override fun onFailure(call: Call<List<DeckResponseDto>>, t: Throwable) {
                _decksLiveData.postValue(DecksResult.ERROR(-1, "API not reachable"))
            }
        }

        // Call API
        deckRepository.getAllDecks(cb)
    }

    fun deleteDeck(deckId: String) {
        _deleteDeckLiveData.postValue(DeleteDeckResult.LOADING)
        val cb: Callback<ResponseBody> = object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                        _deleteDeckLiveData.postValue(DeleteDeckResult.SUCCESS)
                } else {
                    val gson = Gson()
                    try {
                        val type = object : TypeToken<ErrorResponseDto>(){}.type
                        val errorResponse: ErrorResponseDto? = gson.fromJson(response.errorBody()!!.charStream(), type)
                        _deleteDeckLiveData.postValue(DeleteDeckResult.ERROR(response.code(), errorResponse?.message ?: "Unknown Error"))
                    } catch (e: JsonParseException) {
                        _deleteDeckLiveData.postValue(DeleteDeckResult.ERROR(response.code(), "Unknown Error"))
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _deleteDeckLiveData.postValue(DeleteDeckResult.ERROR(-1, "API not reachable"))
            }
        }

        // Call API
        deckRepository.deleteDeck(deckId, cb)
    }

    fun reverseDeck(deckId: String) {
        _reverseDeckLiveData.postValue(ReverseDeckResult.LOADING)
        val cb: Callback<ResponseBody> = object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    _reverseDeckLiveData.postValue(ReverseDeckResult.SUCCESS)
                } else {
                    reverseError = true
                    val gson = Gson()
                    try {
                        val type = object : TypeToken<ErrorResponseDto>(){}.type
                        val errorResponse: ErrorResponseDto? = gson.fromJson(response.errorBody()!!.charStream(), type)
                        _reverseDeckLiveData.postValue(ReverseDeckResult.ERROR(response.code(), errorResponse?.message ?: "Unknown Error"))
                    } catch (e: JsonParseException) {
                        _reverseDeckLiveData.postValue(ReverseDeckResult.ERROR(response.code(), "Unknown Error"))
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                reverseError = true
                _reverseDeckLiveData.postValue(ReverseDeckResult.ERROR(-1, "API not reachable"))
            }
        }

        // Call API
        deckRepository.reverseDeck(deckId, cb)
    }
}