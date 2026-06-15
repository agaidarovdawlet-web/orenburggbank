/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.ui.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.orenburggbank.ui.theme.StatusApproved

@Composable
/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
fun MatrixScreen() {
    val headers = listOf("Действие", "Согл.", "ЗамС", "Автор", "Просм.", "Админ", "Справ.")
    val rows = listOf(
        listOf("Согласование техработ", "●", "●", "", "", "●", ""),
        listOf("Перевод на зама", "●", "", "", "", "●", ""),
        listOf("Перевод с зама", "●", "", "", "", "●", ""),
        listOf("Просмотр техработ", "●", "●", "●", "●", "●", "●"),
        listOf("Создание техработ", "●", "●", "●", "", "●", ""),
        listOf("Отмена техработ", "●", "●", "●", "", "●", ""),
        listOf("Просмотр справочников", "●", "●", "●", "●", "●", "●"),
        listOf("Изменение справочников", "", "", "", "", "●", "●")
    )
    Column(Modifier.fillMaxSize().padding(16.dp).horizontalScroll(rememberScrollState())) {
        Text("Матрица ролей", style = MaterialTheme.typography.headlineMedium)
        (listOf(headers) + rows).forEachIndexed { index, row ->
            Row {
                row.forEachIndexed { column, cell ->
                    Surface(
                        modifier = Modifier.width(if (column == 0) 220.dp else 72.dp).padding(2.dp),
                        color = if (index > 0 && column > 0 && cell == "●") StatusApproved.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp)
                    ) {
                        Text(cell, modifier = Modifier.padding(8.dp), color = if (cell == "●") StatusApproved else MaterialTheme.colorScheme.onSurface, style = if (index == 0) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Divider()
        }
    }
}
