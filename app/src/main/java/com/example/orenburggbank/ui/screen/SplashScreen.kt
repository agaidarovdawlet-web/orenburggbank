/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import com.example.orenburggbank.ui.components.BankLogo
import com.example.orenburggbank.ui.theme.BankPrimary
import com.example.orenburggbank.ui.theme.BankSecondary
import kotlinx.coroutines.delay

@Composable
/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
fun SplashScreen(onFinished: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(if (visible) 1f else 0f, tween(800), label = "splashFade")
    LaunchedEffect(Unit) {
        visible = true
        delay(1400)
        onFinished()
    }
    Column(
        Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(BankSecondary, BankPrimary))),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(Modifier.height(1.dp))
        BankLogo(Modifier.alpha(alpha), light = true)
        Text(
            "Агайдаров Д.А. | 23 КСК 4 | Университетский колледж ОГУ",
            modifier = Modifier.padding(16.dp).alpha(alpha),
            color = Color.White.copy(alpha = 0.75f),
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall
        )
    }
}
