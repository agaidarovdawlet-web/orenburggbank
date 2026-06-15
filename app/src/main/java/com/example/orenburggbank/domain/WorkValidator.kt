/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.domain

import com.example.orenburggbank.data.entity.WorkEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
object WorkValidator {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun validate(work: WorkEntity, validSystemIds: Set<String>): List<String> {
        val errors = mutableListOf<String>()
        if (work.title.isBlank()) errors += "Название обязательно"
        if (work.engineerName.isBlank()) errors += "Инженер обязателен"
        if (work.description.isBlank()) errors += "Описание обязательно"
        if (work.rollbackPlan.isBlank()) errors += "План отката обязателен"
        if (work.affectedSystemIds.isEmpty() || work.affectedSystemIds.any { it !in validSystemIds }) errors += "Выберите затронутые системы из списка"
        try {
            val start = LocalDateTime.parse(work.startDateTime, formatter)
            val end = LocalDateTime.parse(work.endDateTime, formatter)
            if (!end.isAfter(start)) errors += "Дата и время окончания должны быть позже начала"
        } catch (_: Exception) {
            errors += "Дата и время должны соответствовать формату yyyy-MM-dd HH:mm"
        }
        return errors
    }
}
