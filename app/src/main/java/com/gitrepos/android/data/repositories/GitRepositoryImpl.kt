package com.gitrepos.android.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gitrepos.android.data.network.model.GitRepositories
import com.gitrepos.android.data.network.source.GitReposDataSource
import com.gitrepos.android.internal.GitApiResponse
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

    private val _errorMutableLiveData = MutableLiveData<GitApiResponse.Error.ErrorCodes>()
    override val errorLiveData: LiveData<GitApiResponse.Error.ErrorCodes> =
        _errorMutableLiveData

    override suspend fun getPublicGitRepos() {
        coroutineScope {
            launch(Dispatchers.Default) {
                when (val response = gitReposDataSource.getPublicGitRepos(lastVisitedIndex)) {
                    is GitApiResponse.Success -> {
                        var repo: Repo
                        val itemList = arrayListOf<RepoItem>()
                        val repos = response.repositories
                        repos.forEach {
                            repo = Repo(it.name, it.description, null, 0, null)
                            itemList.add(RepoItem(repo))
                        }
                        _successMutableLiveData.postValue(itemList)
                    }
                    is GitApiResponse.Error -> {
                        val error = response.errorCode
                        Log.d(TAG, "Error : $error")
                        _errorMutableLiveData.postValue(error)
                    }
                }
            }
        }
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