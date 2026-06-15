/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.data.entity

/** Уровень критичности банковской системы из страницы 1 ТЗ. */
enum class Criticality(val displayName: String, val colorName: String) {
    CRITICAL("Критический", "red"),
    HIGH("Высокий", "orange"),
    MEDIUM("Средний", "amber"),
    LOW("Низкий", "emerald")
}

/** Допустимые агрегированные статусы технической работы. */
object WorkStatus {
    const val PENDING = "PENDING"
    const val APPROVED = "WORK_APPROVED"
    const val CANCELLED = "WORK_CANCELLED"
}

/** Допустимые решения отдельного согласующего. */
object ApprovalStatus {
    const val PENDING = "PENDING"
    const val APPROVED = "APPROVED"
    const val REJECTED = "REJECTED"
}

/** Сущность справочника систем: назначение, ответственные, критичность и зависимости. */
data class SystemEntity(
    val id: String,
    val name: String,
    val description: String,
    val approverId: String,
    val deputyId: String,
    val criticality: Criticality,
    val dependentSystemIds: List<String>
) {
    val approverName get() = approverId
    val deputyName get() = deputyId
    val level get() = criticality.displayName
    val levelColor get() = criticality.colorName
    val dependencies get() = dependentSystemIds.joinToString(",")
}

/** Полное описание технической работы и затронутых систем. */
data class WorkEntity(
    val id: Long = 0,
    val title: String,
    val date: String,
    val timeStart: String,
    val timeEnd: String,
    val systemId: String,
    val engineerName: String,
    val description: String,
    val rollbackPlan: String,
    val status: String,
    val ownerUsername: String = "",
    val startDateTime: String = "$date $timeStart",
    val endDateTime: String = "$date $timeEnd",
    val affectedSystemIds: List<String> = listOf(systemId)
)

/** Решение согласующего конкретной системы по конкретной техработе. */
data class ApprovalEntity(
    val id: Long = 0,
    val workId: Long,
    val roleName: String,
    val personName: String,
    val status: String,
    val systemId: String,
    val isDeputy: Boolean,
    val assignedUsername: String = "",
    val approverId: String = assignedUsername,
    val delegatedToId: String? = null,
    val comment: String = "",
    val approvedAt: String? = null
) {
    val effectiveApproverId get() = delegatedToId ?: approverId
}

/** Период перенаправления полномочий согласующего заместителю. */
data class DelegationEntity(
    val id: Long = 0,
    val systemId: String,
    val approverId: String,
    val deputyId: String,
    val dateFrom: String,
    val dateTo: String,
    val reason: String,
    val createdAt: String
)

/** Неизменяемая запись аудита действий по техработе. */
data class HistoryEntity(
    val id: Long = 0,
    val workId: Long,
    val action: String,
    val period: String,
    val reason: String,
    val date: String
)

/** Агрегат для UI: работа, её согласования и основная система. */
data class WorkWithApprovals(
    val work: WorkEntity,
    val approvals: List<ApprovalEntity>,
    val system: SystemEntity
)
