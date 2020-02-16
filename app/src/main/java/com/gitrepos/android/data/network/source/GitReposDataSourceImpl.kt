package com.gitrepos.android.data.network.source

import android.util.Log
import com.gitrepos.android.data.network.api.GitApiService
import com.gitrepos.android.internal.GitApiResponse
import com.gitrepos.android.internal.GitApiResponse.Error.*
import com.gitrepos.android.internal.NoConnectivityException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.net.ConnectException
import java.net.UnknownHostException

/**
 * Class to invoke network calls to fetch git repos data
 */
class GitReposDataSourceImpl(private val gitApiService: GitApiService) :
    GitReposDataSource {

    override suspend fun getPublicGitRepos(lastVistedIndex: Int): GitApiResponse =
        withContext(Dispatchers.IO) {

            return@withContext try {
                val response = gitApiService.fetchGitRepos(lastVistedIndex)
                if (response.isSuccessful) {
                    GitApiResponse.Success(response.body().orEmpty())
                } else {
                    GitApiResponse.Error(ErrorCodes.ServerError, response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e("GitReposDataSourceImpl", "Exception :$e")
                if (e is NoConnectivityException || e is UnknownHostException || e is ConnectException) {
                    GitApiResponse.Error(ErrorCodes.NoConnectivityError, e?.message)
                } else {
                    GitApiResponse.Error(ErrorCodes.ServerError, e?.message)
                }
            }
        }

}