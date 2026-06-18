package com.axtarget.processnova.data.repository

import com.axtarget.processnova.core.Result
import com.axtarget.processnova.data.api.ApiClient
import com.axtarget.processnova.data.models.AiAnalysisRequest
import com.axtarget.processnova.data.models.AiChatRequest
import com.axtarget.processnova.data.models.AiChatResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiRepository {

    suspend fun chat(message: String): Result<AiChatResponse> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.aiService.chat(AiChatRequest(message))
            handleResponse(response, "Sin respuesta del asistente")
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun analyzeInventory(): Result<AiChatResponse> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.aiService.analyzeInventory(AiAnalysisRequest("inventory"))
            handleResponse(response, "Sin respuesta del análisis")
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun analyzeCrm(): Result<AiChatResponse> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.aiService.analyzeCrm(AiAnalysisRequest("crm"))
            handleResponse(response, "Sin respuesta del análisis")
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun analyzeFinance(): Result<AiChatResponse> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.aiService.analyzeFinance(AiAnalysisRequest("finance"))
            handleResponse(response, "Sin respuesta del análisis")
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun suggestPrices(): Result<AiChatResponse> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.aiService.suggestPrices(AiAnalysisRequest("inventory"))
            handleResponse(response, "Sin respuesta de precios")
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun analyzeHr(): Result<AiChatResponse> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.aiService.analyzeHr(AiAnalysisRequest("hr"))
            handleResponse(response, "Sin respuesta del análisis")
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    suspend fun analyzeLogistics(): Result<AiChatResponse> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.aiService.analyzeLogistics(AiAnalysisRequest("logistics"))
            handleResponse(response, "Sin respuesta del análisis")
        } catch (e: Exception) {
            Result.Error(handleException(e))
        }
    }

    private fun handleResponse(response: retrofit2.Response<AiChatResponse>, emptyMsg: String): Result<AiChatResponse> {
        if (response.isSuccessful) {
            val body = response.body()
            return if (body != null && (body.response.isNotEmpty() || body.success)) {
                Result.Success(body)
            } else if (response.raw().request.url.toString().contains("/login")) {
                Result.Error("Tu sesión ha expirado. Por favor, inicia sesión de nuevo.")
            } else {
                Result.Error(emptyMsg)
            }
        } else {
            return Result.Error(parseErrorBody(response))
        }
    }

    private fun parseErrorBody(response: retrofit2.Response<*>): String {
        return try {
            val errorJson = response.errorBody()?.string() ?: ""
            if (errorJson.contains("429") || errorJson.contains("rate-limited")) {
                "La IA está experimentando alta demanda (límite de cuota excedido). Por favor espera un momento e intenta de nuevo."
            } else if (errorJson.contains("login") || response.code() == 403) {
                "Sesión inválida o expirada. Por favor reingresa."
            } else {
                "Error del servidor (${response.code()})"
            }
        } catch (e: Exception) {
            "Error de servidor (${response.code()})"
        }
    }

    private fun handleException(e: Exception): String {
        android.util.Log.e("AiRepo", "Error de red", e)
        return when (e) {
            is java.net.SocketTimeoutException -> "Servidor lento (Render despertando), reintenta en un momento"
            is java.net.UnknownHostException -> "Sin conexión a internet"
            is java.net.ConnectException -> "No se pudo conectar al servidor. Reintentando..."
            is java.io.IOException -> "Error de conexión: ${e.message}"
            else -> "Error: ${e.localizedMessage ?: "inesperado"}"
        }
    }
}
