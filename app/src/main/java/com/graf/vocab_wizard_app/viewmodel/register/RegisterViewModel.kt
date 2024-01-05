package com.graf.vocab_wizard_app.viewmodel.register

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.graf.vocab_wizard_app.api.auth.AuthRepository
import com.graf.vocab_wizard_app.data.dto.request.RegisterRequestDto
import com.graf.vocab_wizard_app.data.dto.response.AuthResponseDto
import com.graf.vocab_wizard_app.data.dto.response.ErrorArrayResponseDto
import com.graf.vocab_wizard_app.data.dto.response.ErrorResponseDto
import com.graf.vocab_wizard_app.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    private val _registerLiveData: MutableLiveData<RegisterResult> = MutableLiveData()
    val registerLiveData: LiveData<RegisterResult> = _registerLiveData

    private val authRepository: AuthRepository = AuthRepository()

    fun register(payload: RegisterRequestDto) {
        _registerLiveData.postValue(RegisterResult.LOADING)
        val cb: Callback<AuthResponseDto> = object : Callback<AuthResponseDto> {
            override fun onResponse(
                call: Call<AuthResponseDto>,
                response: Response<AuthResponseDto>
            ) {
                if (response.isSuccessful) {
                    val registerResult = response.body()
                    registerResult?.let {
                        // Save Token to Shared Preferences
                        val sharedPref = MainActivity.activityContext().getSharedPreferences("Auth", Context.MODE_PRIVATE)
                        with (sharedPref.edit()) {
                            putString("AccessToken", it.accessToken)
                            apply()
                        }

                        _registerLiveData.postValue(RegisterResult.SUCCESS(it.accessToken))
                    } ?: run {
                        _registerLiveData.postValue(RegisterResult.ERROR(response.code(), "Null object"))
                    }
                } else {
                    // Parse JSON if Register Fails
                    val gson = Gson()
                    var responseJsonString = response.errorBody()!!.charStream().readText()
                    try {
                        val type = object : TypeToken<ErrorArrayResponseDto>(){}.type
                        val errorResponse: ErrorArrayResponseDto? = gson.fromJson(responseJsonString, type)
                        _registerLiveData.postValue(RegisterResult.ERROR(response.code(),
                            errorResponse?.message?.get(0) ?: "Unknown Error"
                        ))
                    } catch (e: JsonParseException) {
                        try {
                            val type = object : TypeToken<ErrorResponseDto>(){}.type
                            val errorResponse: ErrorResponseDto = gson.fromJson(responseJsonString, type)
                            _registerLiveData.postValue(RegisterResult.ERROR(response.code(), errorResponse.message))
                        } catch (e: JsonParseException) {
                            _registerLiveData.postValue(RegisterResult.ERROR(response.code(), "Unknown Error"))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<AuthResponseDto>, t: Throwable) {
                _registerLiveData.postValue(RegisterResult.ERROR(-1, "API not reachable"))
            }
        }

        // Call API
        authRepository.register(payload, cb)
    }
}