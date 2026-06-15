/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.orenburggbank.ui.components.WorkCard
import com.example.orenburggbank.ui.theme.StatusApproved
import com.example.orenburggbank.ui.theme.StatusCancelled
import com.example.orenburggbank.ui.theme.StatusPending
import com.example.orenburggbank.viewmodel.WorkViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
fun CalendarScreen(viewModel: WorkViewModel, onWorkClick: (Long) -> Unit) {
    val works by viewModel.allWorks.collectAsState()
    var month by remember { mutableStateOf(YearMonth.now()) }
    var selected by remember { mutableStateOf(LocalDate.now()) }
    val days = (1..month.lengthOfMonth()).map { month.atDay(it) }
    val selectedWorks = works.filter { it.work.startDateTime.substringBefore(" ") == selected.toString() }
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton({ month = month.minusMonths(1); selected = month.atDay(1) }) { Icon(Icons.Default.ChevronLeft, "Предыдущий месяц", tint = MaterialTheme.colorScheme.primary) }
            Text(month.format(DateTimeFormatter.ofPattern("LLLL yyyy", Locale("ru"))).replaceFirstChar { it.uppercase() }, Modifier.weight(1f), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge)
            IconButton({ month = month.plusMonths(1); selected = month.atDay(1) }) { Icon(Icons.Default.ChevronRight, "Следующий месяц", tint = MaterialTheme.colorScheme.primary) }
        }
        LazyRow(contentPadding = PaddingValues(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(days) { day ->
                val dayWorks = works.filter { it.work.startDateTime.substringBefore(" ") == day.toString() }
                val dotColor = when {
                    dayWorks.any { it.work.status == "WORK_CANCELLED" } -> StatusCancelled
                    dayWorks.isNotEmpty() && dayWorks.all { it.work.status == "WORK_APPROVED" } -> StatusApproved
                    else -> StatusPending
                }
                Surface(
                    modifier = Modifier.size(52.dp).clickable { selected = day },
                    shape = CircleShape,
                    color = if (selected == day) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    border = if (day == LocalDate.now()) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(day.dayOfMonth.toString(), color = if (selected == day) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                        if (dayWorks.isNotEmpty()) Box(Modifier.size(5.dp).clip(CircleShape).background(dotColor))
                    }
                }
            }
        }
        Text("Работы на $selected", Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 80.dp)) {
            if (selectedWorks.isEmpty()) item { Text("Нет техработ", Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant) }
            items(selectedWorks) { item -> WorkCard(item) { onWorkClick(item.work.id) } }
        }
    }
}
