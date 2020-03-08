package com.gitrepos.android.data.source.git

import com.gitrepos.android.data.database.entity.GitItem

/**
 * @author Created by kuashok on 2020-02-16
 */


sealed class GitResult {
    data class SuccessRepos(val repositories: List<GitItem>, val error: Error) : GitResult()
    data class Error(val errorCode: ErrorCodes, val message: String?) :
        GitResult() {
        enum class ErrorCodes {
            NoConnectivityError,
            ServerError,
            NONE
        }
    }
}