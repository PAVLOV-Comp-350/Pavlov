package com.example.pavlov

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class GoalReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val goalTitle = inputData.getString("goal_title") ?: "Your Goal"
        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.sendNotification(goalTitle)
        return Result.success()
    }
}
