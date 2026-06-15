/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.orenburggbank.data.entity.Criticality
import com.example.orenburggbank.data.entity.SystemEntity
import com.example.orenburggbank.domain.Permission
import com.example.orenburggbank.viewmodel.WorkViewModel
import com.example.orenburggbank.ui.components.LevelBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
fun SystemsScreen(viewModel: WorkViewModel) {
    val systems by viewModel.allSystems.collectAsState()
    var editing by remember { mutableStateOf<SystemEntity?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val filtered = systems.filter { it.name.contains(query, true) || it.description.contains(query, true) }
    Scaffold(floatingActionButton = {
        if (viewModel.hasPermission(Permission.EDIT_SYSTEMS)) {
            FloatingActionButton(onClick = { editing = null; showEditor = true }) { Icon(Icons.Default.Add, null) }
        }
    }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text("Справочники", style = MaterialTheme.typography.headlineMedium) }
            item { OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), placeholder = { Text("Поиск систем") }, leadingIcon = { Icon(Icons.Default.Search, "Поиск") }, singleLine = true) }
            if (filtered.isEmpty()) item { Text("Системы не найдены") }
            items(filtered) { system ->
                Card(onClick = { if (viewModel.hasPermission(Permission.EDIT_SYSTEMS)) { editing = system; showEditor = true } }, shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(48.dp).rotate(45f).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)), contentAlignment = Alignment.Center) {
                            Text(system.name.take(1), Modifier.rotate(-45f), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge)
                        }
                        Column(Modifier.padding(start = 16.dp).weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) { Text(system.name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f)); LevelBadge(system.level, system.levelColor) }
                        Text(system.description, maxLines = 2, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Согласующий: ${system.approverId}; заместитель: ${system.deputyId}")
                        Text("Зависимые системы: ${system.dependentSystemIds.joinToString().ifEmpty { "нет" }}")
                        }
                    }
                }
            }
        }
    }
    if (showEditor) SystemEditor(editing, systems, { showEditor = false }) {
        viewModel.saveSystem(it) { showEditor = false }
    }
}

@Composable
/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
private fun SystemEditor(existing: SystemEntity?, systems: List<SystemEntity>, onDismiss: () -> Unit, onSave: (SystemEntity) -> Unit) {
    var id by remember { mutableStateOf(existing?.id ?: "") }
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var approver by remember { mutableStateOf(existing?.approverId ?: "approver") }
    var deputy by remember { mutableStateOf(existing?.deputyId ?: "deputy") }
    var criticality by remember { mutableStateOf(existing?.criticality ?: Criticality.MEDIUM) }
    var dependencies by remember { mutableStateOf(existing?.dependentSystemIds ?: emptyList()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Новая система" else "Редактирование системы") },
        text = {
            Column(Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(id, { id = it }, label = { Text("ID") }, enabled = existing == null)
                OutlinedTextField(name, { name = it }, label = { Text("Наименование") })
                OutlinedTextField(description, { description = it }, label = { Text("Описание") })
                OutlinedTextField(approver, { approver = it }, label = { Text("ID согласующего") })
                OutlinedTextField(deputy, { deputy = it }, label = { Text("ID заместителя") })
                Text("Критичность")
                Criticality.entries.forEach { item ->
                    Row { RadioButton(criticality == item, { criticality = item }); Text(item.displayName, Modifier.padding(top = 12.dp)) }
                }
                Text("Зависимые системы")
                systems.filter { it.id != id }.forEach { system ->
                    Row { Checkbox(system.id in dependencies, { checked -> dependencies = if (checked) dependencies + system.id else dependencies - system.id }); Text(system.name, Modifier.padding(top = 12.dp)) }
                }
            }
        },
        confirmButton = { Button(onClick = { onSave(SystemEntity(id.trim(), name.trim(), description.trim(), approver.trim(), deputy.trim(), criticality, dependencies.distinct())) }, enabled = id.isNotBlank() && name.isNotBlank()) { Text("Сохранить") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}
