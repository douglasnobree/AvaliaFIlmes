package com.example.avaliafilmes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avaliafilmes.data.model.User
import com.example.avaliafilmes.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.login(email, password)
            
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Success(user)
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Erro ao fazer login")
                }
            )
        }
    }
    
    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.register(username, email, password)
            
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Success(user)
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Erro ao registrar")
                }
            )
        }
    }
    
    fun updateUser(userId: Long, newUsername: String, newEmail: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            val currentUserValue = _currentUser.value
            if (currentUserValue == null) {
                _authState.value = AuthState.Error("Usuário não encontrado")
                return@launch
            }
            
            val updatedUser = currentUserValue.copy(
                username = newUsername,
                email = newEmail
            )
            
            val result = userRepository.updateUser(updatedUser)
            
            result.fold(
                onSuccess = {
                    _currentUser.value = updatedUser
                    _authState.value = AuthState.Success(updatedUser)
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Erro ao atualizar perfil")
                }
            )
        }
    }
    
    fun deleteAccount() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            val currentUserValue = _currentUser.value
            if (currentUserValue == null) {
                _authState.value = AuthState.Error("Usuário não encontrado")
                return@launch
            }
            
            val result = userRepository.deleteUser(currentUserValue)
            
            result.fold(
                onSuccess = {
                    _currentUser.value = null
                    _authState.value = AuthState.Idle
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Erro ao deletar conta")
                }
            )
        }
    }
    
    fun logout() {
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }
    
    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}
