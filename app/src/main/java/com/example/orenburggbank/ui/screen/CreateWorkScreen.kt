/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.FilterChip
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.orenburggbank.data.entity.WorkEntity
import com.example.orenburggbank.ui.theme.*
import com.example.orenburggbank.viewmodel.WorkViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
fun CreateWorkScreen(
    viewModel: WorkViewModel,
    onBack: () -> Unit,
    onWorkCreated: () -> Unit,
    existingWork: WorkEntity? = null
) {
    val systems by viewModel.allSystems.collectAsState()
    
    var title by remember(existingWork?.id) { mutableStateOf(existingWork?.title ?: "") }
    var selectedSystem by remember(existingWork?.id) { mutableStateOf(systems.find { it.id == existingWork?.systemId } ?: systems.firstOrNull()) }
    var affectedSystemIds by remember(existingWork?.id) { mutableStateOf(existingWork?.affectedSystemIds ?: emptyList()) }
    var date by remember(existingWork?.id) { mutableStateOf(existingWork?.date ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)) }
    var endDate by remember(existingWork?.id) { mutableStateOf(existingWork?.endDateTime?.substringBefore(" ") ?: date) }
    var timeStart by remember(existingWork?.id) { mutableStateOf(existingWork?.timeStart ?: "00:00") }
    var timeEnd by remember(existingWork?.id) { mutableStateOf(existingWork?.timeEnd ?: "01:00") }
    var engineerName by remember(existingWork?.id) { mutableStateOf(existingWork?.engineerName ?: "Петров П.") }
    var description by remember(existingWork?.id) { mutableStateOf(existingWork?.description ?: "") }
    var rollbackPlan by remember(existingWork?.id) { mutableStateOf(existingWork?.rollbackPlan ?: "") }
    
    var systemExpanded by remember { mutableStateOf(false) }
    LaunchedEffect(systems, existingWork?.id) {
        if (selectedSystem == null) selectedSystem = systems.find { it.id == existingWork?.systemId } ?: systems.firstOrNull()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existingWork == null) "Новая техработа" else "Редактирование") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ob600,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Основная информация", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ob600)
            Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("1 Основное", "2 Системы", "3 Согласующие").forEach {
                    Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp)) {
                        Text(it, Modifier.padding(horizontal = 8.dp, vertical = 6.dp), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            
            StyledTextField(
                value = title,
                onValueChange = { title = it },
                label = "Название работ",
                placeholder = "Например: Обновление СУБД"
            )

            // System Selector
            Box(modifier = Modifier.padding(top = 12.dp)) {
                OutlinedTextField(
                    value = selectedSystem?.name ?: "Выберите систему",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Система") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.KeyboardArrowDown, null, Modifier.clickable { systemExpanded = true })
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ob600,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                DropdownMenu(
                    expanded = systemExpanded,
                    onDismissRequest = { systemExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    systems.forEach { system ->
                        DropdownMenuItem(
                            text = { Text(system.name) },
                            onClick = {
                                selectedSystem = system
                                systemExpanded = false
                            }
                        )
                    }
                }
            }

            Text("Затронутые системы", modifier = Modifier.padding(top = 16.dp), fontWeight = FontWeight.Bold)
            systems.forEach { system ->
                FilterChip(
                    selected = system.id in affectedSystemIds,
                    onClick = {
                            val checked = system.id !in affectedSystemIds
                            affectedSystemIds = if (checked) (affectedSystemIds + system.id).distinct() else affectedSystemIds - system.id
                            if (checked && selectedSystem == null) selectedSystem = system
                    },
                    label = { Text(system.name) },
                    leadingIcon = {
                        Box(Modifier.size(8.dp).background(when (system.criticality.name) {
                            "CRITICAL" -> CriticalRed; "HIGH" -> HighOrange; "MEDIUM" -> MediumAmber; else -> LowEmerald
                        }, androidx.compose.foundation.shape.CircleShape))
                    },
                    modifier = Modifier.padding(end = 6.dp)
                )
            }

            Row(modifier = Modifier.padding(top = 12.dp)) {
                StyledTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = "Дата",
                    modifier = Modifier.weight(1f).padding(end = 4.dp),
                    leadingIcon = Icons.Default.CalendarToday
                )
            }
            StyledTextField(
                value = endDate,
                onValueChange = { endDate = it },
                label = "Дата окончания",
                leadingIcon = Icons.Default.CalendarToday
            )
            
            Row(modifier = Modifier.padding(top = 12.dp)) {
                StyledTextField(
                    value = timeStart,
                    onValueChange = { timeStart = it },
                    label = "Начало",
                    modifier = Modifier.weight(1f).padding(end = 4.dp),
                    leadingIcon = Icons.Default.Schedule
                )
                StyledTextField(
                    value = timeEnd,
                    onValueChange = { timeEnd = it },
                    label = "Окончание",
                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                    leadingIcon = Icons.Default.Schedule
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Детали реализации", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ob600)

            StyledTextField(
                value = engineerName,
                onValueChange = { engineerName = it },
                label = "Инженер",
                placeholder = "Фамилия И."
            )

            StyledTextField(
                value = description,
                onValueChange = { description = it },
                label = "Описание работ",
                minLines = 3
            )

            StyledTextField(
                value = rollbackPlan,
                onValueChange = { rollbackPlan = it },
                label = "План отката",
                minLines = 2
            )

            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    if (title.isBlank()) {
                        viewModel.notify("Название обязательно")
                    } else if (selectedSystem == null || affectedSystemIds.isEmpty()) {
                        viewModel.notify("Выберите систему из списка")
                    } else {
                        val newWork = WorkEntity(
                            title = title,
                            date = date,
                            timeStart = timeStart,
                            timeEnd = timeEnd,
                            systemId = selectedSystem!!.id,
                            engineerName = engineerName,
                            description = description,
                            rollbackPlan = rollbackPlan,
                            status = com.example.orenburggbank.data.entity.WorkStatus.PENDING,
                            affectedSystemIds = affectedSystemIds,
                            startDateTime = "$date $timeStart",
                            endDateTime = "$endDate $timeEnd"
                        )
                        
                        if (existingWork == null) viewModel.createWork(newWork) {
                            val names = systems.filter { it.id in affectedSystemIds }.joinToString { it.name }
                            viewModel.notify("Отправлено уведомление согласующим систем [$names] и в общий чат Max")
                            onWorkCreated()
                        }
                        else viewModel.updateWork(newWork.copy(id = existingWork.id, ownerUsername = existingWork.ownerUsername), onWorkCreated)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ob600)
            ) {
                Text(if (existingWork == null) "Создать заявку" else "Сохранить изменения", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(40.dp))
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
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    minLines: Int = 1
) {
    Column(modifier = modifier.padding(top = 12.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            placeholder = { if (placeholder.isNotEmpty()) Text(placeholder) },
            leadingIcon = leadingIcon?.let { { Icon(it, null, Modifier.size(20.dp)) } },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ob600,
                unfocusedBorderColor = Color.LightGray
            ),
            minLines = minLines
        )
    }
}
