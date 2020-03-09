package com.gitrepos.android.data.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.gitrepos.android.R
import com.gitrepos.android.data.utils.AppUtils

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        AppUtils.showNotification(
            context,
            context.getString(R.string.title_updates),
            context.getString(R.string.msg_repo_updates_available)
        )
    }
}
