package com.axtarget.processnova.core

/**
 * Clase sellada para manejar estados de operaciones asíncronas.
 * Loading: operación en progreso
 * Success: operación exitosa con datos
 * Error: operación fallida con mensaje
 */
sealed class Result<out T> {
    data object Loading : Result<Nothing>()
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: Int? = null) : Result<Nothing>()
}

