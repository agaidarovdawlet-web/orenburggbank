/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.orenburggbank.data.DatabaseHelper
import com.example.orenburggbank.data.WorkRepository
import com.example.orenburggbank.navigation.NavGraph
import com.example.orenburggbank.navigation.Screen
import com.example.orenburggbank.ui.theme.OrenburggbankTheme
import com.example.orenburggbank.viewmodel.WorkViewModel
import com.example.orenburggbank.domain.AuthService
import com.example.orenburggbank.domain.AuthorizationService
import com.example.orenburggbank.domain.WorkUseCases
import com.example.orenburggbank.di.AppContainer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment

/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
class MainActivity : ComponentActivity() {
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val container = AppContainer(applicationContext)
        val viewModel = ViewModelProvider(
            this,
            WorkViewModel.Factory(container.repository, container.authService, container.workUseCases)
        )[WorkViewModel::class.java]

        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val isLoading by viewModel.isLoading.collectAsState()
            val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
            LaunchedEffect(Unit) {
                viewModel.messages.collect { snackbarHostState.showSnackbar(it) }
            }

            OrenburggbankTheme {
                Scaffold(
                    snackbarHost = {
                        androidx.compose.material3.SnackbarHost(snackbarHostState) { data ->
                            val success = data.visuals.message.startsWith("Отправлено") || data.visuals.message.contains("успеш")
                            androidx.compose.material3.Snackbar(
                                snackbarData = data,
                                containerColor = if (success) com.example.orenburggbank.ui.theme.StatusApproved else androidx.compose.material3.MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    bottomBar = {
                        val visibleScreens = listOf(Screen.Works, Screen.Calendar, Screen.Systems, Screen.Profile, Screen.About)
                        
                        if (currentRoute in visibleScreens.map { it.route }) {
                            NavigationBar(
                                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
                            ) {
                                visibleScreens.forEach { screen ->
                                    val label = when (screen) {
                                        Screen.Works -> "Техработы"
                                        Screen.Calendar -> "Календарь"
                                        Screen.Systems -> "Справочники"
                                        Screen.Profile -> "Профиль"
                                        Screen.About -> "Инфо"
                                        else -> ""
                                    }
                                    val icon = when (screen) {
                                        Screen.Works -> Icons.Default.Work
                                        Screen.Calendar -> Icons.Default.CalendarMonth
                                        Screen.Systems -> Icons.Default.Settings
                                        Screen.Profile -> Icons.Default.Person
                                        Screen.About -> Icons.Default.Info
                                        else -> Icons.Default.Work
                                    }
                                    
                                    AppNavigationBarItem(
                                        label = label,
                                        icon = icon,
                                        selected = currentRoute == screen.route,
                                        onClick = {
                                            if (currentRoute != screen.route) {
                                                navController.navigate(screen.route) {
                                                    popUpTo(Screen.Works.route) { saveState = true }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(Modifier.fillMaxSize()) {
                        NavGraph(
                            navController = navController,
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                        if (isLoading) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
fun RowScope.AppNavigationBarItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) },
        selected = selected,
        onClick = onClick,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
            selectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
            unselectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        )
    )
}
