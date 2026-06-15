/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.data.entity

/** Матрица ролей страницы 2 ТЗ, используемая для принятия решений о доступе. */
enum class AppRole(val displayName: String) {
    TECHNICIAN("Техник"),
    APPROVER("Согласующий"),
    DEPUTY("Заместитель"),
    AUTHOR("Автор"),
    VIEWER("Просмотр"),
    ADMIN("Админ"),
    MAINTAINER("Ведение справочников")
}

/** Аутентифицированный пользователь и его доверенная роль в текущей сессии. */
data class UserSession(
    val username: String,
    val role: AppRole,
    val userId: String = username
)
