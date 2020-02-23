package com.gitrepos.android.di.module

import com.gitrepos.android.R
import com.gitrepos.android.data.network.RetrofitClient
import com.gitrepos.android.data.network.api.GitApiService
import com.gitrepos.android.data.network.interceptor.AuthTokenInterceptor
import com.gitrepos.android.data.network.interceptor.AuthTokenInterceptorImpl
import com.gitrepos.android.data.network.interceptor.ConnectivityInterceptor
import com.gitrepos.android.data.network.interceptor.ConnectivityInterceptorImpl
import com.gitrepos.android.data.network.source.git.GitReposDataSource
import com.gitrepos.android.data.network.source.git.GitReposDataSourceImpl
import com.gitrepos.android.data.network.source.login.LoginDataSource
import com.gitrepos.android.data.repositories.GitRepository
import com.gitrepos.android.data.repositories.GitRepositoryImpl
import com.gitrepos.android.data.repositories.LoginRepository
import com.gitrepos.android.ui.details.DetailsViewModel
import com.gitrepos.android.ui.home.HomeViewModel
import com.gitrepos.android.ui.login.LoginViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Module to provide app dependencies
 */

val appModule = module {
    single<ConnectivityInterceptor> { ConnectivityInterceptorImpl(androidContext()) }
    single<AuthTokenInterceptor> {
        AuthTokenInterceptorImpl(
            androidContext().getString(R.string.authToken)
        )
    }
    single { RetrofitClient(get()) }
    factory {
        GitApiService.invoke(
            get(),
            get(),
            androidContext().getString(R.string.base_url_git_repos)
        )
    }
    single<GitReposDataSource> {
        GitReposDataSourceImpl(
            get()
        )
    }
    single<GitRepository> { GitRepositoryImpl(get()) }
    viewModel { HomeViewModel(get()) }


    single { LoginDataSource() }
    single { LoginRepository(get()) }
    viewModel { LoginViewModel(get()) }

    viewModel { DetailsViewModel(get()) }
}