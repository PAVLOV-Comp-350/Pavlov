package com.example.notification

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var etTaskName: EditText
    private lateinit var btnPickDate: Button
    private lateinit var btnPickTime: Button
    private lateinit var btnScheduleTask: Button
    private lateinit var tvSelectedDateTime: TextView

    private var selectedYear = -1
    private var selectedMonth = -1
    private var selectedDay = -1
    private var selectedHour = -1
    private var selectedMinute = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etTaskName = findViewById(R.id.et_task_name)
        btnPickDate = findViewById(R.id.btn_pick_date)
        btnPickTime = findViewById(R.id.btn_pick_time)
        btnScheduleTask = findViewById(R.id.btn_schedule_task)
        tvSelectedDateTime = findViewById(R.id.tv_selected_datetime)

        btnPickDate.setOnClickListener { pickDate() }
        btnPickTime.setOnClickListener { pickTime() }
        btnScheduleTask.setOnClickListener { scheduleTask() }
    }

    private fun pickDate() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(this, { _, year, month, day ->
            selectedYear = year
            selectedMonth = month + 1 // Month index starts from 0
            selectedDay = day
            updateDateTimeText()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePicker.show()
    }

    private fun pickTime() {
        val calendar = Calendar.getInstance()
        val timePicker = TimePickerDialog(this, { _, hour, minute ->
            selectedHour = hour
            selectedMinute = minute
            updateDateTimeText()
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false)

        timePicker.show()
    }

    private fun updateDateTimeText() {
        if (selectedYear != -1 && selectedHour != -1) {
            tvSelectedDateTime.text = "Selected: $selectedMonth/$selectedDay/$selectedYear $selectedHour:$selectedMinute"
        }
    }

    private fun scheduleTask() {
        val taskName = etTaskName.text.toString().trim()

        if (taskName.isEmpty() || selectedYear == -1 || selectedHour == -1) {
            Toast.makeText(this, "Please enter a task and select date & time", Toast.LENGTH_SHORT).show()
            return
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedYear)
            set(Calendar.MONTH, selectedMonth - 1) // Month is 0-based
            set(Calendar.DAY_OF_MONTH, selectedDay)
            set(Calendar.HOUR_OF_DAY, selectedHour)
            set(Calendar.MINUTE, selectedMinute)
            set(Calendar.SECOND, 0)
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra("TASK_TITLE", taskName)
            putExtra("TASK_MESSAGE", "Reminder for $taskName!")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            taskName.hashCode(), // Unique ID for each task
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(this, "Task Scheduled!", Toast.LENGTH_SHORT).show()
    }
}
