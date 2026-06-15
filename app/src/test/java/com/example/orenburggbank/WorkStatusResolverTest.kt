/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank

import com.example.orenburggbank.data.entity.*
import com.example.orenburggbank.domain.WorkStatusResolver
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
class WorkStatusResolverTest {
    /** Проверяет требование ТЗ: все согласовали — работа допущена. */
    @Test fun allApprovalsMakeWorkApproved() {
        assertEquals(WorkStatus.APPROVED, WorkStatusResolver.resolve(listOf(approval(ApprovalStatus.APPROVED), approval(ApprovalStatus.APPROVED))))
    }

    /** Проверяет требование ТЗ: любой отказ отменяет работу. */
    @Test fun anyRejectedMakesWorkCancelled() {
        assertEquals(WorkStatus.CANCELLED, WorkStatusResolver.resolve(listOf(approval(ApprovalStatus.APPROVED), approval(ApprovalStatus.REJECTED))))
    }

    /** Проверяет сохранение статуса ожидания до получения всех решений. */
    @Test fun pendingApprovalKeepsWorkPending() {
        assertEquals(WorkStatus.PENDING, WorkStatusResolver.resolve(listOf(approval(ApprovalStatus.APPROVED), approval(ApprovalStatus.PENDING))))
    }

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    private fun approval(status: String) = ApprovalEntity(workId = 1, roleName = "", personName = "", status = status, systemId = "abs", isDeputy = false, approverId = "approver")
}
