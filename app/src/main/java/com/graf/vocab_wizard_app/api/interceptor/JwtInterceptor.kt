package com.graf.vocab_wizard_app.api.interceptor

import android.content.Context
import android.util.Log
import com.graf.vocab_wizard_app.ui.MainActivity
import okhttp3.Interceptor
import okhttp3.Response

class JwtInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Get JWT from Shared Preferences
        val sharedPref = MainActivity.activityContext().getSharedPreferences("Auth", Context.MODE_PRIVATE) ?: return chain.proceed(originalRequest)
        val accessToken = sharedPref.getString("AccessToken", "")

        // Add JWT to the Authorization header
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

        return chain.proceed(newRequest)
    }

}