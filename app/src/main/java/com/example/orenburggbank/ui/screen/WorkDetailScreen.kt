/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.orenburggbank.data.entity.ApprovalEntity
import com.example.orenburggbank.domain.Permission
import com.example.orenburggbank.ui.components.StatusBadge
import com.example.orenburggbank.ui.theme.*
import com.example.orenburggbank.viewmodel.WorkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
fun WorkDetailScreen(workId: Long, viewModel: WorkViewModel, onBack: () -> Unit, onEdit: (Long) -> Unit) {
    val works by viewModel.allWorks.collectAsState()
    val item = works.find { it.work.id == workId }
    if (item == null) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Работа не найдена") }; return }
    val statusColor = when (item.work.status) { "WORK_APPROVED" -> StatusApproved; "WORK_CANCELLED" -> StatusCancelled; else -> StatusPending }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Детали техработы") }, navigationIcon = {
                IconButton(onBack) { Icon(Icons.Default.ArrowBack, "Назад") }
            }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface))
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.1f)), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatusBadge(item.work.status)
                    Text(item.work.title, style = MaterialTheme.typography.headlineMedium)
                    DetailRow(Icons.Default.CalendarToday, "Период", "${item.work.startDateTime} — ${item.work.endDateTime}")
                    DetailRow(Icons.Default.Settings, "Системы", item.work.affectedSystemIds.joinToString())
                    DetailRow(Icons.Default.Person, "Инженер", item.work.engineerName)
                    DetailRow(Icons.Default.Info, "Описание", item.work.description)
                    DetailRow(Icons.Default.Replay, "План отката", item.work.rollbackPlan)
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (viewModel.canEdit(item.work)) OutlinedButton({ onEdit(workId) }, Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) { Icon(Icons.Default.Edit, null); Spacer(Modifier.width(6.dp)); Text("Изменить") }
                if (item.work.status in setOf("PENDING", "pending")) OutlinedButton({ viewModel.cancelWork(workId) }, Modifier.weight(1f), enabled = viewModel.canCancel(item.work), colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusCancelled), shape = RoundedCornerShape(8.dp)) { Text("Отменить") }
            }
            Text("Согласования", style = MaterialTheme.typography.titleLarge)
            item.approvals.forEach { approval ->
                ApprovalCard(approval, viewModel.canApprove(approval), viewModel.canApprove(approval) && viewModel.hasPermission(Permission.DELEGATE_APPROVER),
                    { viewModel.approveWork(workId, approval.id) }, { viewModel.rejectWork(workId, approval.id) },
                    { viewModel.delegateApprover(workId, approval.id, item.system.deputyId, "Плановое отсутствие") },
                    { viewModel.returnFromDelegate(workId, approval.id, item.system.approverId) })
            }
        }
    }
}

@Composable private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, label, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
        Column(Modifier.padding(start = 12.dp)) { Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(value) }
    }
}

@Composable private fun ApprovalCard(a: ApprovalEntity, canApprove: Boolean, canDelegate: Boolean, approve: () -> Unit, reject: () -> Unit, delegate: () -> Unit, returnBack: () -> Unit) {
    val color = when (a.status) { "APPROVED" -> StatusApproved; "REJECTED" -> StatusCancelled; else -> StatusPending }
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(44.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)), contentAlignment = Alignment.Center) {
                    Text(a.personName.take(2).uppercase(), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                Column(Modifier.padding(start = 12.dp).weight(1f)) { Text(a.roleName, fontWeight = FontWeight.Medium); Text(a.personName, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                Icon(when (a.status) { "APPROVED" -> Icons.Default.CheckCircle; "REJECTED" -> Icons.Default.Cancel; else -> Icons.Default.Schedule }, a.status, tint = color)
            }
            if (a.status == "PENDING") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(reject, Modifier.weight(1f), enabled = canApprove, colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusCancelled)) { Text("Отклонить") }
                    OutlinedButton(approve, Modifier.weight(1f), enabled = canApprove) { Text("Согласовать") }
                }
                OutlinedButton(if (a.isDeputy) returnBack else delegate, Modifier.fillMaxWidth(), enabled = canDelegate) { Text(if (a.isDeputy) "Вернуть основному" else "Перенаправить") }
            }
        }
    }
}
