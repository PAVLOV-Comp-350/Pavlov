package com.example.pavlov

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pavlov.theme.PavlovTheme
import com.example.pavlov.viewmodels.GoalsViewModel
import com.example.pavlov.views.GoalsListScreen

class MainActivity : ComponentActivity() {

    /**
     * This property allows us to access the GoalsViewModel from the Activity as though it were a
     * member called viewModel. In reality it is a delegate function that initializes the viewModel
     * on the first call using the factoryProducer object that is passed as a parameter. We need to
     * do this because the view model is a consumer of the goalsDAO that allows us to access the app
     * state that is stored in non-volatile storage.
     */
    private val viewModel by viewModels<GoalsViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                private val db = PavlovApplication.local_db
                override fun<T: ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return GoalsViewModel(db.goalDao, db.activityDao) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // This will re-compose when isDarkTheme changes
            val isDarkMode by PavlovApplication.isDarkTheme.collectAsState()
            PavlovTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val state by viewModel.state.collectAsState()
                    GoalsListScreen(
                        state = state,
                        onEvent = { viewModel.onEvent(it) },
                    )
                }
            }
        }
    }
}
