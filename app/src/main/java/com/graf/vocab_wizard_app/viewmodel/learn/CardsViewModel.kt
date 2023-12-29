import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.graf.vocab_wizard_app.api.deck.DeckRepository
import com.graf.vocab_wizard_app.data.dto.request.ConfidenceRequestDto
import com.graf.vocab_wizard_app.data.dto.response.CardResponseDto
import com.graf.vocab_wizard_app.data.dto.response.ErrorResponseDto
import com.graf.vocab_wizard_app.viewmodel.learn.ConfidenceResult
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CardsViewModel : ViewModel() {
    private val _cardsLiveData: MutableLiveData<CardsResult> = MutableLiveData(CardsResult.LOADING)
    val cardsLiveData: LiveData<CardsResult> = _cardsLiveData

    private val _confidenceLiveData: MutableLiveData<ConfidenceResult> = MutableLiveData(ConfidenceResult.INITIAL)
    val confidenceLiveData: LiveData<ConfidenceResult> = _confidenceLiveData

    private val deckRepository: DeckRepository = DeckRepository()

    fun getLearnCards(deckId: String) {
        _cardsLiveData.postValue(CardsResult.LOADING)
        val cb: Callback<List<CardResponseDto>> = object : Callback<List<CardResponseDto>> {
            override fun onResponse(
                call: Call<List<CardResponseDto>>,
                response: Response<List<CardResponseDto>>
            ) {
                if (response.isSuccessful) {
                    val cardsResult = response.body()
                    cardsResult?.let {
                        _cardsLiveData.postValue(CardsResult.SUCCESS(it))
                    } ?: run {
                        _cardsLiveData.postValue(CardsResult.ERROR(response.code(), "Null object"))
                    }
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<ErrorResponseDto>(){}.type
                    val errorResponse: ErrorResponseDto? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    _cardsLiveData.postValue((CardsResult.ERROR(response.code(), errorResponse?.message ?: "Unknown Error")))
                }
            }

            override fun onFailure(call: Call<List<CardResponseDto>>, t: Throwable) {
                _cardsLiveData.postValue(CardsResult.ERROR(-1, "API not reachable"))
            }
        }

        // Call API
        deckRepository.getLearnCards(deckId, cb)
    }

    fun updateCardConfidence(payload: ConfidenceRequestDto, deckId: String, cardId: String) {
        _confidenceLiveData.postValue(ConfidenceResult.LOADING)
        val cb: Callback<ResponseBody> = object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                        _confidenceLiveData.postValue(ConfidenceResult.SUCCESS)
                } else {
                    val gson = Gson()
                    val type = object : TypeToken<ErrorResponseDto>(){}.type
                    val errorResponse: ErrorResponseDto? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    _confidenceLiveData.postValue((ConfidenceResult.ERROR(response.code(), errorResponse?.message ?: "Unknown Error")))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _confidenceLiveData.postValue(ConfidenceResult.ERROR(-1, "API not reachable"))
            }
        }

        // Call API
        deckRepository.updateCardConfidence(payload, deckId, cardId, cb)
    }
}