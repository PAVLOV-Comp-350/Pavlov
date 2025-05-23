package com.example.pavlov.views

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.pavlov.PavlovApplication
import com.example.pavlov.viewmodels.AnyEvent
import com.example.pavlov.viewmodels.CasinoEvent
import com.example.pavlov.viewmodels.CasinoViewModel
import com.example.pavlov.viewmodels.GoalsEvent
import com.example.pavlov.viewmodels.GoalsViewModel
import com.example.pavlov.viewmodels.PetEvent
import com.example.pavlov.viewmodels.PetViewModel
import com.example.pavlov.viewmodels.SettingsEvent
import com.example.pavlov.viewmodels.SettingsViewModel
import com.example.pavlov.viewmodels.SharedEvent
import com.example.pavlov.viewmodels.SharedViewModel
import kotlinx.serialization.Serializable


@Serializable object MainRoute

sealed interface Screen {
    @Serializable
    data object Goals : Screen
    @Serializable
    data object Settings : Screen
    @Serializable
    data object Casino : Screen
    @Serializable
    data object Pet : Screen
}



@Composable
fun PavlovNavHost(
    sharedViewModel: SharedViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Screen = Screen.Goals,
) {
    NavHost(
        navController = navController,
        startDestination = MainRoute,
    ) {
        navigation<MainRoute>(
            startDestination = startDestination,
        ) {
            composable<Screen.Goals>() {
                val sharedState by sharedViewModel.state.collectAsStateWithLifecycle()
                /**
                 * A function that initializes the viewModel on the first call using the factoryProducer
                 * object that is passed as a parameter. We need to do this because the view model is a
                 * consumer of the goalsDAO that allows us to access the app state that is stored in
                 * non-volatile storage.
                 */
                val goalsViewModel = viewModel<GoalsViewModel>(factory =
                object : ViewModelProvider.Factory {
                    private val db = PavlovApplication.local_db
                    override fun<T: ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return GoalsViewModel(db.goalDao, db.activityDao) as T
                    }
                }
                )
                val state by goalsViewModel.state.collectAsState()
                GoalsListScreen(
                    state = state,
                    sharedState = sharedState,
                    onEvent = {
                        when(it) {
                            is GoalsEvent -> goalsViewModel.onEvent(it)
                            is SharedEvent -> sharedViewModel.onEvent(it)
                            else -> Log.e("GOALS", "Received an unsupported event: $it")
                        }},
                    onNavigate = {
                        navController.navigate(it)
                        sharedViewModel.onEvent(SharedEvent.SetScreen(it))
                    }
                )
            }
            composable<Screen.Settings> {
                val sharedState by sharedViewModel.state.collectAsStateWithLifecycle()

                val settingsViewModel = viewModel(SettingsViewModel::class)
                val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
                SettingsScreen(
                    state = settingsState,
                    sharedState = sharedState,
                    onEvent = {
                        when(it) {
                            is SettingsEvent -> settingsViewModel.onEvent(it)
                            is SharedEvent -> sharedViewModel.onEvent(it)
                            else -> Log.e("GOALS", "Received an unsupported event: $it")
                    }},
                    onNavigate = {
                        navController.navigate(it)
                        sharedViewModel.onEvent(SharedEvent.SetScreen(it))
                    }
                )

            }
            composable<Screen.Casino> {
                val sharedState by sharedViewModel.state.collectAsStateWithLifecycle()

                val casinoViewModel = viewModel(CasinoViewModel::class)
                val casinoState by casinoViewModel.state.collectAsState()
                CasinoScreen(
                    state = casinoState,
                    sharedState = sharedState,
                    onEvent = {
                        when(it) {
                            is CasinoEvent -> casinoViewModel.onEvent(it)
                            is SharedEvent -> sharedViewModel.onEvent(it)
                            else -> Log.e("GOALS", "Received an unsupported event: $it")
                        }},
                    onNavigate = {
                        navController.navigate(it)
                        sharedViewModel.onEvent(SharedEvent.SetScreen(it))
                    }
                )

            }

            composable<Screen.Pet> {
                val sharedState by sharedViewModel.state.collectAsStateWithLifecycle()

                val petViewModel = viewModel<PetViewModel>(factory =
                    object : ViewModelProvider.Factory {
                        private val db = PavlovApplication.local_db
                        override fun<T: ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return PetViewModel(db.petDao) as T
                        }
                    }
                )
                val petState by petViewModel.state.collectAsState()
                PetScreen(
                    state = petState,
                    sharedState = sharedState,
                    onEvent = {
                        when(it) {
                            is PetEvent -> petViewModel.onEvent(it)
                            is SharedEvent -> sharedViewModel.onEvent(it)
                            else -> Log.e("PET", "Received an unsupported event: $it")
                        }},
                    onNavigate = {
                        navController.navigate(it)
                        sharedViewModel.onEvent(SharedEvent.SetScreen(it))
                    }
                )

            }
        }
    }
}

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null,
    val screenId: Screen,
)

@Composable
fun PavlovNavbar(
    modifier: Modifier = Modifier,
    activeScreen: Screen,
    onNavigate: (Screen) -> Unit,
) {
    val items = listOf(
        BottomNavigationItem(
            title = "Goals",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            hasNews = false,
            screenId = Screen.Goals
        ),
        BottomNavigationItem(
            title = "Casino",
            selectedIcon = Icons.Filled.AddCircle,
            unselectedIcon = Icons.Outlined.AddCircle,
            hasNews = false,
            screenId = Screen.Casino
        ),
        BottomNavigationItem(
            title = "Pet",
            selectedIcon = Icons.Filled.Star,
            unselectedIcon = Icons.Outlined.Star,
            hasNews = false,
            screenId = Screen.Pet,
        ),
        BottomNavigationItem(
            title = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            hasNews = false,
            screenId = Screen.Settings
        ),
    )
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = activeScreen == item.screenId,
                onClick = { onNavigate(item.screenId) },
                label = { Text(text = item.title) },
                alwaysShowLabel = true,
                icon = {
                    BadgedBox(
                        badge = {
                            if(item.badgeCount != null) {
                                Badge {
                                    Text(text = item.badgeCount.toString())
                                }
                            } else if(item.hasNews) {
                                Badge()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (item.screenId == activeScreen) {
                                item.selectedIcon
                            } else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    }
                }
            )
        }
    }
}