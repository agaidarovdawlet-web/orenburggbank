/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.domain

import com.example.orenburggbank.data.entity.ApprovalEntity
import com.example.orenburggbank.data.entity.ApprovalStatus
import com.example.orenburggbank.data.entity.WorkStatus

/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
object WorkStatusResolver {
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun resolve(approvals: List<ApprovalEntity>): String = when {
        approvals.any { it.status == ApprovalStatus.REJECTED } -> WorkStatus.CANCELLED
        approvals.isNotEmpty() && approvals.all { it.status == ApprovalStatus.APPROVED } -> WorkStatus.APPROVED
        else -> WorkStatus.PENDING
    }
}
