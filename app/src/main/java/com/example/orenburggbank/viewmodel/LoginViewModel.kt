/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.viewmodel

import androidx.lifecycle.ViewModel
import com.example.orenburggbank.data.entity.UserSession
import com.example.orenburggbank.domain.AuthService

/**
 * [МОДЕЛЬ СОСТОЯНИЯ ВХОДА]
 *
 * Назначение: предоставляет изолированный сценарий проверки логина и пароля.
 * Входные данные: [AuthService], логин и пароль.
 * Выходные данные: [UserSession] при корректных данных либо `null`.
 * Роль в проекте: отделяет аутентификацию от Compose UI и может применяться
 * при дальнейшем разделении общего WorkViewModel.
 *
 * Пример использования:
 * `loginViewModel.authenticate("admin", "admin")`
 */
class LoginViewModel(private val authService: AuthService) : ViewModel() {
    /** Проверяет введённые учётные данные и возвращает доверенную сессию. */
    fun authenticate(username: String, password: String): UserSession? = authService.login(username, password)
}
