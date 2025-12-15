package com.example.avaliafilmes.data.repository

import com.example.avaliafilmes.data.dao.UserDao
import com.example.avaliafilmes.data.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    
    suspend fun register(username: String, email: String, password: String): Result<User> {
        return try {
            // Verifica se o email já existe
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                return Result.failure(Exception("Email já cadastrado"))
            }
            
            val user = User(username = username, email = email, password = password)
            val userId = userDao.insert(user)
            Result.success(user.copy(id = userId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val user = userDao.login(email, password)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Email ou senha inválidos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getUserById(userId: Long): Flow<User?> {
        return userDao.getUserById(userId)
    }
    
    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            // Verifica se o novo email já está sendo usado por outro usuário
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser != null && existingUser.id != user.id) {
                return Result.failure(Exception("Email já está em uso"))
            }
            userDao.update(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteUser(user: User): Result<Unit> {
        return try {
            userDao.delete(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
