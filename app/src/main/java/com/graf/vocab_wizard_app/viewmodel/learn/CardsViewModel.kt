import android.animation.ValueAnimator
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.graf.vocab_wizard_app.api.deck.DeckRepository
import com.graf.vocab_wizard_app.data.dto.request.ConfidenceRequestDto
import com.graf.vocab_wizard_app.data.dto.response.CardResponseDto
import com.graf.vocab_wizard_app.data.dto.response.ErrorResponseDto
import com.graf.vocab_wizard_app.viewmodel.deckoverview.DeleteDeckResult
import com.graf.vocab_wizard_app.viewmodel.learn.ConfidenceResult
import com.graf.vocab_wizard_app.viewmodel.learn.DeleteCardResult
import com.graf.vocab_wizard_app.viewmodel.register.RegisterResult
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CardsViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    // Put Variables here to prevent Data loss during orientation change
    var isFront: Boolean = true
    var currentRotationAngle: Float = 0f
    var cards: MutableList<CardResponseDto> = mutableListOf()
    var audioLink: String? = null
    val mediaPlayer = MediaPlayer()

    private val _cardsLiveData: MutableLiveData<CardsResult> = MutableLiveData(CardsResult.LOADING)
    val cardsLiveData: LiveData<CardsResult> = _cardsLiveData

    private val _confidenceLiveData: MutableLiveData<ConfidenceResult> = MutableLiveData()
    val confidenceLiveData: LiveData<ConfidenceResult> = _confidenceLiveData

    private val _deleteCardLiveData: MutableLiveData<DeleteCardResult> = MutableLiveData(DeleteCardResult.LOADING)
    val deleteCardLiveData: LiveData<DeleteCardResult> = _deleteCardLiveData

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
                    try {
                        val type = object : TypeToken<ErrorResponseDto>(){}.type
                        val errorResponse: ErrorResponseDto? = gson.fromJson(response.errorBody()!!.charStream(), type)
                        _cardsLiveData.postValue(CardsResult.ERROR(response.code(), errorResponse?.message ?: "Unknown Error"))
                    } catch (e: JsonParseException) {
                        _cardsLiveData.postValue(CardsResult.ERROR(response.code(), "Unknown Error"))
                    }
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
                    try {
                        val type = object : TypeToken<ErrorResponseDto>(){}.type
                        val errorResponse: ErrorResponseDto? = gson.fromJson(response.errorBody()!!.charStream(), type)
                        _confidenceLiveData.postValue(ConfidenceResult.ERROR(response.code(), errorResponse?.message ?: "Unknown Error"))
                    } catch (e: JsonParseException) {
                        _confidenceLiveData.postValue(ConfidenceResult.ERROR(response.code(), "Unknown Error"))
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _confidenceLiveData.postValue(ConfidenceResult.ERROR(-1, "API not reachable"))
            }
        }

        // Call API
        deckRepository.updateCardConfidence(payload, deckId, cardId, cb)
    }

    fun deleteCard(deckId: String, cardId: String) {
        _deleteCardLiveData.postValue(DeleteCardResult.LOADING)
        val cb: Callback<ResponseBody> = object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    _deleteCardLiveData.postValue(DeleteCardResult.SUCCESS)
                } else {
                    val gson = Gson()
                    try {
                        val type = object : TypeToken<ErrorResponseDto>(){}.type
                        val errorResponse: ErrorResponseDto? = gson.fromJson(response.errorBody()!!.charStream(), type)
                        _deleteCardLiveData.postValue(DeleteCardResult.ERROR(response.code(), errorResponse?.message ?: "Unknown Error"))
                    } catch (e: JsonParseException) {
                        _deleteCardLiveData.postValue(DeleteCardResult.ERROR(response.code(), "Unknown Error"))
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _deleteCardLiveData.postValue(DeleteCardResult.ERROR(-1, "API not reachable"))
            }
        }

        // Call API
        deckRepository.deleteCard(deckId, cardId, cb)
    }
}