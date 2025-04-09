package com.example.pavlov

import android.os.Bundle
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.pavlov.theme.PavlovTheme
import com.example.pavlov.viewmodels.SharedViewModel
import com.example.pavlov.views.GlobalCanvasOverlay
import com.example.pavlov.views.PavlovNavHost

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !hasExactAlarmPermission(this)) {
            requestExactAlarmPermission(this)
        }

        setContent {
            val sharedViewModel = viewModel<SharedViewModel>()
            val sharedState by sharedViewModel.state.collectAsStateWithLifecycle()
            // This will re-compose when isDarkTheme changes
            val isDarkMode by PavlovApplication.isDarkTheme.collectAsState()
            PavlovTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PavlovNavHost(
                        sharedViewModel = sharedViewModel,
                        navController = rememberNavController()
                    )
                    // This is Root element for rendering particle effects on top of the main scene.
                    // It needs to be placed here so that it uses that same global coordinates that
                    // we get from measuring elements in the normal layout hierarchy.
                    GlobalCanvasOverlay(
                        state = sharedState,
                        onEvent = {sharedViewModel.onEvent(it)}
                    )

                }
            }
        }
    }
}


