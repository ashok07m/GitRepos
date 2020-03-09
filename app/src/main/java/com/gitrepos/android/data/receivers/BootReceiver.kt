package com.gitrepos.android.data.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.gitrepos.android.RepoApp
import com.gitrepos.android.data.utils.Constants

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Constants.INTENT_BOOT_RECEIVER) {
            (context.applicationContext as RepoApp).setAlarm()
        }
    }
}
