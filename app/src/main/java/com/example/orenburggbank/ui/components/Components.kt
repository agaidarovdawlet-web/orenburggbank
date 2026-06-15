/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.ui.components

import androidx.compose.foundation.background
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.orenburggbank.data.entity.WorkWithApprovals
import com.example.orenburggbank.ui.theme.*
import kotlinx.coroutines.delay

@Composable
/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
fun GradientHeader(title: String, subtitle: String = "") {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BankSecondary, BankPrimary)
                )
            )
            .padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 32.dp)
    ) {
        Column {
            Text(
                text = title,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
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
fun BankLogo(
    modifier: Modifier = Modifier,
    showText: Boolean = true,
    light: Boolean = false
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            modifier = Modifier.size(52.dp).rotate(45f).clip(RoundedCornerShape(10.dp)).background(if (light) Color.White else BankPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text("О", modifier = Modifier.rotate(-45f), color = if (light) BankPrimary else Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
        }
        if (showText) Text("БАНК ОРЕНБУРГ", color = if (light) Color.White else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
    }
}

@Composable
/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
fun StatusBadge(status: String) {
    val (color, text) = when (status) {
        "approved", "APPROVED", "WORK_APPROVED" -> StatusApproved to "Согласовано"
        "cancelled", "REJECTED", "WORK_CANCELLED" -> StatusCancelled to "Отменено"
        else -> StatusPending to "На согласовании"
    }
    
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = text,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 6.dp)
            )
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
fun LevelBadge(level: String, colorName: String) {
    val color = when (colorName) {
        "red" -> CriticalRed
        "orange" -> HighOrange
        "amber" -> MediumAmber
        else -> LowEmerald
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Text(
            text = level.uppercase(),
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
fun WorkCard(
    workWithApprovals: WorkWithApprovals,
    entryDelayMillis: Int = 0,
    onClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(workWithApprovals.work.id) {
        delay(entryDelayMillis.toLong())
        visible = true
    }
    AnimatedVisibility(visible, enter = fadeIn(tween(250)) + slideInVertically(tween(250)) { it / 4 }) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(workWithApprovals.work.status)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = workWithApprovals.work.date,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Text(
                text = workWithApprovals.work.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 12.dp)
            )
            
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val criticalityColor = when (workWithApprovals.system.criticality.name) {
                    "CRITICAL" -> CriticalRed
                    "HIGH" -> HighOrange
                    "MEDIUM" -> MediumAmber
                    else -> LowEmerald
                }
                Box(Modifier.size(9.dp).clip(CircleShape).background(criticalityColor))
                Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = workWithApprovals.system.name,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                
                Text(
                    text = " • ",
                    color = MaterialTheme.colorScheme.outline
                )
                
                Text(
                    text = workWithApprovals.work.engineerName,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            val approvedCount = workWithApprovals.approvals.count { it.status in setOf("approved", "APPROVED") }
            val totalCount = workWithApprovals.approvals.size
            
            if (totalCount > 0) {
                LinearProgressIndicator(
                    progress = approvedCount.toFloat() / totalCount,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(4.dp)
                        .clip(CircleShape),
                    color = if (workWithApprovals.work.status in setOf("cancelled", "WORK_CANCELLED")) StatusCancelled else StatusApproved,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                )
                Text(
                    text = "Согласовано: $approvedCount из $totalCount",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
    }
}
