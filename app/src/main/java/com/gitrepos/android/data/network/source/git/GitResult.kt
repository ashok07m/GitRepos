package com.gitrepos.android.data.network.source.git

import com.gitrepos.android.data.network.model.git.GitRepositories

/**
 * @author Created by kuashok on 2020-02-16
 */


sealed class GitResult {
    data class SuccessRepos(val repositories: List<GitRepositories>) : GitResult()
    data class SuccessLang(val language: String) : GitResult()
    data class Error(val errorCode: ErrorCodes, val message: String?) :
        GitResult() {
        enum class ErrorCodes {
            NoConnectivityError,
            ServerError
        }
    }
}