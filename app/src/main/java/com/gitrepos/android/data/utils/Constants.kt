package com.gitrepos.android.data.utils

object Constants {

    const val INTENT_BOOT_RECEIVER = "android.intent.action.BOOT_COMPLETED"
    const val NOTIFICATION_CHANNEL_NAME = "Git WorkManager Notifications"
    const val NOTIFICATION_CHANNEL_DESCRIPTION =
        "Shows notifications to notify user about available updates"
    const val CHANNEL_ID = "GIT_UPDATES_NOTIFICATION"
    const val NOTIFICATION_ID = 1
    const val ALARM_DELAY_TIME: Long = 10 * 60 * 10000 // 10 minutes

}