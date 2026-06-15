/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.orenburggbank.ui.components.BankLogo

/**
 * [ЭКРАН «О ПРИЛОЖЕНИИ»]
 *
 * Назначение: показывает сведения о проекте, версии и авторе практики.
 * Входные данные: отсутствуют.
 * Выходные данные: Material 3 экран со справочной информацией.
 * Роль в проекте: фиксирует авторство и контекст производственной практики.
 *
 * Пример использования:
 * `AboutScreen()`
 */
@Composable
fun AboutScreen() {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier.widthIn(max = 520.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                BankLogo(showText = false)
                Text("Портал техработ", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text("Версия 3.0.0", style = MaterialTheme.typography.labelLarge)
                Divider()
                Text("Автор: Агайдаров Даулет Азаматович", textAlign = TextAlign.Center)
                Text("Студент группы 23 КСК 4", textAlign = TextAlign.Center)
                Text("Университетский колледж ОГУ", textAlign = TextAlign.Center)
                Text("Производственная практика в АО «БАНК ОРЕНБУРГ»", textAlign = TextAlign.Center)
                Text("2026", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
            }
        }
    }
}
