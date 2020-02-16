package com.gitrepos.android.data.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * @author Created by kuashok on 2019-10-05
 */


object AppUtils {

    /**
     * Checks if device connected to a network
     */
    fun isOnline(appContext: Context): Boolean {
        val connectivityManager =
            appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))

    }
}