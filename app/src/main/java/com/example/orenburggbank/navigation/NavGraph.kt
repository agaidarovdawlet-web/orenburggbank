/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.orenburggbank.ui.screen.CalendarScreen
import com.example.orenburggbank.ui.screen.CreateWorkScreen
import com.example.orenburggbank.ui.screen.HomeScreen
import com.example.orenburggbank.ui.screen.RolesScreen
import com.example.orenburggbank.ui.screen.LoginScreen
import com.example.orenburggbank.ui.screen.AccessDeniedScreen
import com.example.orenburggbank.ui.screen.MatrixScreen
import com.example.orenburggbank.ui.screen.DelegationScreen
import com.example.orenburggbank.ui.screen.SplashScreen
import com.example.orenburggbank.ui.screen.SystemsScreen
import com.example.orenburggbank.ui.screen.WorkDetailScreen
import com.example.orenburggbank.ui.screen.WorksListScreen
import com.example.orenburggbank.ui.screen.ProfileScreen
import com.example.orenburggbank.ui.screen.AboutScreen
import com.example.orenburggbank.viewmodel.WorkViewModel
import com.example.orenburggbank.domain.Permission

/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
sealed class Screen(val route: String) {
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    object Login : Screen("login")
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    object Splash : Screen("splash")
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    object AccessDenied : Screen("access_denied")
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    object Home : Screen("home")
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    object Profile : Screen("profile")
    object About : Screen("about")
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    object Works : Screen("works")
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    object Calendar : Screen("calendar")
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    object Systems : Screen("systems")
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    object Roles : Screen("roles")
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    object Matrix : Screen("matrix")
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    object Delegation : Screen("delegation")
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    object CreateWork : Screen("create_work")
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    object EditWork : Screen("edit_work/{workId}") {
        /**
         * Назначение: блок реализует отдельную функцию портала техработ.
         * Входные данные: параметры объявления.
         * Выходные данные: результат объявления или изменение состояния UI.
         * Роль в проекте: покрывает соответствующий сценарий технического задания.
         */
        fun createRoute(workId: Long) = "edit_work/$workId"
    }
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    object WorkDetail : Screen("work_detail/{workId}") {
        /**
         * Назначение: блок реализует отдельную функцию портала техработ.
         * Входные данные: параметры объявления.
         * Выходные данные: результат объявления или изменение состояния UI.
         * Роль в проекте: покрывает соответствующий сценарий технического задания.
         */
        fun createRoute(workId: Long) = "work_detail/$workId"
    }
}

@Composable
/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
fun NavGraph(
    navController: NavHostController,
    viewModel: WorkViewModel,
    modifier: Modifier = Modifier
) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier,
        enterTransition = { slideInHorizontally { it / 4 } + fadeIn() },
        exitTransition = { slideOutHorizontally { -it / 4 } + fadeOut() },
        popEnterTransition = { slideInHorizontally { -it / 4 } + fadeIn() },
        popExitTransition = { slideOutHorizontally { it / 4 } + fadeOut() }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen {
                navController.navigate(if (isAuthenticated) Screen.Works.route else Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        }
        composable(Screen.Login.route) {
            LoginScreen(viewModel) {
                navController.navigate(Screen.Works.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }
        composable(Screen.AccessDenied.route) {
            AccessDeniedScreen { navController.popBackStack() }
        }
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onWorkClick = { id -> navController.navigate(Screen.WorkDetail.createRoute(id)) }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel,
                onMatrix = { navController.navigate(Screen.Matrix.route) },
                onDelegation = { navController.navigate(Screen.Delegation.route) },
                onLogout = {
                    viewModel.logout()
                    navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                }
            )
        }
        composable(Screen.About.route) { AboutScreen() }
        composable(Screen.Works.route) {
            WorksListScreen(
                viewModel = viewModel,
                onCreateWork = { navController.navigate(Screen.CreateWork.route) },
                onWorkClick = { id -> navController.navigate(Screen.WorkDetail.createRoute(id)) }
            )
        }
        composable(Screen.Calendar.route) {
            CalendarScreen(
                viewModel = viewModel,
                onWorkClick = { id -> navController.navigate(Screen.WorkDetail.createRoute(id)) }
            )
        }
        composable(Screen.Systems.route) {
            SystemsScreen(viewModel = viewModel)
        }
        composable(Screen.Roles.route) {
            if (viewModel.hasPermission(Permission.MANAGE_ROLES)) RolesScreen()
            else AccessDeniedScreen { navController.popBackStack() }
        }
        composable(Screen.Matrix.route) { MatrixScreen() }
        composable(Screen.Delegation.route) {
            if (viewModel.hasPermission(Permission.DELEGATE_APPROVER)) DelegationScreen(viewModel) { navController.popBackStack() }
            else AccessDeniedScreen { navController.popBackStack() }
        }
        composable(Screen.CreateWork.route) {
            if (viewModel.hasPermission(Permission.CREATE_WORK)) CreateWorkScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onWorkCreated = { navController.popBackStack() }
            )
            else AccessDeniedScreen { navController.popBackStack() }
        }
        composable(Screen.EditWork.route) { backStackEntry ->
            val workId = backStackEntry.arguments?.getString("workId")?.toLongOrNull() ?: 0L
            val work = viewModel.allWorks.value.find { it.work.id == workId }?.work
            if (work != null && viewModel.canEdit(work)) {
                CreateWorkScreen(viewModel, { navController.popBackStack() }, { navController.popBackStack() }, work)
            } else AccessDeniedScreen { navController.popBackStack() }
        }
        composable(Screen.WorkDetail.route) { backStackEntry ->
            val workId = backStackEntry.arguments?.getString("workId")?.toLongOrNull() ?: 0L
            WorkDetailScreen(
                workId = workId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate(Screen.EditWork.createRoute(id)) }
            )
        }
    }
}
