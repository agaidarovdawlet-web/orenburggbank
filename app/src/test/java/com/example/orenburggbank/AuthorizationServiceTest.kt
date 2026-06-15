/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank

import com.example.orenburggbank.data.entity.*
import com.example.orenburggbank.domain.AuthorizationService
import com.example.orenburggbank.domain.Permission
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
class AuthorizationServiceTest {
    private val service = AuthorizationService()

    /** Проверяет матрицу ролей со страницы 2 ТЗ для каждой роли и каждого действия. */
    @Test
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun permissionMatrixMatchesSpecification() {
        val expected = mapOf(
            AppRole.ADMIN to Permission.entries.toSet(),
            AppRole.TECHNICIAN to setOf(Permission.CREATE_WORK, Permission.EDIT_WORK, Permission.CANCEL_WORK),
            AppRole.APPROVER to setOf(Permission.CREATE_WORK, Permission.CANCEL_WORK, Permission.APPROVE_WORK, Permission.DELEGATE_APPROVER),
            AppRole.DEPUTY to setOf(Permission.CREATE_WORK, Permission.CANCEL_WORK, Permission.APPROVE_WORK),
            AppRole.AUTHOR to setOf(Permission.CREATE_WORK, Permission.EDIT_WORK, Permission.CANCEL_WORK),
            AppRole.VIEWER to emptySet(),
            AppRole.MAINTAINER to setOf(Permission.EDIT_SYSTEMS)
        )
        AppRole.entries.forEach { role -> Permission.entries.forEach { permission ->
            assertEquals("$role x $permission", permission in expected.getValue(role), service.hasPermission(UserSession(role.name.lowercase(), role), permission))
        } }
    }

    /** Проверяет требование ТЗ: роль VIEWER имеет только право просмотра. */
    @Test
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun viewerCannotCreateOrApprove() {
        val viewer = UserSession("viewer", AppRole.VIEWER)
        assertThrows(SecurityException::class.java) { service.require(viewer, Permission.CREATE_WORK) }
        assertThrows(SecurityException::class.java) { service.requireCanApprove(viewer, approval("approver")) }
    }

    /** Проверяет требование ТЗ: согласующий работает только с назначенной системой. */
    @Test
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun approverCannotApproveAnotherSystem() {
        assertThrows(SecurityException::class.java) {
            service.requireCanApprove(UserSession("other", AppRole.APPROVER), approval("approver"))
        }
    }

    /** Проверяет требование ТЗ: заместитель получает право только в период делегирования. */
    @Test
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun deputyCanApproveOnlyDuringActiveDelegation() {
        val deputy = UserSession("deputy", AppRole.DEPUTY)
        val approval = approval("approver")
        assertThrows(SecurityException::class.java) { service.requireCanApprove(deputy, approval, null) }
        service.requireCanApprove(deputy, approval, activeDelegation())
    }

    /** Проверяет требование ТЗ: автор может отменять только собственные техработы. */
    @Test
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun authorCancelsOnlyOwnWork() {
        val author = UserSession("author", AppRole.AUTHOR)
        service.requireCanCancelBySystem(author, work("author"), listOf(system()), emptyList())
        assertThrows(SecurityException::class.java) { service.requireCanCancelBySystem(author, work("other"), listOf(system()), emptyList()) }
    }

    /** Проверяет требование ТЗ: отмена согласующим ограничена его системами. */
    @Test
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun approverCancellationIsLimitedBySystems() {
        service.requireCanCancelBySystem(UserSession("approver", AppRole.APPROVER), work("other"), listOf(system()), emptyList())
        assertThrows(SecurityException::class.java) {
            service.requireCanCancelBySystem(UserSession("other", AppRole.APPROVER), work("other"), listOf(system()), emptyList())
        }
    }

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    private fun system() = SystemEntity("abs", "АБС", "", "approver", "deputy", Criticality.CRITICAL, emptyList())
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    private fun work(owner: String) = WorkEntity(1, "Работа", "2026-06-15", "10:00", "11:00", "abs", "Инженер", "Описание", "Откат", WorkStatus.PENDING, owner, affectedSystemIds = listOf("abs"))
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    private fun approval(approver: String) = ApprovalEntity(1, 1, "Согласующий", approver, ApprovalStatus.PENDING, "abs", false, approver, approver)
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    private fun activeDelegation() = DelegationEntity(1, "abs", "approver", "deputy", LocalDateTime.now().minusDays(1).toString(), LocalDateTime.now().plusDays(1).toString(), "Отпуск", LocalDateTime.now().toString())
}
