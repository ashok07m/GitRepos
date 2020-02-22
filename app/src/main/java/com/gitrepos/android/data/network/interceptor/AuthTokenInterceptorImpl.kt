package com.gitrepos.android.data.network.interceptor

import android.util.Base64
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthTokenInterceptorImpl(private val authToken: String) :
    AuthTokenInterceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val basicAuth2 = "Basic " + Base64.encodeToString(
            authToken.toByteArray(),
            Base64.NO_WRAP
        )

        val updatedRequest = chain.request().newBuilder()
            .addHeader("Authorization", basicAuth2)
            .build()

        Log.d("AuthTokenInterceptor", "updatedRequest : $updatedRequest")
        return chain.proceed(updatedRequest)
    }

}