/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.domain

import com.example.orenburggbank.data.entity.AppRole
import com.example.orenburggbank.data.entity.UserSession

/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
class AuthService {
    private val accounts = mapOf(
        "admin" to ("admin" to AppRole.ADMIN),
        "technician" to ("tech" to AppRole.TECHNICIAN),
        "viewer" to ("view" to AppRole.VIEWER),
        "approver" to ("approver" to AppRole.APPROVER),
        "deputy" to ("deputy" to AppRole.DEPUTY),
        "author" to ("author" to AppRole.AUTHOR),
        "maintainer" to ("maintainer" to AppRole.MAINTAINER)
    )

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun login(username: String, password: String): UserSession? {
        val account = accounts[username.trim().lowercase()] ?: return null
        return if (account.first == password) UserSession(username.trim().lowercase(), account.second) else null
    }
}
