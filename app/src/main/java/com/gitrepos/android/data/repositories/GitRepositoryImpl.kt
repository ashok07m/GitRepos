package com.gitrepos.android.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gitrepos.android.data.database.entity.GitItem
import com.gitrepos.android.data.source.git.GitReposDataSource
import com.gitrepos.android.data.source.git.GitResult
import com.gitrepos.android.ui.home.model.Repo
import com.gitrepos.android.ui.home.model.RepoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class GitRepositoryImpl(private val gitReposDataSource: GitReposDataSource) : GitRepository {

    private val _successMutableLiveData = MutableLiveData<List<RepoItem>>()
    override val successLiveData: LiveData<List<RepoItem>> = _successMutableLiveData

    private val _errorMutableLiveData = MutableLiveData<GitResult.Error.ErrorCodes>()
    override val errorLiveData: LiveData<GitResult.Error.ErrorCodes> =
        _errorMutableLiveData


    private fun transformResult(repos: List<GitItem>): List<RepoItem> {
        var repo: Repo
        val itemList = arrayListOf<RepoItem>()
        repos.forEach {

            repo = Repo(
                name = it.name,
                fullName = it.fullName,
                description = it.description,
                homePage = it.homepage,
                language = it.language,
                starsCount = it.stars.toString(),
                forksCount = it.forksCount.toString()
            )
            itemList.add(RepoItem(repo))
        }

        return itemList
    }

    override suspend fun searchPublicGitRepos(queryString: String) {
        coroutineScope {
            launch(Dispatchers.Default) {
                val response = gitReposDataSource.searchGitRepos(queryString)
                updateResponse(response)
            }
        }
    }

    override suspend fun loadMoreGitRepos(queryString: String) {
        val response = gitReposDataSource.loadMoreGitRepos(queryString)
        updateResponse(response)
    }

    private fun updateResponse(response: GitResult) {
        when (response) {
            is GitResult.SuccessRepos -> {
                val repos = response.repositories
                val itemList = transformResult(repos)
                _successMutableLiveData.postValue(itemList)

                val error = response.error
                Log.d(TAG, "Error : ${error.errorCode}")
                if (GitResult.Error.ErrorCodes.NONE != error.errorCode) {
                    _errorMutableLiveData.postValue(error.errorCode)
                }
            }
            else -> {
                Log.d(TAG, "response :$response")
            }
        }
    }

    companion object {
        const val TAG = "GitRepositoryImpl"
    }
}