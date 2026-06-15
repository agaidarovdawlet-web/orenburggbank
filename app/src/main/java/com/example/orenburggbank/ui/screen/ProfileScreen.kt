/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.orenburggbank.domain.Permission
import com.example.orenburggbank.viewmodel.WorkViewModel

@Composable
/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
fun ProfileScreen(viewModel: WorkViewModel, onMatrix: () -> Unit, onDelegation: () -> Unit, onLogout: () -> Unit) {
    val user by viewModel.currentUser.collectAsState()
    Column(Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(Modifier.size(88.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)), contentAlignment = Alignment.Center) {
            Text(user?.username?.take(2)?.uppercase() ?: "О", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
        Text(user?.username ?: "Пользователь", style = MaterialTheme.typography.headlineMedium)
        Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp)) {
            Text(user?.role?.displayName ?: "Просмотр", Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = MaterialTheme.colorScheme.primary)
        }
        OutlinedButton(onMatrix, Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Icon(Icons.Default.GridView, null); Spacer(Modifier.width(8.dp)); Text("Матрица ролей") }
        if (viewModel.hasPermission(Permission.DELEGATE_APPROVER)) {
            OutlinedButton(onDelegation, Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Icon(Icons.Default.SwapHoriz, null); Spacer(Modifier.width(8.dp)); Text("Перенаправления") }
        }
        Spacer(Modifier.weight(1f))
        OutlinedButton(onLogout, Modifier.fillMaxWidth().heightIn(min = 48.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
            Icon(Icons.Default.ExitToApp, "Выйти"); Spacer(Modifier.width(8.dp)); Text("Выйти")
        }
    }
}
