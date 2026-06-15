/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * [ЛОКАЛЬНАЯ БАЗА ДАННЫХ]
 *
 * Назначение: создаёт, открывает и последовательно мигрирует SQLite-базу портала.
 * Входные данные: Android [Context], необходимый платформенному SQLiteOpenHelper.
 * Выходные данные: подключение к БД версии 3 с таблицами систем, техработ,
 * согласований, перенаправлений и истории.
 * Роль в проекте: реализует хранение справочника систем и техработ из страницы 1 ТЗ.
 *
 * Пример использования:
 * `val database = DatabaseHelper(context).writableDatabase`
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "orenburggbank.db"
        private const val DATABASE_VERSION = 3

        const val TABLE_SYSTEMS = "systems"
        const val COL_SYS_ID = "id"
        const val COL_SYS_NAME = "name"
        const val COL_SYS_DESC = "description"
        const val COL_SYS_APPROVER = "approverId"
        const val COL_SYS_DEPUTY = "deputyId"
        const val COL_SYS_LEVEL = "criticality"
        const val COL_SYS_DEPS = "dependentSystemIds"

        const val TABLE_WORKS = "works"
        const val COL_WORK_ID = "id"
        const val COL_WORK_TITLE = "title"
        const val COL_WORK_DATE = "date"
        const val COL_WORK_START = "timeStart"
        const val COL_WORK_END = "timeEnd"
        const val COL_WORK_SYS_ID = "systemId"
        const val COL_WORK_ENGINEER = "engineerName"
        const val COL_WORK_DESC = "description"
        const val COL_WORK_ROLLBACK = "rollbackPlan"
        const val COL_WORK_STATUS = "status"
        const val COL_WORK_OWNER = "ownerUsername"
        const val COL_WORK_START_DT = "startDateTime"
        const val COL_WORK_END_DT = "endDateTime"
        const val COL_WORK_AFFECTED = "affectedSystemIds"

        const val TABLE_APPROVALS = "approvals"
        const val COL_APP_ID = "id"
        const val COL_APP_WORK_ID = "workId"
        const val COL_APP_SYS_ID = "systemId"
        const val COL_APP_APPROVER_ID = "approverId"
        const val COL_APP_STATUS = "status"
        const val COL_APP_DELEGATED_TO = "delegatedToId"
        const val COL_APP_COMMENT = "comment"
        const val COL_APP_APPROVED_AT = "approvedAt"

        const val TABLE_DELEGATIONS = "delegations"
        const val COL_DEL_ID = "id"
        const val COL_DEL_SYS_ID = "systemId"
        const val COL_DEL_APPROVER_ID = "approverId"
        const val COL_DEL_DEPUTY_ID = "deputyId"
        const val COL_DEL_FROM = "dateFrom"
        const val COL_DEL_TO = "dateTo"
        const val COL_DEL_REASON = "reason"
        const val COL_DEL_CREATED = "createdAt"

        const val TABLE_HISTORY = "history"
        const val COL_HIST_ID = "id"
        const val COL_HIST_WORK_ID = "workId"
        const val COL_HIST_ACTION = "action"
        const val COL_HIST_PERIOD = "period"
        const val COL_HIST_REASON = "reason"
        const val COL_HIST_DATE = "date"
    }

    /** Создаёт полную схему новой базы и добавляет демонстрационный справочник систем. */
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""CREATE TABLE $TABLE_SYSTEMS (
            $COL_SYS_ID TEXT PRIMARY KEY, $COL_SYS_NAME TEXT NOT NULL, $COL_SYS_DESC TEXT NOT NULL,
            $COL_SYS_APPROVER TEXT NOT NULL, $COL_SYS_DEPUTY TEXT NOT NULL, $COL_SYS_LEVEL TEXT NOT NULL,
            $COL_SYS_DEPS TEXT NOT NULL DEFAULT '[]')""")
        db.execSQL("""CREATE TABLE $TABLE_WORKS (
            $COL_WORK_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_WORK_TITLE TEXT NOT NULL,
            $COL_WORK_DATE TEXT NOT NULL, $COL_WORK_START TEXT NOT NULL, $COL_WORK_END TEXT NOT NULL,
            $COL_WORK_SYS_ID TEXT NOT NULL, $COL_WORK_ENGINEER TEXT NOT NULL, $COL_WORK_DESC TEXT NOT NULL,
            $COL_WORK_ROLLBACK TEXT NOT NULL, $COL_WORK_STATUS TEXT NOT NULL, $COL_WORK_OWNER TEXT NOT NULL,
            $COL_WORK_START_DT TEXT NOT NULL, $COL_WORK_END_DT TEXT NOT NULL, $COL_WORK_AFFECTED TEXT NOT NULL DEFAULT '[]')""")
        db.execSQL("""CREATE TABLE $TABLE_APPROVALS (
            $COL_APP_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_APP_WORK_ID INTEGER NOT NULL,
            $COL_APP_SYS_ID TEXT NOT NULL, $COL_APP_APPROVER_ID TEXT NOT NULL, $COL_APP_STATUS TEXT NOT NULL,
            $COL_APP_DELEGATED_TO TEXT, $COL_APP_COMMENT TEXT NOT NULL DEFAULT '', $COL_APP_APPROVED_AT TEXT,
            FOREIGN KEY($COL_APP_WORK_ID) REFERENCES $TABLE_WORKS($COL_WORK_ID) ON DELETE CASCADE)""")
        db.execSQL("""CREATE TABLE $TABLE_DELEGATIONS (
            $COL_DEL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_DEL_SYS_ID TEXT NOT NULL,
            $COL_DEL_APPROVER_ID TEXT NOT NULL, $COL_DEL_DEPUTY_ID TEXT NOT NULL,
            $COL_DEL_FROM TEXT NOT NULL, $COL_DEL_TO TEXT NOT NULL, $COL_DEL_REASON TEXT NOT NULL,
            $COL_DEL_CREATED TEXT NOT NULL)""")
        db.execSQL("""CREATE TABLE $TABLE_HISTORY (
            $COL_HIST_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_HIST_WORK_ID INTEGER NOT NULL,
            $COL_HIST_ACTION TEXT NOT NULL, $COL_HIST_PERIOD TEXT NOT NULL DEFAULT '',
            $COL_HIST_REASON TEXT NOT NULL DEFAULT '', $COL_HIST_DATE TEXT NOT NULL,
            FOREIGN KEY($COL_HIST_WORK_ID) REFERENCES $TABLE_WORKS($COL_WORK_ID) ON DELETE CASCADE)""")
        populateInitialData(db)
    }

    /** Заполняет новую базу начальными системами для демонстрации приложения. */
    private fun populateInitialData(db: SQLiteDatabase) {
        listOf(
            arrayOf("abs", "АБС", "Автоматизированная банковская система", "approver", "deputy", "CRITICAL", """["dwh","dbo"]"""),
            arrayOf("dwh", "DWH", "Хранилище данных", "approver", "deputy", "HIGH", "[]"),
            arrayOf("dbo", "ДБО", "Дистанционное банковское обслуживание", "approver", "deputy", "CRITICAL", """["abs"]"""),
            arrayOf("smev", "СМЭВ", "Межведомственное взаимодействие", "approver", "deputy", "MEDIUM", "[]")
        ).forEach { row ->
            db.insertOrThrow(TABLE_SYSTEMS, null, ContentValues().apply {
                put(COL_SYS_ID, row[0]); put(COL_SYS_NAME, row[1]); put(COL_SYS_DESC, row[2])
                put(COL_SYS_APPROVER, row[3]); put(COL_SYS_DEPUTY, row[4]); put(COL_SYS_LEVEL, row[5]); put(COL_SYS_DEPS, row[6])
            })
        }
    }

    /** Выполняет сохраняющие пользовательские данные миграции между версиями схемы. */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_WORKS ADD COLUMN ownerUsername TEXT NOT NULL DEFAULT 'technician'")
            db.execSQL("ALTER TABLE $TABLE_APPROVALS ADD COLUMN assignedUsername TEXT NOT NULL DEFAULT 'technician'")
        }
        if (oldVersion < 3) migrateToV3(db)
    }

    /** Переносит legacy-таблицы версии 2 в нормализованную схему версии 3 транзакционно. */
    private fun migrateToV3(db: SQLiteDatabase) {
        db.beginTransaction()
        try {
            db.execSQL("ALTER TABLE $TABLE_SYSTEMS RENAME TO systems_legacy")
            db.execSQL("ALTER TABLE $TABLE_WORKS RENAME TO works_legacy")
            db.execSQL("ALTER TABLE $TABLE_APPROVALS RENAME TO approvals_legacy")
            db.execSQL("ALTER TABLE $TABLE_HISTORY RENAME TO history_legacy")
            db.execSQL("""CREATE TABLE $TABLE_SYSTEMS (
                id TEXT PRIMARY KEY, name TEXT NOT NULL, description TEXT NOT NULL, approverId TEXT NOT NULL,
                deputyId TEXT NOT NULL, criticality TEXT NOT NULL, dependentSystemIds TEXT NOT NULL DEFAULT '[]')""")
            db.execSQL("""INSERT INTO $TABLE_SYSTEMS SELECT id, name, description, 'approver', 'deputy',
                CASE level WHEN 'Критический' THEN 'CRITICAL' WHEN 'Высокий' THEN 'HIGH' WHEN 'Низкий' THEN 'LOW' ELSE 'MEDIUM' END,
                CASE WHEN dependencies = '' THEN '[]' ELSE '[\"' || replace(dependencies, ',', '\",\"') || '\"]' END FROM systems_legacy""")
            db.execSQL("""CREATE TABLE $TABLE_WORKS (
                id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, date TEXT NOT NULL, timeStart TEXT NOT NULL,
                timeEnd TEXT NOT NULL, systemId TEXT NOT NULL, engineerName TEXT NOT NULL, description TEXT NOT NULL,
                rollbackPlan TEXT NOT NULL, status TEXT NOT NULL, ownerUsername TEXT NOT NULL,
                startDateTime TEXT NOT NULL, endDateTime TEXT NOT NULL, affectedSystemIds TEXT NOT NULL DEFAULT '[]')""")
            db.execSQL("""INSERT INTO $TABLE_WORKS SELECT id,title,date,timeStart,timeEnd,systemId,engineerName,description,rollbackPlan,
                CASE status WHEN 'approved' THEN 'WORK_APPROVED' WHEN 'cancelled' THEN 'WORK_CANCELLED' ELSE 'PENDING' END,
                CASE WHEN ownerUsername='' THEN 'technician' ELSE ownerUsername END, date||' '||timeStart,date||' '||timeEnd,'[\"'||systemId||'\"]' FROM works_legacy""")
            db.execSQL("""CREATE TABLE $TABLE_APPROVALS (
                id INTEGER PRIMARY KEY AUTOINCREMENT, workId INTEGER NOT NULL, systemId TEXT NOT NULL, approverId TEXT NOT NULL,
                status TEXT NOT NULL, delegatedToId TEXT, comment TEXT NOT NULL DEFAULT '', approvedAt TEXT)""")
            db.execSQL("""INSERT INTO $TABLE_APPROVALS SELECT id,workId,systemId,
                CASE WHEN assignedUsername='' THEN 'approver' ELSE assignedUsername END,
                upper(status), CASE WHEN isDeputy=1 THEN 'deputy' ELSE NULL END, '', NULL FROM approvals_legacy""")
            db.execSQL("""CREATE TABLE $TABLE_DELEGATIONS (
                id INTEGER PRIMARY KEY AUTOINCREMENT, systemId TEXT NOT NULL, approverId TEXT NOT NULL, deputyId TEXT NOT NULL,
                dateFrom TEXT NOT NULL, dateTo TEXT NOT NULL, reason TEXT NOT NULL, createdAt TEXT NOT NULL)""")
            db.execSQL("""CREATE TABLE $TABLE_HISTORY (
                id INTEGER PRIMARY KEY AUTOINCREMENT, workId INTEGER NOT NULL, action TEXT NOT NULL,
                period TEXT NOT NULL DEFAULT '', reason TEXT NOT NULL DEFAULT '', date TEXT NOT NULL,
                FOREIGN KEY(workId) REFERENCES works(id) ON DELETE CASCADE)""")
            db.execSQL("""INSERT INTO $TABLE_HISTORY SELECT id,workId,action,coalesce(period,''),coalesce(reason,''),date FROM history_legacy""")
            db.execSQL("DROP TABLE systems_legacy"); db.execSQL("DROP TABLE approvals_legacy"); db.execSQL("DROP TABLE history_legacy"); db.execSQL("DROP TABLE works_legacy")
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
    }

    /** Включает контроль внешних ключей при каждом открытии writable-базы. */
    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        if (!db.isReadOnly) db.execSQL("PRAGMA foreign_keys=ON")
    }
}
