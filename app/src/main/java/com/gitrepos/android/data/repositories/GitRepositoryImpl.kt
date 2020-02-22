package com.gitrepos.android.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gitrepos.android.data.network.model.git.GitRepositories
import com.gitrepos.android.data.network.source.git.GitReposDataSource
import com.gitrepos.android.data.network.source.git.GitResult
import com.gitrepos.android.ui.home.model.Repo
import com.gitrepos.android.ui.home.model.RepoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GitRepositoryImpl(private val gitReposDataSource: GitReposDataSource) : GitRepository {

    private var lastVisitedIndex = 0
    private val _successMutableLiveData = MutableLiveData<List<RepoItem>>()
    override val successLiveData: LiveData<List<RepoItem>> = _successMutableLiveData

    private val _errorMutableLiveData = MutableLiveData<GitResult.Error.ErrorCodes>()
    override val errorLiveData: LiveData<GitResult.Error.ErrorCodes> =
        _errorMutableLiveData

    override suspend fun getPublicGitRepos() {
        coroutineScope {
            launch(Dispatchers.Default) {
                when (val response = gitReposDataSource.getPublicGitRepos(lastVisitedIndex)) {
                    is GitResult.SuccessRepos -> {
                        val repos = response.repositories
                        val itemList = zipRequests(repos)
                        _successMutableLiveData.postValue(itemList)
                    }
                    is GitResult.Error -> {
                        val error = response.errorCode
                        Log.d(TAG, "Error : $error")
                        _errorMutableLiveData.postValue(error)
                    }
                    else -> {
                        Log.d(TAG, "response :$response")
                    }
                }
            }
        }
    }

    private suspend fun fetchGitLanguage(fullName: String) = withContext(Dispatchers.Default) {
        var languages = ""
        when (val response = gitReposDataSource.fetchRepoLanguages(fullName)) {
            is GitResult.SuccessLang -> {
                languages = response.language
                Log.d(TAG, "languages :$languages")
            }
            is GitResult.Error -> {
                val error = response.errorCode
                Log.d(TAG, "Error : $error")
            }
            else -> {
                Log.d(TAG, "response :$response")
            }
        }
        return@withContext languages
    }

    private suspend fun zipRequests(repos: List<GitRepositories>): List<RepoItem> {
        var repo: Repo
        var lang: String
        val itemList = arrayListOf<RepoItem>()
        repos.forEach {
            lang = fetchGitLanguage(it.languagesUrl)
            repo = Repo(it.name, it.description, lang, 0, null)
            itemList.add(RepoItem(repo))
        }

        return itemList
    }

    /**
     * Reset last visited index to initial value
     */
    fun resetLastVisitedIndex() {
        lastVisitedIndex = 0
    }

    companion object {
        const val TAG = "GitRepositoryImpl"
    }
}