/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
fun RolesScreen() {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Роли и пользователи", style = MaterialTheme.typography.headlineMedium)
        Text("Изменение ролей доступно только администратору. В демонстрационной версии учётные записи заданы в коде.")
        listOf(
            "admin" to "Администратор",
            "technician" to "Техник",
            "approver" to "Согласующий",
            "deputy" to "Заместитель",
            "author" to "Автор",
            "maintainer" to "Ведение справочников",
            "viewer" to "Зритель"
        ).forEach { (login, role) ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(login)
                    Text(role, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
