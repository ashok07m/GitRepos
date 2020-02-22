package com.gitrepos.android.data.network.api

import com.gitrepos.android.data.network.RetrofitClient
import com.gitrepos.android.data.network.interceptor.AuthTokenInterceptor
import com.gitrepos.android.data.network.model.git.GitRepositories
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Class to fetch the data from network
 */
interface GitApiService {

    @GET("/repositories?per_page=10")
    suspend fun fetchGitRepos(@Query("since") since: Int): Response<List<GitRepositories>>

    @GET("/repos/{owner}/{repo}/languages")
    suspend fun fetchRepoLanguages(@Path("owner") owner: String, @Path("repo") repo: String): Response<JsonObject>

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