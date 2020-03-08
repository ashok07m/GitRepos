package com.gitrepos.android.data.source.git

import android.util.Log
import com.gitrepos.android.data.network.api.GitApiService
import com.gitrepos.android.data.repositories.GitCacheDbRepository
import com.gitrepos.android.data.source.git.GitResult.Error.ErrorCodes
import com.gitrepos.android.internal.NoConnectivityException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.UnknownHostException

/**
 * Class to invoke network calls to fetch git repos data
 */
class GitReposDataSourceImpl(
    private val gitApiService: GitApiService,
    private val gitCacheDbRepository: GitCacheDbRepository
) : GitReposDataSource {

    private var lastRequestedPage = 1
    private var isRequestInProgress = false

    override suspend fun searchGitRepos(query: String): GitResult = withContext(Dispatchers.IO) {
        //if (isRequestInProgress) return
        var searchErrorResult: GitResult.Error = GitResult.Error(ErrorCodes.NONE, "No error")
        try {
            lastRequestedPage = 1
            isRequestInProgress = true
            val response =
                gitApiService.searchRepos(query, lastRequestedPage, NETWORK_PAGE_SIZE)

            if (response.isSuccessful) {
                val body = response.body()

                if (body?.gitItems != null) {
                    val items = body.gitItems
                    if (items.isNotEmpty()) {
                        gitCacheDbRepository.saveGitRepos(items)
                        lastRequestedPage++
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("GitReposDataSourceImpl", "Exception :$e")
            searchErrorResult =
                if (e is NoConnectivityException || e is UnknownHostException || e is ConnectException) {
                    GitResult.Error(ErrorCodes.NoConnectivityError, e.message)
                } else {
                    GitResult.Error(ErrorCodes.ServerError, e.message)
                }
        }

        // query database for the cached data
        val items = gitCacheDbRepository.fetchCachedRepos(query)
        val result = GitResult.SuccessRepos(items, searchErrorResult)
        isRequestInProgress = false

        return@withContext result
    }


    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }
}

