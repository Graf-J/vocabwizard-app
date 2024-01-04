package com.graf.vocab_wizard_app.viewmodel.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.graf.vocab_wizard_app.api.deck.DeckRepository
import com.graf.vocab_wizard_app.data.dto.response.ErrorResponseDto
import com.graf.vocab_wizard_app.data.dto.response.StatsResponseDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StatsViewModel : ViewModel() {
    private val _statsLiveData: MutableLiveData<StatsResult> = MutableLiveData(StatsResult.LOADING)
    val statsLiveData: LiveData<StatsResult> = _statsLiveData

    private val deckRepository: DeckRepository = DeckRepository()

    fun getStats(deckId: String) {
        _statsLiveData.postValue(StatsResult.LOADING)
        val cb: Callback<List<StatsResponseDto>> = object : Callback<List<StatsResponseDto>> {
            override fun onResponse(
                call: Call<List<StatsResponseDto>>,
                response: Response<List<StatsResponseDto>>
            ) {
                if (response.isSuccessful) {
                    val statsResult = response.body()
                    statsResult?.let {
                        _statsLiveData.postValue(StatsResult.SUCCESS(it))
                    } ?: run {
                        _statsLiveData.postValue(StatsResult.ERROR(response.code(), "Null object"))
                    }
                } else {
                    val gson = Gson()
                    try {
                        val type = object : TypeToken<ErrorResponseDto>(){}.type
                        val errorResponse: ErrorResponseDto? = gson.fromJson(response.errorBody()!!.charStream(), type)
                        _statsLiveData.postValue(StatsResult.ERROR(response.code(), errorResponse?.message ?: "Unknown Error"))
                    } catch (e: JsonParseException) {
                        _statsLiveData.postValue(StatsResult.ERROR(response.code(), "Unknown Error"))
                    }
                }
            }

            override fun onFailure(call: Call<List<StatsResponseDto>>, t: Throwable) {
                _statsLiveData.postValue(StatsResult.ERROR(-1, "API not reachable"))
            }
        }

        // Call API
        deckRepository.getStats(deckId, cb)
    }
}