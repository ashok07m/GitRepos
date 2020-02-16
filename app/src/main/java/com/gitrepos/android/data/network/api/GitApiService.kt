package com.gitrepos.android.data.network.api

import com.gitrepos.android.data.network.RetrofitClient
import com.gitrepos.android.data.network.interceptor.ApiKeyInterceptor
import com.gitrepos.android.data.network.model.GitRepositories
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Class to fetch the data from network
 */
interface GitApiService {

    // Git base url : https://api.github.com

    @GET("/repositories")
    suspend fun fetchGitRepos(@Query("since") since: Int): Response<List<GitRepositories>>


    companion object {
        operator fun invoke(
            retrofitClient: RetrofitClient,
            /*apiKeyInterceptor: ApiKeyInterceptor,*/
            baseUrl: String
        ): GitApiService {
            var okHttpClient = retrofitClient.getOkHttpClient().newBuilder().apply {
                //addInterceptor(apiKeyInterceptor)
            }.build()

            val retrofit = retrofitClient.getRetrofitClient(okHttpClient, baseUrl)
            return retrofit.create(GitApiService::class.java)
        }
    }
}