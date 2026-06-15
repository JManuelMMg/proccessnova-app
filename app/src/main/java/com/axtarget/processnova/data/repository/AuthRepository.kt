package com.axtarget.processnova.data.repository

import android.util.Log
import com.axtarget.processnova.core.Constants
import com.axtarget.processnova.core.Result
import com.axtarget.processnova.data.api.ApiClient
import com.axtarget.processnova.data.api.CookieManager
import com.axtarget.processnova.data.api.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Repositorio de Autenticación de Grado Industrial para Django.
 * Detecta éxitos incluso cuando el servidor responde con redirecciones HTML.
 */
class AuthRepository(
    private val sessionManager: SessionManager
) {

    suspend fun login(username: String, password: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val token = CookieManager.getCookie(Constants.CSRF_COOKIE) ?: ""
                val response = ApiClient.authService.login(username, password, token)
                handleDjangoResponse(response, username)
            } catch (e: Exception) {
                Result.Error("Fallo de red: ${e.message}")
            }
        }
    }

    suspend fun register(
        username: String, email: String, password1: String, password2: String,
        organizationName: String, rfc: String, razonSocial: String,
        regimenFiscal: String, codigoPostal: String, branchName: String
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val token = CookieManager.getCookie(Constants.CSRF_COOKIE) ?: ""
                val response = ApiClient.authService.register(
                    username, email, password1, password2,
                    organizationName, rfc, razonSocial,
                    regimenFiscal, codigoPostal, branchName, token
                )
                handleDjangoResponse(response, username)
            } catch (e: Exception) {
                Result.Error("Error en registro: ${e.message}")
            }
        }
    }

    suspend fun requestPasswordReset(email: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val token = CookieManager.getCookie(Constants.CSRF_COOKIE) ?: ""
                val response = ApiClient.authService.requestPasswordReset(email, token)
                handleDjangoResponse(response, "")
            } catch (e: Exception) {
                Result.Error("Error al solicitar recuperación: ${e.message}")
            }
        }
    }

    suspend fun changePassword(oldPassword: String, newPassword1: String, newPassword2: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val token = CookieManager.getCookie(Constants.CSRF_COOKIE) ?: ""
                val response = ApiClient.authService.changePassword(oldPassword, newPassword1, newPassword2, token)
                handleDjangoResponse(response, "")
            } catch (e: Exception) {
                Result.Error("Error al cambiar contraseña: ${e.message}")
            }
        }
    }

    private suspend fun handleDjangoResponse(
        response: Response<ResponseBody>,
        username: String
    ): Result<String> {
        val bodyString = response.body()?.string() ?: response.errorBody()?.string() ?: ""
        
        // 1. ¿ES ÉXITO? (Si el HTML dice "Dashboard", Django nos dejó entrar)
        if (bodyString.contains("<title>Dashboard") || bodyString.contains("¡Bienvenido")) {
            val sessionId = CookieManager.getCookie(Constants.SESSION_COOKIE) ?: ""
            if (sessionId.isNotEmpty() && username.isNotEmpty()) {
                sessionManager.saveSession(sessionId, username, "", "")
            }
            return Result.Success("Acceso concedido")
        }

        // 2. ¿ES ERROR EN HTML? (Extraer mensaje de error del backend)
        if (bodyString.contains("<html") || bodyString.contains("<!DOCTYPE html>")) {
            val errorMatch = Regex("<p class=\"text-sm\">(.*?)</p>").find(bodyString)
            val msg = errorMatch?.groupValues?.get(1) ?: "Error de validación (revisa tus datos)"
            
            if (bodyString.contains("CSRF") || response.code() == 403) {
                return Result.Error("Error de seguridad. Intenta de nuevo.", 403)
            }
            return Result.Error(msg.replace("&quot;", "\"").replace("&#x27;", "'"))
        }

        // 3. ¿ES ÉXITO POR CÓDIGO?
        if (response.isSuccessful) {
            return Result.Success("Operación exitosa")
        }

        return Result.Error("Servidor no disponible (${response.code()})")
    }

    suspend fun logout(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try { ApiClient.authService.logout() } catch (e: Exception) {}
            sessionManager.clearSession()
            CookieManager.clear()
            Result.Success(Unit)
        }
    }

    suspend fun isLoggedIn(): Boolean = sessionManager.getSessionId().isNotEmpty()
}
