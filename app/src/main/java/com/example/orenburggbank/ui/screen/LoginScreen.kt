/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.orenburggbank.ui.components.BankLogo
import com.example.orenburggbank.ui.theme.BankPrimary
import com.example.orenburggbank.ui.theme.BankSecondary
import com.example.orenburggbank.ui.theme.StatusCancelled
import com.example.orenburggbank.viewmodel.WorkViewModel

@Composable
/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
fun LoginScreen(viewModel: WorkViewModel, onLoggedIn: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var failed by remember { mutableStateOf(false) }
    val shake = remember { Animatable(0f) }
    LaunchedEffect(failed) {
        if (failed) {
            repeat(3) { shake.animateTo(10f, tween(45)); shake.animateTo(-10f, tween(45)) }
            shake.animateTo(0f, tween(45))
            failed = false
        }
    }
    Box(
        Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(BankSecondary, BankPrimary))).padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
            BankLogo(light = true)
            Card(
                Modifier.widthIn(max = 430.dp).offset { IntOffset(shake.value.toInt(), 0) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Портал технических работ", style = MaterialTheme.typography.headlineMedium, color = BankSecondary)
                    OutlinedTextField(
                        username, { username = it }, label = { Text("Логин") },
                        leadingIcon = { Icon(Icons.Default.Person, "Пользователь") },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        isError = failed
                    )
                    OutlinedTextField(
                        password, { password = it }, label = { Text("Пароль") },
                        leadingIcon = { Icon(Icons.Default.Lock, "Пароль") },
                        singleLine = true, visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(), isError = failed
                    )
                    Button(
                        onClick = {
                            if (viewModel.login(username, password)) onLoggedIn() else failed = true
                        },
                        enabled = username.isNotBlank() && password.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BankPrimary, disabledContainerColor = Color.White.copy(alpha = 0.7f))
                    ) { Text("Войти") }
                    if (failed) Text("Проверьте логин и пароль", color = StatusCancelled, style = MaterialTheme.typography.bodySmall)
                    Text("Демо: admin/admin · approver/approver · viewer/view", color = BankSecondary, style = MaterialTheme.typography.bodySmall)
                    Text("© 2026 АО «БАНК ОРЕНБУРГ» | Агайдаров Д.А.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
