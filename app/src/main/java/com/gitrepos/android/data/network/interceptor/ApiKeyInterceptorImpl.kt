package com.gitrepos.android.data.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptorImpl(private val api_key: String) :
    ApiKeyInterceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val updatedUrl = chain.request().url.newBuilder().addQueryParameter("key", api_key).build()
        val updatedRequest = chain.request().newBuilder().url(updatedUrl).build()
        return chain.proceed(updatedRequest)
    }

}