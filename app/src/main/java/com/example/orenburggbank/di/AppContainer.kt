/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.di

import android.content.Context
import com.example.orenburggbank.data.DatabaseHelper
import com.example.orenburggbank.data.WorkRepository
import com.example.orenburggbank.domain.AuthService
import com.example.orenburggbank.domain.AuthorizationService
import com.example.orenburggbank.domain.WorkUseCases

/**
 * [КОНТЕЙНЕР ЗАВИСИМОСТЕЙ]
 *
 * Назначение: создаёт и связывает зависимости приложения без Hilt/Dagger.
 * Входные данные: Android [Context].
 * Выходные данные: готовые сервисы, репозиторий и UseCase.
 * Роль в проекте: реализует manual DI и направление зависимостей Clean Architecture.
 *
 * Пример использования:
 * `val container = AppContainer(applicationContext)`
 */
class AppContainer(context: Context) {
    /** Единая политика авторизации для Domain и Data-слоёв. */
    val authorization = AuthorizationService()

    /** Сервис проверки демонстрационных учётных записей. */
    val authService = AuthService()

    /** SQLite-реализация контракта репозитория. */
    val repository = WorkRepository(DatabaseHelper(context), authorization)

    /** Набор бизнес-сценариев портала. */
    val workUseCases = WorkUseCases(repository, authorization)
}
