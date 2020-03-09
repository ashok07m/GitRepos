package com.gitrepos.android

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.gitrepos.android.data.receivers.NotificationReceiver
import com.gitrepos.android.data.utils.Constants
import com.gitrepos.android.di.appModule
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

    /**
     * Sets alaram to trigger notification after certain interval
     */
    fun setAlarm() {
        val alarmManager =
            this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        val time: Long = System.currentTimeMillis() + Constants.ALARM_DELAY_TIME
        alarmManager.setRepeating(
            AlarmManager.RTC,
            time,
            Constants.ALARM_DELAY_TIME,
            pendingIntent
        )
    }
}