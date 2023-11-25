package com.graf.vocab_wizard_app.viewmodel.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.graf.vocab_wizard_app.api.auth.AuthRepository
import com.graf.vocab_wizard_app.data.dto.request.LoginRequestDto
import com.graf.vocab_wizard_app.data.dto.response.AuthResponseDto
import com.graf.vocab_wizard_app.data.dto.response.ErrorResponseDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.Exception

class LoginViewModel : ViewModel() {
    private val _loginLiveData: MutableLiveData<LoginResult> = MutableLiveData(LoginResult.LOADING)
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
                        _loginLiveData.postValue(LoginResult.SUCCESS(it.accessToken))
                    } ?: run {
                        _loginLiveData.postValue(LoginResult.ERROR(response.code(), "Null object"))
                    }
                } else {
                    // _loginLiveData.postValue(LoginResult.ERROR(response.code(), response.message()))
                    // Parse JSON if Login Fails
                    val gson = Gson()
                    val type = object : TypeToken<ErrorResponseDto>(){}.type
                    val errorResponse: ErrorResponseDto? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    _loginLiveData.postValue((LoginResult.ERROR(response.code(), errorResponse?.message ?: "Unknown Error")))
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