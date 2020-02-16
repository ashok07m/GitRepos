package com.gitrepos.android.data.network

import com.gitrepos.android.data.network.interceptor.ConnectivityInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Class to provide retrofit instance
 */

class RetrofitClient(private val connectivityInterceptor: ConnectivityInterceptor) {

    fun getOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder().apply {
            addInterceptor(httpLoggingInterceptor)
            addInterceptor(connectivityInterceptor)
        }.build()
    }

    fun getRetrofitClient(okHttpClient: OkHttpClient, baseUrl: String): Retrofit {
        return Retrofit.Builder().apply {
            client(okHttpClient)
            baseUrl(baseUrl)
            addConverterFactory(GsonConverterFactory.create())
        }.build()
    }
}