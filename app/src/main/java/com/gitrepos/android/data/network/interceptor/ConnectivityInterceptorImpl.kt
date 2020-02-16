package com.gitrepos.android.data.network.interceptor

import android.content.Context
import com.gitrepos.android.data.utils.AppUtils
import com.gitrepos.android.internal.NoConnectivityException
import okhttp3.Interceptor
import okhttp3.Response

class ConnectivityInterceptorImpl(context: Context) :
    ConnectivityInterceptor {
    private val appContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!AppUtils.isOnline(appContext))
            throw NoConnectivityException()
        return chain.proceed(chain.request())
    }
}