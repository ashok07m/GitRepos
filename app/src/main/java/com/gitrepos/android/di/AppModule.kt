package com.gitrepos.android.di

import com.gitrepos.android.R
import com.gitrepos.android.data.database.AppDatabase
import com.gitrepos.android.data.network.RetrofitClient
import com.gitrepos.android.data.network.api.GitApiService
import com.gitrepos.android.data.network.interceptor.AuthTokenInterceptor
import com.gitrepos.android.data.network.interceptor.AuthTokenInterceptorImpl
import com.gitrepos.android.data.network.interceptor.ConnectivityInterceptor
import com.gitrepos.android.data.network.interceptor.ConnectivityInterceptorImpl
import com.gitrepos.android.data.persistence.PreferenceManger
import com.gitrepos.android.data.repositories.*
import com.gitrepos.android.data.source.git.GitReposDataSource
import com.gitrepos.android.data.source.git.GitReposDataSourceImpl
import com.gitrepos.android.data.source.login.LoginDataSource
import com.gitrepos.android.ui.CommonViewModel
import com.gitrepos.android.ui.details.DetailsViewModel
import com.gitrepos.android.ui.home.HomeViewModel
import com.gitrepos.android.ui.login.LoginViewModel
import com.gitrepos.android.ui.repos.ReposViewModel
import com.gitrepos.android.ui.settings.SettingsViewModel
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

    single { AppDatabase.getAppDatabaseInstance(get()) }
    factory { get<AppDatabase>().reposDao() }
    single<DatabaseRepository> { DatabaseRepositoryImpl(get()) }

    factory { get<AppDatabase>().gitItemsDao() }
    single<GitCacheDbRepository> { GitCacheDbRepositoryImpl(get()) }

    single<GitReposDataSource> {
        GitReposDataSourceImpl(get(), get())
    }

    single<GitRepository> { GitRepositoryImpl(get()) }
    viewModel { HomeViewModel(get()) }

    single { LoginDataSource() }
    single<LoginRepository> { LoginRepositoryImpl(get()) }
    viewModel { LoginViewModel(get(), get()) }

    viewModel { DetailsViewModel(get(), get()) }

    viewModel { ReposViewModel(get()) }

    viewModel { SettingsViewModel(get()) }

    single { PreferenceManger(get()) }

    viewModel { CommonViewModel(get(), get(), get(), get()) }
}