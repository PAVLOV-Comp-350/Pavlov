package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra("TASK_TITLE") ?: "Task Reminder"
        val message = intent?.getStringExtra("TASK_MESSAGE") ?: "It's time to complete your task!"

        val notificationHelper = NotificationHelper(context)
        notificationHelper.createNotificationChannel()
        notificationHelper.sendNotification(title, message)
    }
}
