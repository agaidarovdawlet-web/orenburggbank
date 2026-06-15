/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.data

import com.example.orenburggbank.data.entity.*
import kotlinx.coroutines.flow.StateFlow

/**
 * [КОНТРАКТ РЕПОЗИТОРИЯ]
 *
 * Назначение: отделяет Domain/Presentation от конкретной SQLite-реализации.
 * Входные данные: пользовательская сессия и сущности предметной области.
 * Выходные данные: StateFlow для чтения и suspend-операции для изменений.
 * Роль в проекте: обеспечивает Clean Architecture и тестируемость UseCase.
 *
 * Пример использования:
 * `class WorkUseCases(private val repository: WorkRepositoryContract)`
 */
interface WorkRepositoryContract {
    val allWorks: StateFlow<List<WorkWithApprovals>>
    val allSystems: StateFlow<List<SystemEntity>>
    val allDelegations: StateFlow<List<DelegationEntity>>
    /** Reloads all locally stored systems and work requests. */
    suspend fun refreshData()
    /** Creates a work request and its approvals atomically. */
    suspend fun createWork(user: UserSession, work: WorkEntity): Long
    /** Updates a work request after ownership checks. */
    suspend fun updateWork(user: UserSession, work: WorkEntity)
    /** Changes an approval status after assignment checks. */
    suspend fun updateApproverStatus(user: UserSession, workId: Long, approvalId: Long, status: String, comment: String = "")
    /** Cancels a work after ownership checks. */
    suspend fun cancelWork(user: UserSession, workId: Long)
    /** Delegates an assigned approval and records audit history. */
    suspend fun createDelegation(user: UserSession, delegation: DelegationEntity)
    /** Removes a delegation and returns pending approvals to the approver. */
    suspend fun deleteDelegation(user: UserSession, delegationId: Long)
    /** Creates or updates a system dictionary entry. */
    suspend fun saveSystem(user: UserSession, system: SystemEntity)
    /** Returns audit history for a work request. */
    fun getHistoryForWork(workId: Long): List<HistoryEntity>
}
