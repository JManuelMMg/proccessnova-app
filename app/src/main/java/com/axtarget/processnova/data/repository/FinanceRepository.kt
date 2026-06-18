package com.axtarget.processnova.data.repository

import com.axtarget.processnova.core.Result
import com.axtarget.processnova.data.api.ApiClient
import com.axtarget.processnova.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FinanceRepository {

    suspend fun getSummary(): Result<FinanceSummary> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.financeService.getSummary()
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: Result.Error("Error al obtener resumen")
            } else {
                Result.Error("Error al obtener resumen", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getAccounts(): Result<List<Account>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.financeService.getAccounts()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener cuentas", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getTransactions(startDate: String?, endDate: String?): Result<List<Transaction>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.financeService.getTransactions(startDate, endDate)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener transacciones", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getIncome(startDate: String?, endDate: String?): Result<List<Transaction>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.financeService.getIncome(startDate, endDate)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener ingresos", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun createIncome(transaction: Transaction): Result<Transaction> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.financeService.createIncome(transaction)
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: Result.Error("Error al registrar ingreso")
            } else {
                Result.Error("Error al registrar ingreso", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getExpenses(startDate: String?, endDate: String?): Result<List<Transaction>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.financeService.getExpenses(startDate, endDate)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener egresos", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun createExpense(transaction: Transaction): Result<Transaction> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.financeService.createExpense(transaction)
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: Result.Error("Error al registrar egreso")
            } else {
                Result.Error("Error al registrar egreso", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getInvoices(): Result<List<Invoice>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.financeService.getInvoices()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener facturas", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getInvoice(id: Int): Result<Invoice> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.financeService.getInvoice(id)
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: Result.Error("Factura no encontrada")
            } else {
                Result.Error("Error al obtener factura", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    private fun handleException(e: Exception): String {
        android.util.Log.e("FinanceRepo", "Error de red", e)
        return when (e) {
            is java.net.SocketTimeoutException -> "Servidor lento (Render despertando), reintenta en un momento"
            is java.net.UnknownHostException -> "Sin conexión a internet"
            is java.net.ConnectException -> "No se pudo conectar al servidor. Reintentando..."
            is java.io.IOException -> "Error de conexión: ${e.message}"
            else -> "Error inesperado: ${e.message}"
        }
    }
}

