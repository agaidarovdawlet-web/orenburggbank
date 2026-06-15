/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.data

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabaseLockedException
import android.database.sqlite.SQLiteException
import com.example.orenburggbank.data.entity.*
import com.example.orenburggbank.domain.AuthorizationService
import com.example.orenburggbank.domain.Permission
import com.example.orenburggbank.domain.WorkStatusResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

/**
 * [РЕПОЗИТОРИЙ ПОРТАЛА ТЕХРАБОТ]
 *
 * Назначение: выполняет CRUD-операции SQLite и защищает каждую запись проверкой прав.
 * Входные данные: [DatabaseHelper] и единая политика [AuthorizationService].
 * Выходные данные: реактивные списки систем, работ и перенаправлений.
 * Роль в проекте: связывает Data-слой с UseCase, автоматически создаёт согласования
 * и вычисляет итоговый статус техработы согласно странице 1 ТЗ.
 *
 * Пример использования:
 * `repository.createWork(currentUser, work)`
 */
class WorkRepository(
    private val dbHelper: DatabaseHelper,
    private val authorization: AuthorizationService
) : WorkRepositoryContract {
    private val json = Json { ignoreUnknownKeys = true }
    private val _allWorks = MutableStateFlow<List<WorkWithApprovals>>(emptyList())
    override val allWorks = _allWorks.asStateFlow()
    private val _allSystems = MutableStateFlow<List<SystemEntity>>(emptyList())
    override val allSystems = _allSystems.asStateFlow()
    private val _allDelegations = MutableStateFlow<List<DelegationEntity>>(emptyList())
    override val allDelegations = _allDelegations.asStateFlow()

    /** Перечитывает данные SQLite в StateFlow после запуска или изменяющей операции. */
    override suspend fun refreshData() = withContext(Dispatchers.IO) {
        _allSystems.value = fetchAllSystems()
        _allDelegations.value = fetchAllDelegations()
        _allWorks.value = fetchAllWorks()
    }

    /** Создаёт работу и согласования затронутых/зависимых систем одной транзакцией. */
    override suspend fun createWork(user: UserSession, work: WorkEntity): Long = withContext(Dispatchers.IO) {
        authorization.require(user, Permission.CREATE_WORK)
        val db = dbHelper.writableDatabase
        transaction(db) {
            val normalizedSystems = work.affectedSystemIds.ifEmpty { listOf(work.systemId) }.distinct()
            val workId = db.insertOrThrow(DatabaseHelper.TABLE_WORKS, null, workValues(work.copy(
                ownerUsername = user.username,
                affectedSystemIds = normalizedSystems,
                status = WorkStatus.PENDING
            )))
            val approvalSystems = normalizedSystems.flatMap { id ->
                val system = fetchSystemById(id) ?: error("Система $id не найдена")
                listOf(id) + system.dependentSystemIds
            }.distinct()
            approvalSystems.forEach { systemId ->
                val system = fetchSystemById(systemId) ?: error("Система $systemId не найдена")
                insertApproval(db, workId, systemId, system.approverId, activeDelegation(systemId, system.approverId)?.deputyId)
            }
            insertApproval(db, workId, "department", "admin", null)
            workId
        }.also { refreshData() }
    }

    /** Обновляет работу после проверки права редактировать конкретную заявку. */
    override suspend fun updateWork(user: UserSession, work: WorkEntity) = withContext(Dispatchers.IO) {
        authorization.requireCanEdit(user, fetchWorkById(work.id) ?: error("Заявка не найдена"))
        try {
            requireUpdated(dbHelper.writableDatabase.update(
                DatabaseHelper.TABLE_WORKS, workValues(work), "${DatabaseHelper.COL_WORK_ID} = ?", arrayOf(work.id.toString())
            ))
        } catch (exception: SQLiteException) { throw storageException(exception) }
        refreshData()
    }

    /** Сохраняет решение согласующего и пересчитывает статус работы. */
    override suspend fun updateApproverStatus(user: UserSession, workId: Long, approvalId: Long, status: String, comment: String) = withContext(Dispatchers.IO) {
        require(status in setOf(ApprovalStatus.APPROVED, ApprovalStatus.REJECTED))
        val approval = fetchApprovalById(approvalId) ?: error("Согласование не найдено")
        authorization.requireCanApprove(user, approval, activeDelegation(approval.systemId, approval.approverId))
        val db = dbHelper.writableDatabase
        transaction(db) {
            requireUpdated(db.update(DatabaseHelper.TABLE_APPROVALS, ContentValues().apply {
                put(DatabaseHelper.COL_APP_STATUS, status)
                put(DatabaseHelper.COL_APP_COMMENT, comment)
                put(DatabaseHelper.COL_APP_APPROVED_AT, LocalDateTime.now().toString())
            }, "${DatabaseHelper.COL_APP_ID} = ?", arrayOf(approvalId.toString())))
            updateWorkStatus(db, workId)
        }
        refreshData()
    }

    /** Отменяет техработу при наличии права на одну из затронутых систем. */
    override suspend fun cancelWork(user: UserSession, workId: Long) = withContext(Dispatchers.IO) {
        authorization.requireCanCancelBySystem(user, fetchWorkById(workId) ?: error("Заявка не найдена"), fetchAllSystems(), fetchAllDelegations())
        requireUpdated(dbHelper.writableDatabase.update(DatabaseHelper.TABLE_WORKS, ContentValues().apply {
            put(DatabaseHelper.COL_WORK_STATUS, WorkStatus.CANCELLED)
        }, "${DatabaseHelper.COL_WORK_ID} = ?", arrayOf(workId.toString())))
        refreshData()
    }

    /** Создаёт период перенаправления согласований на заместителя. */
    override suspend fun createDelegation(user: UserSession, delegation: DelegationEntity) = withContext(Dispatchers.IO) {
        authorization.require(user, Permission.DELEGATE_APPROVER)
        if (user.role != AppRole.ADMIN && delegation.approverId != user.userId) throw SecurityException("Можно делегировать только свои согласования")
        require(delegation.reason.isNotBlank()) { "Укажите обоснование" }
        require(delegation.dateTo > delegation.dateFrom) { "Конец периода должен быть позже начала" }
        val system = fetchSystemById(delegation.systemId) ?: error("Система не найдена")
        require(system.approverId == delegation.approverId && system.deputyId == delegation.deputyId) { "Согласующий или заместитель не соответствует системе" }
        val db = dbHelper.writableDatabase
        transaction(db) {
            db.insertOrThrow(DatabaseHelper.TABLE_DELEGATIONS, null, delegationValues(delegation))
            db.update(DatabaseHelper.TABLE_APPROVALS, ContentValues().apply {
                put(DatabaseHelper.COL_APP_DELEGATED_TO, delegation.deputyId)
            }, "${DatabaseHelper.COL_APP_SYS_ID} = ? AND ${DatabaseHelper.COL_APP_APPROVER_ID} = ? AND ${DatabaseHelper.COL_APP_STATUS} = ?",
                arrayOf(delegation.systemId, delegation.approverId, ApprovalStatus.PENDING))
        }
        refreshData()
    }

    /** Завершает перенаправление и возвращает ожидающие согласования основному сотруднику. */
    override suspend fun deleteDelegation(user: UserSession, delegationId: Long) = withContext(Dispatchers.IO) {
        authorization.require(user, Permission.DELEGATE_APPROVER)
        val delegation = fetchDelegationById(delegationId) ?: error("Перенаправление не найдено")
        if (user.role != AppRole.ADMIN && delegation.approverId != user.userId) throw SecurityException("Нет доступа")
        val db = dbHelper.writableDatabase
        transaction(db) {
            db.delete(DatabaseHelper.TABLE_DELEGATIONS, "${DatabaseHelper.COL_DEL_ID} = ?", arrayOf(delegationId.toString()))
            db.update(DatabaseHelper.TABLE_APPROVALS, ContentValues().apply { putNull(DatabaseHelper.COL_APP_DELEGATED_TO) },
                "${DatabaseHelper.COL_APP_SYS_ID} = ? AND ${DatabaseHelper.COL_APP_APPROVER_ID} = ?",
                arrayOf(delegation.systemId, delegation.approverId))
        }
        refreshData()
    }

    /** Создаёт или редактирует запись справочника систем для ADMIN/MAINTAINER. */
    override suspend fun saveSystem(user: UserSession, system: SystemEntity) = withContext(Dispatchers.IO) {
        authorization.require(user, Permission.EDIT_SYSTEMS)
        require(system.id.isNotBlank() && system.name.isNotBlank() && system.description.isNotBlank()) { "Заполните обязательные поля системы" }
        require(system.id !in system.dependentSystemIds) { "Система не может зависеть от самой себя" }
        dbHelper.writableDatabase.insertWithOnConflict(
            DatabaseHelper.TABLE_SYSTEMS, null, systemValues(system), SQLiteDatabase.CONFLICT_REPLACE
        )
        refreshData()
    }

    /** Возвращает аудит действий, относящийся к указанной техработе. */
    override fun getHistoryForWork(workId: Long): List<HistoryEntity> {
        val cursor = dbHelper.readableDatabase.query(DatabaseHelper.TABLE_HISTORY, null, "${DatabaseHelper.COL_HIST_WORK_ID} = ?", arrayOf(workId.toString()), null, null, null)
        return cursor.use { generateSequence { if (it.moveToNext()) cursorToHistory(it) else null }.toList() }
    }

    private fun fetchAllSystems(): List<SystemEntity> = queryAll(DatabaseHelper.TABLE_SYSTEMS, ::cursorToSystem)
    private fun fetchAllDelegations(): List<DelegationEntity> = queryAll(DatabaseHelper.TABLE_DELEGATIONS, ::cursorToDelegation)
    private fun fetchAllWorks(): List<WorkWithApprovals> = queryAll(DatabaseHelper.TABLE_WORKS, ::cursorToWork).map { work ->
        WorkWithApprovals(work, fetchApprovalsForWork(work.id), fetchSystemById(work.systemId) ?: unknownSystem(work.systemId))
    }
    private fun fetchApprovalsForWork(workId: Long): List<ApprovalEntity> =
        query(DatabaseHelper.TABLE_APPROVALS, "${DatabaseHelper.COL_APP_WORK_ID} = ?", arrayOf(workId.toString()), ::cursorToApproval)
    private fun fetchSystemById(id: String) = queryOne(DatabaseHelper.TABLE_SYSTEMS, DatabaseHelper.COL_SYS_ID, id, ::cursorToSystem)
    private fun fetchWorkById(id: Long) = queryOne(DatabaseHelper.TABLE_WORKS, DatabaseHelper.COL_WORK_ID, id.toString(), ::cursorToWork)
    private fun fetchApprovalById(id: Long) = queryOne(DatabaseHelper.TABLE_APPROVALS, DatabaseHelper.COL_APP_ID, id.toString(), ::cursorToApproval)
    private fun fetchDelegationById(id: Long) = queryOne(DatabaseHelper.TABLE_DELEGATIONS, DatabaseHelper.COL_DEL_ID, id.toString(), ::cursorToDelegation)
    private fun activeDelegation(systemId: String, approverId: String): DelegationEntity? {
        val now = LocalDateTime.now().toString()
        return fetchAllDelegations().firstOrNull { it.systemId == systemId && it.approverId == approverId && now in it.dateFrom..it.dateTo }
    }

    private fun updateWorkStatus(db: SQLiteDatabase, workId: Long) {
        db.update(DatabaseHelper.TABLE_WORKS, ContentValues().apply {
            put(DatabaseHelper.COL_WORK_STATUS, WorkStatusResolver.resolve(fetchApprovalsForWork(workId)))
        }, "${DatabaseHelper.COL_WORK_ID} = ?", arrayOf(workId.toString()))
    }

    private fun insertApproval(db: SQLiteDatabase, workId: Long, systemId: String, approverId: String, deputyId: String?) {
        db.insertOrThrow(DatabaseHelper.TABLE_APPROVALS, null, ContentValues().apply {
            put(DatabaseHelper.COL_APP_WORK_ID, workId); put(DatabaseHelper.COL_APP_SYS_ID, systemId)
            put(DatabaseHelper.COL_APP_APPROVER_ID, approverId); put(DatabaseHelper.COL_APP_STATUS, ApprovalStatus.PENDING)
            if (deputyId == null) putNull(DatabaseHelper.COL_APP_DELEGATED_TO) else put(DatabaseHelper.COL_APP_DELEGATED_TO, deputyId)
            put(DatabaseHelper.COL_APP_COMMENT, "")
        })
    }

    private fun systemValues(s: SystemEntity) = ContentValues().apply {
        put(DatabaseHelper.COL_SYS_ID, s.id); put(DatabaseHelper.COL_SYS_NAME, s.name); put(DatabaseHelper.COL_SYS_DESC, s.description)
        put(DatabaseHelper.COL_SYS_APPROVER, s.approverId); put(DatabaseHelper.COL_SYS_DEPUTY, s.deputyId)
        put(DatabaseHelper.COL_SYS_LEVEL, s.criticality.name); put(DatabaseHelper.COL_SYS_DEPS, json.encodeToString(s.dependentSystemIds))
    }
    private fun workValues(w: WorkEntity) = ContentValues().apply {
        put(DatabaseHelper.COL_WORK_TITLE, w.title); put(DatabaseHelper.COL_WORK_DATE, w.date); put(DatabaseHelper.COL_WORK_START, w.timeStart)
        put(DatabaseHelper.COL_WORK_END, w.timeEnd); put(DatabaseHelper.COL_WORK_SYS_ID, w.systemId); put(DatabaseHelper.COL_WORK_ENGINEER, w.engineerName)
        put(DatabaseHelper.COL_WORK_DESC, w.description); put(DatabaseHelper.COL_WORK_ROLLBACK, w.rollbackPlan); put(DatabaseHelper.COL_WORK_STATUS, w.status)
        put(DatabaseHelper.COL_WORK_OWNER, w.ownerUsername); put(DatabaseHelper.COL_WORK_START_DT, w.startDateTime); put(DatabaseHelper.COL_WORK_END_DT, w.endDateTime)
        put(DatabaseHelper.COL_WORK_AFFECTED, json.encodeToString(w.affectedSystemIds))
    }
    private fun delegationValues(d: DelegationEntity) = ContentValues().apply {
        put(DatabaseHelper.COL_DEL_SYS_ID, d.systemId); put(DatabaseHelper.COL_DEL_APPROVER_ID, d.approverId); put(DatabaseHelper.COL_DEL_DEPUTY_ID, d.deputyId)
        put(DatabaseHelper.COL_DEL_FROM, d.dateFrom); put(DatabaseHelper.COL_DEL_TO, d.dateTo); put(DatabaseHelper.COL_DEL_REASON, d.reason); put(DatabaseHelper.COL_DEL_CREATED, d.createdAt)
    }

    private fun cursorToSystem(c: Cursor) = SystemEntity(c.string(DatabaseHelper.COL_SYS_ID), c.string(DatabaseHelper.COL_SYS_NAME), c.string(DatabaseHelper.COL_SYS_DESC),
        c.string(DatabaseHelper.COL_SYS_APPROVER), c.string(DatabaseHelper.COL_SYS_DEPUTY), Criticality.valueOf(c.string(DatabaseHelper.COL_SYS_LEVEL)),
        decodeList(c.string(DatabaseHelper.COL_SYS_DEPS)))
    private fun cursorToWork(c: Cursor) = WorkEntity(c.long(DatabaseHelper.COL_WORK_ID), c.string(DatabaseHelper.COL_WORK_TITLE), c.string(DatabaseHelper.COL_WORK_DATE),
        c.string(DatabaseHelper.COL_WORK_START), c.string(DatabaseHelper.COL_WORK_END), c.string(DatabaseHelper.COL_WORK_SYS_ID), c.string(DatabaseHelper.COL_WORK_ENGINEER),
        c.string(DatabaseHelper.COL_WORK_DESC), c.string(DatabaseHelper.COL_WORK_ROLLBACK), c.string(DatabaseHelper.COL_WORK_STATUS), c.string(DatabaseHelper.COL_WORK_OWNER),
        c.string(DatabaseHelper.COL_WORK_START_DT), c.string(DatabaseHelper.COL_WORK_END_DT), decodeList(c.string(DatabaseHelper.COL_WORK_AFFECTED)))
    private fun cursorToApproval(c: Cursor) = ApprovalEntity(c.long(DatabaseHelper.COL_APP_ID), c.long(DatabaseHelper.COL_APP_WORK_ID),
        "Согласующий ${c.string(DatabaseHelper.COL_APP_SYS_ID)}", c.string(DatabaseHelper.COL_APP_APPROVER_ID), c.string(DatabaseHelper.COL_APP_STATUS),
        c.string(DatabaseHelper.COL_APP_SYS_ID), c.nullable(DatabaseHelper.COL_APP_DELEGATED_TO) != null, c.nullable(DatabaseHelper.COL_APP_DELEGATED_TO) ?: c.string(DatabaseHelper.COL_APP_APPROVER_ID),
        c.string(DatabaseHelper.COL_APP_APPROVER_ID), c.nullable(DatabaseHelper.COL_APP_DELEGATED_TO), c.string(DatabaseHelper.COL_APP_COMMENT), c.nullable(DatabaseHelper.COL_APP_APPROVED_AT))
    private fun cursorToDelegation(c: Cursor) = DelegationEntity(c.long(DatabaseHelper.COL_DEL_ID), c.string(DatabaseHelper.COL_DEL_SYS_ID), c.string(DatabaseHelper.COL_DEL_APPROVER_ID),
        c.string(DatabaseHelper.COL_DEL_DEPUTY_ID), c.string(DatabaseHelper.COL_DEL_FROM), c.string(DatabaseHelper.COL_DEL_TO), c.string(DatabaseHelper.COL_DEL_REASON), c.string(DatabaseHelper.COL_DEL_CREATED))
    private fun cursorToHistory(c: Cursor) = HistoryEntity(c.long(DatabaseHelper.COL_HIST_ID), c.long(DatabaseHelper.COL_HIST_WORK_ID), c.string(DatabaseHelper.COL_HIST_ACTION),
        c.string(DatabaseHelper.COL_HIST_PERIOD), c.string(DatabaseHelper.COL_HIST_REASON), c.string(DatabaseHelper.COL_HIST_DATE))

    private fun <T> queryAll(table: String, mapper: (Cursor) -> T) = query(table, null, null, mapper)
    private fun <T> query(table: String, selection: String?, args: Array<String>?, mapper: (Cursor) -> T): List<T> =
        dbHelper.readableDatabase.query(table, null, selection, args, null, null, null).use { c ->
            buildList { while (c.moveToNext()) add(mapper(c)) }
        }
    private fun <T> queryOne(table: String, column: String, value: String, mapper: (Cursor) -> T): T? =
        query(table, "$column = ?", arrayOf(value), mapper).firstOrNull()
    private fun decodeList(value: String): List<String> = runCatching { json.decodeFromString<List<String>>(value) }.getOrDefault(emptyList())
    private fun unknownSystem(id: String) = SystemEntity(id, "Неизвестная система", "", "", "", Criticality.LOW, emptyList())
    private fun Cursor.string(column: String) = getString(getColumnIndexOrThrow(column)) ?: ""
    private fun Cursor.nullable(column: String) = getString(getColumnIndexOrThrow(column))
    private fun Cursor.long(column: String) = getLong(getColumnIndexOrThrow(column))
    private fun requireUpdated(rows: Int) = check(rows == 1) { "Запись не найдена" }
    private fun storageException(e: SQLiteException) = IllegalStateException(if (e is SQLiteDatabaseLockedException) "База данных занята" else "Ошибка локальной базы данных", e)
    private inline fun <T> transaction(db: SQLiteDatabase, block: () -> T): T {
        db.beginTransaction()
        return try { block().also { db.setTransactionSuccessful() } } catch (e: SQLiteException) { throw storageException(e) } finally { db.endTransaction() }
    }
}
