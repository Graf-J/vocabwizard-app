package com.graf.vocab_wizard_app.viewmodel.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.graf.vocab_wizard_app.api.auth.AuthRepository
import com.graf.vocab_wizard_app.data.dto.request.LoginRequestDto
import com.graf.vocab_wizard_app.data.dto.response.AuthResponseDto
import com.graf.vocab_wizard_app.data.dto.response.ErrorResponseDto
import com.graf.vocab_wizard_app.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    private val _loginLiveData: MutableLiveData<LoginResult> = MutableLiveData()
    val loginLiveData: LiveData<LoginResult> = _loginLiveData

    private val authRepository: AuthRepository = AuthRepository()

    fun login(payload: LoginRequestDto) {
        _loginLiveData.postValue(LoginResult.LOADING)
        val cb: Callback<AuthResponseDto> = object : Callback<AuthResponseDto> {
            override fun onResponse(
                call: Call<AuthResponseDto>,
                response: Response<AuthResponseDto>
            ) {
                if (response.isSuccessful) {
                    val loginResult = response.body()
                    loginResult?.let {
                        // Save Token to Shared Preferences
                        val sharedPref = MainActivity.activityContext().getSharedPreferences("Auth", Context.MODE_PRIVATE)
                        with (sharedPref.edit()) {
                            putString("AccessToken", it.accessToken)
                            apply()
                        }

                        _loginLiveData.postValue(LoginResult.SUCCESS(it.accessToken))
                    } ?: run {
                        _loginLiveData.postValue(LoginResult.ERROR(response.code(), "Null object"))
                    }
                } else {
                    // Parse JSON if Login Fails
                    val gson = Gson()
                    try {
                        val type = object : TypeToken<ErrorResponseDto>(){}.type
                        val errorResponse: ErrorResponseDto? = gson.fromJson(response.errorBody()!!.charStream(), type)
                        _loginLiveData.postValue((LoginResult.ERROR(response.code(), errorResponse?.message ?: "Unknown Error")))
                    } catch (e: JsonParseException) {
                        _loginLiveData.postValue(LoginResult.ERROR(response.code(), "Unknown Error"))
                    }
                }
            }

            override fun onFailure(call: Call<AuthResponseDto>, t: Throwable) {
                _loginLiveData.postValue(LoginResult.ERROR(-1, "API not reachable"))
            }
        }

        // Call API
        authRepository.login(payload, cb)
    }
}