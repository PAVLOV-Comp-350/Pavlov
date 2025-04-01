package com.example.pavlov

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.app.Activity
import android.util.Log
import android.widget.Toast
import java.util.*

//Check the app has permission
fun hasExactAlarmPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.canScheduleExactAlarms()
    } else {
        true
    }
}
//Request permission
fun requestExactAlarmPermission(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        intent.data = Uri.parse("package:${activity.packageName}")
        activity.startActivity(intent)
    }
}

fun scheduleNotification(context: Context, taskName: String, triggerTimeInMinutes: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !hasExactAlarmPermission(context)) {
        Log.e("NotificationHelper", "Missing SCHEDULE_EXACT_ALARM permission")
        Toast.makeText(
            context,
            "Notification permission required. Please enable in settings.",
            Toast.LENGTH_LONG
        ).show()
        return
    }
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("task_name", taskName)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, triggerTimeInMinutes / 60)
        set(Calendar.MINUTE, triggerTimeInMinutes % 60)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (before(Calendar.getInstance())) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    val triggerTime = calendar.timeInMillis

    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        triggerTime,
        pendingIntent
    )
}

