package com.gitrepos.android.internal

import com.gitrepos.android.data.network.model.GitRepositories

/**
 * @author Created by kuashok on 2020-02-16
 */


sealed class GitApiResponse {
    data class Success(val repositories: List<GitRepositories>) : GitApiResponse()
    data class Error(val errorCode: ErrorCodes, val message: String?) :
        GitApiResponse() {
        enum class ErrorCodes {
            NoConnectivityError,
            ServerError
        }
    }
}