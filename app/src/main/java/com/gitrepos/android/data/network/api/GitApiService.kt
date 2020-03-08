package com.gitrepos.android.data.network.api

import com.gitrepos.android.data.network.RetrofitClient
import com.gitrepos.android.data.network.interceptor.AuthTokenInterceptor
import com.gitrepos.android.data.network.model.git.GitSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Class to fetch the data from network
 */
interface GitApiService {

    /**
     * Get repos ordered by stars.
     */
    @GET("search/repositories?sort=stars")
    suspend fun searchRepos(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): Response<GitSearchResponse>

    companion object {
        operator fun invoke(
            retrofitClient: RetrofitClient,
            authTokenInterceptor: AuthTokenInterceptor,
            baseUrl: String
        ): GitApiService {
            var okHttpClient = retrofitClient.getOkHttpClient().newBuilder().apply {
                addInterceptor(authTokenInterceptor)
            }.build()

            val retrofit = retrofitClient.getRetrofitClient(okHttpClient, baseUrl)
            return retrofit.create(GitApiService::class.java)
        }
    }
}