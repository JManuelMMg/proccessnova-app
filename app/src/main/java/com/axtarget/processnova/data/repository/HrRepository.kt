package com.axtarget.processnova.data.repository

import com.axtarget.processnova.core.Result
import com.axtarget.processnova.data.api.ApiClient
import com.axtarget.processnova.data.api.services.*
import com.axtarget.processnova.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HrRepository {

    suspend fun getEmployees(): Result<List<Employee>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.hrService.getEmployees()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener empleados", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getEmployee(id: Int): Result<Employee> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.hrService.getEmployee(id)
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: Result.Error("Empleado no encontrado")
            } else {
                Result.Error("Error al obtener empleado", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun createEmployee(employee: Employee): Result<Employee> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.hrService.createEmployee(employee)
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: Result.Error("Error al crear empleado")
            } else {
                Result.Error("Error al crear empleado", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun checkIn(employeeId: Int): Result<CheckInResponse> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.hrService.checkIn(CheckInRequest(employeeId))
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: Result.Error("Error al registrar entrada")
            } else {
                Result.Error("Error al registrar entrada", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun checkOut(employeeId: Int): Result<CheckInResponse> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.hrService.checkOut(CheckOutRequest(employeeId))
            if (response.isSuccessful) {
                response.body()?.let { Result.Success(it) }
                    ?: Result.Error("Error al registrar salida")
            } else {
                Result.Error("Error al registrar salida", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getAttendance(employeeId: Int?, month: String?): Result<List<AttendanceRecord>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.hrService.getAttendance(employeeId, month)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener asistencia", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getPayroll(period: String?): Result<List<PayrollEntry>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.hrService.getPayroll(period)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener nómina", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun getDepartments(): Result<List<Department>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.hrService.getDepartments()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener departamentos", response.code())
            }
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    private fun handleException(e: Exception): String {
        android.util.Log.e("HrRepo", "Error de red", e)
        return when (e) {
            is java.net.SocketTimeoutException -> "Servidor lento (Render despertando), reintenta en un momento"
            is java.net.UnknownHostException -> "Sin conexión a internet"
            is java.net.ConnectException -> "No se pudo conectar al servidor. Reintentando..."
            is java.io.IOException -> "Error de conexión: ${e.message}"
            else -> "Error inesperado: ${e.message}"
        }
    }
}
