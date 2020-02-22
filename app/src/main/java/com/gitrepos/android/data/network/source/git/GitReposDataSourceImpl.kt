package com.gitrepos.android.data.network.source.git

import android.util.Log
import com.gitrepos.android.data.network.api.GitApiService
import com.gitrepos.android.data.network.source.git.GitResult.Error.ErrorCodes
import com.gitrepos.android.internal.NoConnectivityException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.UnknownHostException

/**
 * Class to invoke network calls to fetch git repos data
 */
class GitReposDataSourceImpl(private val gitApiService: GitApiService) :
    GitReposDataSource {

    override suspend fun getPublicGitRepos(lastVistedIndex: Int): GitResult =
        withContext(Dispatchers.IO) {

            return@withContext try {
                val response = gitApiService.fetchGitRepos(lastVistedIndex)
                if (response.isSuccessful) {
                    GitResult.SuccessRepos(response.body().orEmpty())
                } else {
                    GitResult.Error(ErrorCodes.ServerError, response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e("GitReposDataSourceImpl", "Exception :$e")
                if (e is NoConnectivityException || e is UnknownHostException || e is ConnectException) {
                    GitResult.Error(ErrorCodes.NoConnectivityError, e?.message)
                } else {
                    GitResult.Error(ErrorCodes.ServerError, e?.message)
                }
            }
        }

    override suspend fun fetchRepoLanguages(fullName: String): GitResult =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val response = gitApiService.fetchRepoLanguages(fullName)
                if (response.isSuccessful) {
                    GitResult.SuccessLang(response.body().orEmpty())
                } else {
                    GitResult.Error(ErrorCodes.ServerError, response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e("GitReposDataSourceImpl", "Exception :$e")
                if (e is NoConnectivityException || e is UnknownHostException || e is ConnectException) {
                    GitResult.Error(ErrorCodes.NoConnectivityError, e?.message)
                } else {
                    GitResult.Error(ErrorCodes.ServerError, e?.message)
                }
            }
        }

}