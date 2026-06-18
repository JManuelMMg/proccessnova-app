package com.axtarget.processnova.core

object Constants {
    const val BASE_URL = "https://processnovacore.onrender.com/"
    
    // Configuración de Sesión y Seguridad (Django)
    const val SESSION_COOKIE = "sessionid"
    const val CSRF_COOKIE = "csrftoken"
    const val CSRF_HEADER = "X-CSRFToken"
    
    // Nombres de archivos y preferencias
    const val DATABASE_NAME = "processnova_db"
    const val DATASTORE_NAME = "processnova_prefs"
    
    // Keys para Preferences DataStore
    const val KEY_SESSION_ID = "session_id"
    const val KEY_USER_NAME = "user_name"
    const val KEY_USER_EMAIL = "user_email"
    const val KEY_ORG_NAME = "org_name"
    const val KEY_BRANCH_NAME = "branch_name"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    
    // Configuración de red
    const val TIMEOUT_SECONDS = 60L
    const val DEFAULT_TAX_RATE = 0.16f
    const val APP_VERSION = "1.0.0"
    const val COMPANY_URL = "https://axtarget.com"
}

/**
 * Retorna un mensaje de error amigable según el código HTTP.
 */
fun getHttpErrorMessage(code: Int, context: String = ""): String {
    return when (code) {
        400 -> "Solicitud incorrecta. Verifica los datos enviados."
        401 -> "Sesión expirada. Por favor, inicia sesión de nuevo."
        403 -> "No tienes permiso para realizar esta acción."
        404 -> "El recurso no fue encontrado. Es posible que el módulo no esté habilitado en el servidor."
        408 -> "La solicitud tardó demasiado. Intenta de nuevo."
        429 -> "Demasiadas solicitudes. Espera un momento y reintenta."
        500 -> "Error interno del servidor. Intenta más tarde."
        502 -> "El servidor no está disponible temporalmente."
        503 -> "Servicio no disponible. El servidor puede estar en mantenimiento."
        else -> if (context.isNotEmpty()) "$context ($code)" else "Error de conexión ($code)"
    }
}
