package com.axtarget.processnova.data.repository

import com.axtarget.processnova.core.Result
import com.axtarget.processnova.data.api.ApiClient
import com.axtarget.processnova.data.models.AppNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationRepository {

    suspend fun getNotifications(): Result<List<AppNotification>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.notificationService.getNotifications()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener notificaciones", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun markAsRead(id: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.notificationService.markAsRead(id)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("Error al marcar como leída", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun markAllAsRead(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.notificationService.markAllAsRead()
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error("Error al marcar todas como leídas", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getUnreadCount(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.notificationService.getUnreadCount()
            if (response.isSuccessful) {
                Result.Success(response.body()?.count ?: 0)
            } else {
                Result.Success(0)
            }
        } catch (e: Exception) {
            Result.Success(0)
        }
    }

    private fun handleException(e: Exception): String {
        return when (e) {
            is java.net.SocketTimeoutException -> "Conexión lenta, verifica tu internet"
            is java.net.UnknownHostException -> "Sin conexión a internet"
            is java.io.IOException -> "Error de conexión"
            else -> "Error inesperado: ${e.message}"
        }
    }
}

