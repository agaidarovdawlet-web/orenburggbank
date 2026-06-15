/*
 * Портал техработ — АО «БАНК ОРЕНБУРГ»
 * Автор: Агайдаров Даулет Азаматович
 * Студент группы 23 КСК 4, Университетский колледж ОГУ
 * Производственная практика 2026
 */

package com.example.orenburggbank.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.orenburggbank.data.WorkRepositoryContract
import com.example.orenburggbank.data.entity.*
import com.example.orenburggbank.domain.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Назначение: блок реализует отдельную функцию портала техработ.
 * Входные данные: параметры объявления.
 * Выходные данные: результат объявления или изменение состояния UI.
 * Роль в проекте: покрывает соответствующий сценарий технического задания.
 */
class WorkViewModel(
    private val repository: WorkRepositoryContract,
    private val authService: AuthService,
    private val useCases: WorkUseCases,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val allWorks = repository.allWorks
    val allSystems = repository.allSystems
    val allDelegations = repository.allDelegations

    private val _searchQuery = MutableStateFlow(savedStateHandle["searchQuery"] ?: "")
    val searchQuery: StateFlow<String> = _searchQuery

    private val restoredUsername: String? = savedStateHandle["username"]
    private val restoredRole = savedStateHandle.get<String>("role")?.let { runCatching { AppRole.valueOf(it) }.getOrNull() }
    private val _currentUser = MutableStateFlow(restoredUsername?.let { UserSession(it, restoredRole ?: AppRole.VIEWER) })
    val currentUser = _currentUser.asStateFlow()
    val currentRole: StateFlow<AppRole> = currentUser.map { it?.role ?: AppRole.VIEWER }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppRole.VIEWER)
    val isAuthenticated: StateFlow<Boolean> = currentUser.map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messages = _messages.asSharedFlow()

    val filteredWorks: StateFlow<List<WorkWithApprovals>> = combine(allWorks, _searchQuery) { works, query ->
        if (query.isBlank()) works else works.filter {
            it.work.title.contains(query, true) || it.system.name.contains(query, true)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        refresh()
    }

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun login(username: String, password: String): Boolean {
        val user = authService.login(username, password)
        if (user == null) {
            notify("Неверный логин или пароль")
            return false
        }
        _currentUser.value = user
        savedStateHandle["username"] = user.username
        savedStateHandle["role"] = user.role.name
        return true
    }

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun logout() {
        _currentUser.value = null
        savedStateHandle.remove<String>("username")
        savedStateHandle.remove<String>("role")
    }

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun refresh() = launchOperation { repository.refreshData() }

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        savedStateHandle["searchQuery"] = query
    }

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun hasPermission(permission: Permission): Boolean =
        currentUser.value?.let { useCases.authorization.hasPermission(it, permission) } == true

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun canCancel(work: WorkEntity): Boolean = runCatching {
        useCases.authorization.requireCanCancel(requireUser(), work)
    }.isSuccess

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun canEdit(work: WorkEntity): Boolean = runCatching {
        useCases.authorization.requireCanEdit(requireUser(), work)
    }.isSuccess

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun canApprove(approval: ApprovalEntity): Boolean = runCatching {
        val delegation = allDelegations.value.firstOrNull { it.systemId == approval.systemId && it.approverId == approval.approverId }
        useCases.authorization.requireCanApprove(requireUser(), approval, delegation)
    }.isSuccess

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun createWork(work: WorkEntity, onSuccess: () -> Unit = {}) =
        launchResult({ useCases.create(requireUser(), work) }, onSuccess)

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun updateWork(work: WorkEntity, onSuccess: () -> Unit = {}) =
        launchResult({ useCases.update(requireUser(), work) }, onSuccess)

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun approveWork(workId: Long, approvalId: Long) =
        launchResult({ useCases.setApprovalStatus(requireUser(), workId, approvalId, ApprovalStatus.APPROVED) })

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun rejectWork(workId: Long, approvalId: Long) =
        launchResult({ useCases.setApprovalStatus(requireUser(), workId, approvalId, ApprovalStatus.REJECTED) })

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun cancelWork(workId: Long) = launchResult({ useCases.cancel(requireUser(), workId) })

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun delegateApprover(workId: Long, approvalId: Long, deputyName: String, reason: String) =
        launchResult({
            val approval = allWorks.value.flatMap { it.approvals }.first { it.id == approvalId }
            val deputyId = allSystems.value.firstOrNull { it.id == approval.systemId }?.deputyId ?: deputyName
            useCases.createDelegation(requireUser(), DelegationEntity(
                systemId = approval.systemId,
                approverId = approval.approverId,
                deputyId = deputyId,
                dateFrom = java.time.LocalDateTime.now().toString(),
                dateTo = java.time.LocalDateTime.now().plusDays(30).toString(),
                reason = reason,
                createdAt = java.time.LocalDateTime.now().toString()
            ))
        })

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun returnFromDelegate(workId: Long, approvalId: Long, originalName: String) =
        launchResult({
            val approval = allWorks.value.flatMap { it.approvals }.first { it.id == approvalId }
            val delegation = allDelegations.value.first { it.systemId == approval.systemId && it.approverId == approval.approverId }
            useCases.deleteDelegation(requireUser(), delegation.id)
        })

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun saveSystem(system: SystemEntity, onSuccess: () -> Unit = {}) =
        launchResult({ useCases.saveSystem(requireUser(), system) }, onSuccess)

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun createDelegation(delegation: DelegationEntity, onSuccess: () -> Unit = {}) =
        launchResult({ useCases.createDelegation(requireUser(), delegation) }, onSuccess)

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun getHistoryForWork(workId: Long) = repository.getHistoryForWork(workId)
    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    fun notify(message: String) { _messages.tryEmit(message) }

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    private fun requireUser() = currentUser.value ?: throw SecurityException("Сначала выполните вход")

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    private fun launchOperation(block: suspend () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            runCatching { block() }.onFailure { notify(it.message ?: "Ошибка") }
            _isLoading.value = false
        }
    }

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    private fun <T> launchResult(block: suspend () -> AppResult<T>, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = block()) {
                is AppResult.Success -> onSuccess()
                is AppResult.Error -> notify(result.message)
                AppResult.Loading -> Unit
            }
            _isLoading.value = false
        }
    }

    /**
     * Назначение: блок реализует отдельную функцию портала техработ.
     * Входные данные: параметры объявления.
     * Выходные данные: результат объявления или изменение состояния UI.
     * Роль в проекте: покрывает соответствующий сценарий технического задания.
     */
    class Factory(
        private val repository: WorkRepositoryContract,
        private val authService: AuthService,
        private val useCases: WorkUseCases
    ) : ViewModelProvider.Factory {
        /**
         * Назначение: блок реализует отдельную функцию портала техработ.
         * Входные данные: параметры объявления.
         * Выходные данные: результат объявления или изменение состояния UI.
         * Роль в проекте: покрывает соответствующий сценарий технического задания.
         */
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            if (modelClass.isAssignableFrom(WorkViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return WorkViewModel(repository, authService, useCases, extras.createSavedStateHandle()) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
