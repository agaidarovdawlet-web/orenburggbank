/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.domain

import com.example.orenburggbank.data.entity.AppRole
import com.example.orenburggbank.data.entity.ApprovalEntity
import com.example.orenburggbank.data.entity.UserSession
import com.example.orenburggbank.data.entity.WorkEntity
import com.example.orenburggbank.data.entity.DelegationEntity
import com.example.orenburggbank.data.entity.SystemEntity
import java.time.LocalDateTime

/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
enum class Permission {
    CREATE_WORK, EDIT_WORK, CANCEL_WORK, APPROVE_WORK, DELEGATE_APPROVER, EDIT_SYSTEMS, MANAGE_ROLES
}

/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
class AuthorizationService {
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun hasPermission(user: UserSession, permission: Permission): Boolean = when (permission) {
        Permission.CREATE_WORK -> user.role in setOf(AppRole.ADMIN, AppRole.TECHNICIAN, AppRole.AUTHOR, AppRole.APPROVER, AppRole.DEPUTY)
        Permission.EDIT_WORK -> user.role in setOf(AppRole.ADMIN, AppRole.TECHNICIAN, AppRole.AUTHOR)
        Permission.CANCEL_WORK -> user.role in setOf(AppRole.ADMIN, AppRole.TECHNICIAN, AppRole.AUTHOR, AppRole.APPROVER, AppRole.DEPUTY)
        Permission.APPROVE_WORK -> user.role in setOf(AppRole.ADMIN, AppRole.APPROVER, AppRole.DEPUTY)
        Permission.DELEGATE_APPROVER -> user.role in setOf(AppRole.ADMIN, AppRole.APPROVER)
        Permission.EDIT_SYSTEMS -> user.role in setOf(AppRole.ADMIN, AppRole.MAINTAINER)
        Permission.MANAGE_ROLES -> user.role == AppRole.ADMIN
    }

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun require(user: UserSession, permission: Permission) {
        if (!hasPermission(user, permission)) {
            throw SecurityException("У роли ${user.role.displayName} нет права: ${permission.name}")
        }
    }

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun requireCanCancel(user: UserSession, work: WorkEntity) {
        require(user, Permission.CANCEL_WORK)
        if (user.role !in setOf(AppRole.ADMIN, AppRole.APPROVER, AppRole.DEPUTY) && work.ownerUsername != user.username) {
            throw SecurityException("Можно отменять только свои заявки")
        }
    }

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun requireCanCancelBySystem(user: UserSession, work: WorkEntity, systems: List<SystemEntity>, delegations: List<DelegationEntity>) {
        require(user, Permission.CANCEL_WORK)
        val affected = systems.filter { it.id in work.affectedSystemIds }
        val allowed = when (user.role) {
            AppRole.ADMIN -> true
            AppRole.AUTHOR, AppRole.TECHNICIAN -> work.ownerUsername == user.username
            AppRole.APPROVER -> affected.any { it.approverId == user.userId }
            AppRole.DEPUTY -> affected.any { system ->
                delegations.any { it.systemId == system.id && it.deputyId == user.userId && LocalDateTime.now().toString() in it.dateFrom..it.dateTo }
            }
            else -> false
        }
        if (!allowed) throw SecurityException("Нет права отмены для затронутых систем")
    }

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun requireCanEdit(user: UserSession, work: WorkEntity) {
        require(user, Permission.EDIT_WORK)
        if (user.role != AppRole.ADMIN && work.ownerUsername != user.username) {
            throw SecurityException("Можно редактировать только свои заявки")
        }
    }

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun requireCanApprove(user: UserSession, approval: ApprovalEntity, activeDelegation: DelegationEntity? = null) {
        require(user, Permission.APPROVE_WORK)
        val canApprove = when (user.role) {
            AppRole.ADMIN -> true
            AppRole.APPROVER -> approval.approverId == user.userId
            AppRole.DEPUTY -> activeDelegation?.deputyId == user.userId &&
                activeDelegation.systemId == approval.systemId &&
                LocalDateTime.now().toString() in activeDelegation.dateFrom..activeDelegation.dateTo
            else -> false
        }
        if (!canApprove) {
            throw SecurityException("Можно согласовывать только назначенные вам заявки")
        }
    }
}
