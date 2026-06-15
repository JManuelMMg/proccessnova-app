package com.axtarget.processnova.data.repository

import com.axtarget.processnova.core.Result
import com.axtarget.processnova.data.api.ApiClient
import com.axtarget.processnova.data.api.services.*
import com.axtarget.processnova.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LogisticsRepository {

    suspend fun getOrders(): Result<List<ShipmentOrder>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.logisticsService.getOrders()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener órdenes", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getOrder(id: Int): Result<ShipmentOrder> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.logisticsService.getOrder(id)
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: Result.Error("Orden no encontrada")
            } else {
                Result.Error("Error al obtener orden", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun updateOrderStatus(id: Int, status: String, notes: String?): Result<ShipmentOrder> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.logisticsService.updateOrderStatus(id, UpdateOrderRequest(status, notes))
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: Result.Error("Error al actualizar estado")
            } else {
                Result.Error("Error al actualizar estado", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getVehicles(): Result<List<Vehicle>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.logisticsService.getVehicles()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener vehículos", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
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
