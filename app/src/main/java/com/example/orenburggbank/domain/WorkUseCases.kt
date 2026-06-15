/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.domain

import com.example.orenburggbank.data.WorkRepositoryContract
import com.example.orenburggbank.data.entity.*

/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
class WorkUseCases(
    private val repository: WorkRepositoryContract,
    val authorization: AuthorizationService
) {
    /** Creates a validated work request for the authenticated user. */
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    suspend fun create(user: UserSession, work: WorkEntity): AppResult<Long> =
        runOperation {
            authorization.require(user, Permission.CREATE_WORK)
            val errors = WorkValidator.validate(work, repository.allSystems.value.map { it.id }.toSet())
            require(errors.isEmpty()) { errors.joinToString("\n") }
            repository.createWork(user, work.copy(ownerUsername = user.username))
        }

    /** Updates a validated work request after ownership checks. */
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    suspend fun update(user: UserSession, work: WorkEntity): AppResult<Unit> = runOperation {
        authorization.requireCanEdit(user, work)
        val errors = WorkValidator.validate(work, repository.allSystems.value.map { it.id }.toSet())
        require(errors.isEmpty()) { errors.joinToString("\n") }
        repository.updateWork(user, work)
    }

    /** Cancels a work request after permission and ownership checks. */
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    suspend fun cancel(user: UserSession, workId: Long): AppResult<Unit> =
        runOperation { repository.cancelWork(user, workId) }

    /** Approves or rejects an approval assigned to the authenticated user. */
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    suspend fun setApprovalStatus(user: UserSession, workId: Long, approvalId: Long, status: String, comment: String = ""): AppResult<Unit> =
        runOperation { repository.updateApproverStatus(user, workId, approvalId, status, comment) }

    /** Delegates an assigned approval to its configured deputy. */
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    suspend fun createDelegation(user: UserSession, delegation: DelegationEntity): AppResult<Unit> =
        runOperation { repository.createDelegation(user, delegation) }

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    suspend fun deleteDelegation(user: UserSession, delegationId: Long): AppResult<Unit> =
        runOperation { repository.deleteDelegation(user, delegationId) }

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    suspend fun saveSystem(user: UserSession, system: SystemEntity): AppResult<Unit> =
        runOperation { repository.saveSystem(user, system) }

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    private suspend fun <T> runOperation(block: suspend () -> T): AppResult<T> = try {
        AppResult.Success(block())
    } catch (exception: Exception) {
        AppResult.Error(exception)
    }
}
