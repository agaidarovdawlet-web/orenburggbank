/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ColorLightBlue = androidx.compose.ui.graphics.Color(0xFFEAF7FC)

private val DarkColorScheme = darkColorScheme(
    primary = DarkAccent,
    onPrimary = DarkBackground,
    secondary = BankAccent,
    tertiary = StatusInfo,
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkCard,
    onSurface = DarkText,
    surfaceVariant = DarkCard,
    onSurfaceVariant = DarkTextSecondary,
    outline = BankTextSecondary,
    error = StatusCancelled
)

private val LightColorScheme = lightColorScheme(
    primary = BankPrimary,
    onPrimary = BankCard,
    secondary = BankSecondary,
    onSecondary = BankCard,
    tertiary = BankAccent,
    background = BankBackground,
    onBackground = BankText,
    surface = BankCard,
    onSurface = BankText,
    surfaceVariant = ColorLightBlue,
    onSurfaceVariant = BankTextSecondary,
    outline = BankDivider,
    error = StatusCancelled
)

@Composable
/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
fun OrenburggbankTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.background.toArgb()
            window.navigationBarColor = colors.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }
    MaterialTheme(colorScheme = colors, typography = Typography, content = content)
}
