package com.gitrepos.android.data.network.api

import com.gitrepos.android.data.network.RetrofitClient
import com.gitrepos.android.data.network.model.git.GitRepositories
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Class to fetch the data from network
 */
interface GitApiService {

    // Git base url : https://api.github.com

    @GET("/repositories")
    suspend fun fetchGitRepos(@Query("since") since: Int): Response<List<GitRepositories>>

    @GET("/repos/{fullName}/languages")
    suspend fun fetchRepoLanguages(@Path("fullName") fullName: String): Response<String>


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