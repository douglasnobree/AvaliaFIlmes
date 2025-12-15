package com.example.avaliafilmes.util

object ValidationUtils {
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
    
    fun getPasswordErrorMessage(password: String): String? {
        return when {
            password.isEmpty() -> "A senha não pode ser vazia"
            password.length < 6 -> "A senha deve ter no mínimo 6 caracteres"
            else -> null
        }
    }
    
    fun getEmailErrorMessage(email: String): String? {
        return when {
            email.isEmpty() -> "O email não pode ser vazio"
            !isValidEmail(email) -> "Email inválido"
            else -> null
        }
    }
}
