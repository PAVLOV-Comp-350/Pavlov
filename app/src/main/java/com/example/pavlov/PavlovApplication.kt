package com.example.pavlov

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.room.Room
import androidx.preference.PreferenceManager
import com.example.pavlov.models.LocalDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * We are overriding the base Application class to manage the global state of the Room db
 */
class PavlovApplication : Application() {

    /**
     * This is a singleton instance that holds all of the state that needs to exist for the entire
     * lifetime of the app. Declaring it this way allows us to access these properties via
     * PavlovApplication.<property>
     */
    companion object {
        /** The local database to store the structured data of the app */
        lateinit var local_db: LocalDatabase
            private set

        /** A simple key -> value store that is used for unstructured app state */
        lateinit var preferences: SharedPreferences
            private set

        /** The retrieval key to access the isDarkMode property */
        private val DARK_MODE_KEY = "is_dark_mode"
        private val _isDarkTheme = MutableStateFlow(false)

        /** The user facing API for modifying the app theme */
        val isDarkTheme = _isDarkTheme.asStateFlow()
        fun setDarkTheme(value: Boolean) {
            preferences.edit().putBoolean(DARK_MODE_KEY, value).apply()
            _isDarkTheme.value = value
        }

        // TODO: This should be more robust once we have more UserState
        /** The retrieval key to access the treats property */
        private val TREATS_KEY = "user_treats"
        private val _treats = MutableStateFlow<Int>(0)
        val treats = _treats.asStateFlow()
        fun addTreats(value: Int) {
            val newval = _treats.value + value
            preferences.edit().putInt(TREATS_KEY, newval).apply()
            _treats.update { newval }
        }
        fun removeTreats(value: Int) {
            val newval = _treats.value - value
            preferences.edit().putInt(TREATS_KEY, newval).apply()
            _treats.update { newval }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("Pavlov App","Application Created.")

        local_db = Room.databaseBuilder(
            applicationContext,
            LocalDatabase::class.java,
            "pavlov_db"
        )
            .createFromAsset("database/test_data.db")
            .build()

        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        _isDarkTheme.value = preferences.getBoolean(DARK_MODE_KEY, false)
        _treats.value = preferences.getInt(TREATS_KEY, 0)
    }

}