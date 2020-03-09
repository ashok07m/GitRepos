package com.gitrepos.android.data.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gitrepos.android.R


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

    /**
     * Shows notification to user
     */
    fun showNotification(context: Context, title: String, message: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Create the NotificationChannel, but only on API 26+ because
            val name: CharSequence = Constants.NOTIFICATION_CHANNEL_NAME
            val description: String = Constants.NOTIFICATION_CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel =
                NotificationChannel(Constants.CHANNEL_ID, name, importance)
            channel.description = description
            // Add the channel
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
        // Create the notification
        val builder =
            NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(LongArray(0))
        // Show the notification
        NotificationManagerCompat.from(context).notify(Constants.NOTIFICATION_ID, builder.build())
    }

}