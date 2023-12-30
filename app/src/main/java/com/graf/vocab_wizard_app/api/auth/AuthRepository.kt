package com.graf.vocab_wizard_app.api.auth

import com.graf.vocab_wizard_app.api.interceptor.JwtInterceptor
import com.graf.vocab_wizard_app.config.AppConfig
import com.graf.vocab_wizard_app.data.dto.request.LoginRequestDto
import com.graf.vocab_wizard_app.data.dto.request.RegisterRequestDto
import com.graf.vocab_wizard_app.data.dto.response.AuthResponseDto
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.Callback
import retrofit2.converter.gson.GsonConverterFactory

class AuthRepository {
    private val authApi: AuthApi

    init {
        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(AppConfig.SERVER_URL + "/auth/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        authApi = retrofit.create(AuthApi::class.java)
    }

    fun login(payload: LoginRequestDto, callback: Callback<AuthResponseDto>) {
        authApi.login(payload).enqueue(callback)
    }

    fun register(payload: RegisterRequestDto, callback: Callback<AuthResponseDto>) {
        authApi.register(payload).enqueue(callback)
    }
}