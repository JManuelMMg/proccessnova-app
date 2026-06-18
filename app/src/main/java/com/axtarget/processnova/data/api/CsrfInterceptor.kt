package com.axtarget.processnova.data.api

import android.util.Log
import com.axtarget.processnova.core.Constants
import kotlinx.coroutines.*
import okhttp3.*

/**
 * Interceptor de Seguridad de Grado Industrial.
 * Sincroniza los tokens CSRF de Django con la app móvil.
 */
class CsrfInterceptor : Interceptor {

    companion object {
        @Volatile
        private var isWarmingUp = false
        private val warmUpLock = Any()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // 1. WARM-UP Sincronizado: Si no tenemos el token, lo pedimos
        if (CookieManager.getCookie(Constants.CSRF_COOKIE).isNullOrEmpty()) {
            synchronized(warmUpLock) {
                // Doble comprobación dentro del lock
                if (CookieManager.getCookie(Constants.CSRF_COOKIE).isNullOrEmpty() && !isWarmingUp) {
                    isWarmingUp = true
                    try {
                        Log.d("CSRF", "Token no encontrado. Realizando Warm-up...")
                        val baseUrl = Constants.BASE_URL.removeSuffix("/")
                        val warmUpRequest = Request.Builder()
                            .url("$baseUrl/login/")
                            .header("User-Agent", "ProcessNova-Android/${Constants.APP_VERSION}")
                            .get()
                            .build()
                        
                        // Usamos una petición síncrona dentro del interceptor
                        val response = chain.proceed(warmUpRequest)
                        response.close()
                        Log.d("CSRF", "Warm-up completado. Token: ${CookieManager.getCookie(Constants.CSRF_COOKIE)}")
                    } catch (e: Exception) {
                        Log.e("CSRF", "Error en warm-up: ${e.message}")
                    } finally {
                        isWarmingUp = false
                    }
                }
            }
        }

        // 2. APLICACIÓN DE SEGURIDAD
        if (originalRequest.method in listOf("POST", "PUT", "DELETE", "PATCH")) {
            val csrfToken = CookieManager.getCookie(Constants.CSRF_COOKIE) ?: ""
            Log.d("CSRF", "Aplicando token a ${originalRequest.method}: $csrfToken")
            
            val newRequestBuilder = originalRequest.newBuilder()
                .header(Constants.CSRF_HEADER, csrfToken)
                .header("Referer", Constants.BASE_URL)
                .header("X-Requested-With", "XMLHttpRequest")
                .header("User-Agent", "ProcessNova-Android/${Constants.APP_VERSION}")

            return chain.proceed(newRequestBuilder.build())
        }

        return chain.proceed(originalRequest.newBuilder()
            .header("User-Agent", "ProcessNova-Android/${Constants.APP_VERSION}")
            .build())
    }
}

/**
 * Almacén de cookies en memoria para acceso rápido por interceptores.
 */
object CookieManager {
    private val cookieStore = mutableMapOf<String, Cookie>()

    fun getCookie(name: String): String? = cookieStore[name]?.value
    fun getAllCookies(): List<Cookie> = cookieStore.values.toList()
    fun putCookie(cookie: Cookie) { cookieStore[cookie.name] = cookie }
    fun clear() { cookieStore.clear() }
}

/**
 * Jar de cookies persistente a nivel de sesión.
 * Intenta restaurar la sesión desde el SessionManager si la memoria está vacía.
 */
class SessionCookieJar(private val sessionManager: SessionManager? = null) : CookieJar {
    private val cookieStore = mutableMapOf<String, MutableList<Cookie>>()
    private var isInitialized = false

    /**
     * Pre-carga la sesión de forma asíncrona para evitar bloqueos en el hilo principal.
     */
    fun prewarm(host: String) {
        if (isInitialized || sessionManager == null) return
        
        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val storedSessionId = sessionManager.getSessionId()
                if (storedSessionId.isNotEmpty()) {
                    synchronized(this@SessionCookieJar) {
                        val sessionCookie = Cookie.Builder()
                            .name(Constants.SESSION_COOKIE)
                            .value(storedSessionId)
                            .domain(host)
                            .path("/")
                            .build()
                        
                        val list = cookieStore.getOrPut(host) { mutableListOf() }
                        list.add(sessionCookie)
                        CookieManager.putCookie(sessionCookie)
                        isInitialized = true
                        Log.d("SessionCookieJar", "Sesión pre-cargada: $storedSessionId")
                    }
                } else {
                    isInitialized = true
                }
            } catch (e: Exception) {
                Log.e("SessionCookieJar", "Error en prewarm: ${e.message}")
            }
        }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val host = url.host
        val existing = cookieStore.getOrPut(host) { mutableListOf() }
        cookies.forEach { cookie ->
            existing.removeAll { it.name == cookie.name }
            existing.add(cookie)
            CookieManager.putCookie(cookie)
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val host = url.host
        
        // Bloqueo mínimo solo si el prewarm aún no ha terminado y es la primera petición
        if (!isInitialized && sessionManager != null) {
            synchronized(this) {
                if (!isInitialized) {
                    // Solo bloqueamos si es estrictamente necesario y no se pre-cargó
                    runBlocking {
                        val storedSessionId = sessionManager.getSessionId()
                        if (storedSessionId.isNotEmpty()) {
                            val sessionCookie = Cookie.Builder()
                                .name(Constants.SESSION_COOKIE)
                                .value(storedSessionId)
                                .domain(host)
                                .path("/")
                                .build()
                            
                            val list = cookieStore.getOrPut(host) { mutableListOf() }
                            list.add(sessionCookie)
                            CookieManager.putCookie(sessionCookie)
                        }
                    }
                    isInitialized = true
                }
            }
        }

        return cookieStore[host] ?: emptyList()
    }
}
