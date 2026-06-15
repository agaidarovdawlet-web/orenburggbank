/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.orenburggbank.data.entity.DelegationEntity
import com.example.orenburggbank.viewmodel.WorkViewModel
import java.time.LocalDateTime

@Composable
/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
fun DelegationScreen(viewModel: WorkViewModel, onDone: () -> Unit) {
    val systems by viewModel.allSystems.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    var selected by remember { mutableStateOf(systems.firstOrNull()) }
    var from by remember { mutableStateOf(LocalDateTime.now().toString()) }
    var to by remember { mutableStateOf(LocalDateTime.now().plusDays(7).toString()) }
    var reason by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Перенаправить согласования", style = MaterialTheme.typography.headlineMedium)
        Text("Система: ${selected?.name ?: "нет"}")
        systems.forEach { system -> Row { RadioButton(selected == system, { selected = system }); Text(system.name, Modifier.padding(top = 12.dp)) } }
        OutlinedTextField(from, { from = it }, label = { Text("Период с (ISO)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(to, { to = it }, label = { Text("Период по (ISO)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(reason, { reason = it }, label = { Text("Обоснование") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            selected?.let { system ->
                viewModel.createDelegation(DelegationEntity(systemId = system.id, approverId = user?.userId ?: "", deputyId = system.deputyId, dateFrom = from, dateTo = to, reason = reason, createdAt = LocalDateTime.now().toString()), onDone)
            }
        }, enabled = selected != null && reason.isNotBlank()) { Text("Перенаправить") }
    }
}
