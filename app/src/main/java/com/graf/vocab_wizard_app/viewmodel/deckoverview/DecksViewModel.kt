import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.graf.vocab_wizard_app.api.deck.DeckRepository
import com.graf.vocab_wizard_app.data.dto.response.DeckResponseDto
import com.graf.vocab_wizard_app.data.dto.response.ErrorResponseDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DecksViewModel : ViewModel() {
    private val _decksLiveData: MutableLiveData<DecksResult> = MutableLiveData(DecksResult.LOADING)
    val decksLiveData: LiveData<DecksResult> = _decksLiveData

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
                    // Parse JSON if Login Fails
                    val gson = Gson()
                    val type = object : TypeToken<ErrorResponseDto>(){}.type
                    val errorResponse: ErrorResponseDto? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    _decksLiveData.postValue((DecksResult.ERROR(response.code(), errorResponse?.message ?: "Unknown Error")))
                }
            }

            override fun onFailure(call: Call<List<DeckResponseDto>>, t: Throwable) {
                _decksLiveData.postValue(DecksResult.ERROR(-1, "API not reachable"))
            }
        }

        // Call API
        deckRepository.all(cb)
    }
}