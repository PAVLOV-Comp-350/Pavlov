package com.example.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class TaskReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.createNotificationChannel()
        notificationHelper.sendNotification("Task Reminder", "Remember to complete your task!")
        return Result.success()
    }
}
