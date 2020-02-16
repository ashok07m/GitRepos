package com.gitrepos.android

import android.app.Application
import com.gitrepos.android.di.module.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * @author Created by kuashok on 2020-02-16
 */

class RepoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@RepoApp)
            modules(appModule)
        }
    }
}