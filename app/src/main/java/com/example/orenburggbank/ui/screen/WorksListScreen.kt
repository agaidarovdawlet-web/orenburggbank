/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.orenburggbank.domain.Permission
import com.example.orenburggbank.ui.components.GradientHeader
import com.example.orenburggbank.ui.components.WorkCard
import com.example.orenburggbank.viewmodel.WorkViewModel
import com.example.orenburggbank.ui.components.StatusBadge

@OptIn(androidx.compose.material.ExperimentalMaterialApi::class)
@Composable
/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
fun WorksListScreen(viewModel: WorkViewModel, onCreateWork: () -> Unit, onWorkClick: (Long) -> Unit) {
    val works by viewModel.filteredWorks.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val refreshing by viewModel.isLoading.collectAsState()
    val pullState = rememberPullRefreshState(refreshing, viewModel::refresh)
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedWorkId by remember { mutableStateOf<Long?>(null) }
    val tabs = listOf("Все", "На согласовании", "Согласовано", "Отменено")
    val filtered = when (selectedTab) {
        1 -> works.filter { it.work.status in setOf("pending", "PENDING") }
        2 -> works.filter { it.work.status in setOf("approved", "WORK_APPROVED") }
        3 -> works.filter { it.work.status in setOf("cancelled", "WORK_CANCELLED") }
        else -> works
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (viewModel.hasPermission(Permission.CREATE_WORK)) {
                FloatingActionButton(onClick = onCreateWork, containerColor = MaterialTheme.colorScheme.primary) {
                    Icon(Icons.Default.Add, "Создать техработу")
                }
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background)) {
            GradientHeader("Техработы", "Планирование и согласование")
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    query, viewModel::updateSearchQuery, modifier = Modifier.weight(1f),
                    placeholder = { Text("Поиск") }, leadingIcon = { Icon(Icons.Default.Search, "Поиск") }, singleLine = true
                )
                IconButton(onClick = viewModel::refresh, modifier = Modifier.size(48.dp)) { Icon(Icons.Default.Refresh, "Обновить список", tint = MaterialTheme.colorScheme.primary) }
            }
            ScrollableTabRow(selectedTab, edgePadding = 16.dp, containerColor = MaterialTheme.colorScheme.background, divider = {}) {
                tabs.forEachIndexed { index, title -> Tab(selectedTab == index, { selectedTab = index }, text = { Text(title) }) }
            }
            BoxWithConstraints(Modifier.fillMaxSize().pullRefresh(pullState)) {
                val columns = if (maxWidth >= 700.dp) 2 else 1
                if (filtered.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Заявки не найдены", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                } else if (maxWidth >= 1000.dp) {
                    val selected = filtered.firstOrNull { it.work.id == selectedWorkId } ?: filtered.first()
                    Row(Modifier.fillMaxSize()) {
                        LazyColumn(Modifier.weight(0.45f), contentPadding = PaddingValues(top = 10.dp, bottom = 88.dp)) {
                            items(filtered, key = { it.work.id }) { work -> WorkCard(work) { selectedWorkId = work.work.id } }
                        }
                        Card(Modifier.weight(0.55f).fillMaxHeight().padding(16.dp), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                StatusBadge(selected.work.status)
                                Text(selected.work.title, style = MaterialTheme.typography.headlineMedium)
                                Text(selected.system.name, color = MaterialTheme.colorScheme.primary)
                                Text("${selected.work.startDateTime} — ${selected.work.endDateTime}", style = MaterialTheme.typography.labelSmall)
                                Text(selected.work.description)
                                Button({ onWorkClick(selected.work.id) }) { Text("Открыть детали") }
                            }
                        }
                    }
                } else {
                    LazyVerticalGrid(GridCells.Fixed(columns), contentPadding = PaddingValues(top = 10.dp, bottom = 88.dp)) {
                        itemsIndexed(filtered, key = { _, item -> item.work.id }) { index, work -> WorkCard(work, index * 45) { onWorkClick(work.work.id) } }
                    }
                }
                PullRefreshIndicator(refreshing, pullState, Modifier.align(Alignment.TopCenter), contentColor = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
