package com.axtarget.processnova.data.api

import android.util.Log
import com.axtarget.processnova.core.Constants
import okhttp3.*

/**
 * Interceptor de Seguridad de Grado Industrial.
 * Sincroniza los tokens CSRF de Django con la app móvil.
 */
class CsrfInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // 1. WARM-UP: Si no tenemos el token, lo pedimos a la página de login (que siempre lo da)
        if (CookieManager.getCookie(Constants.CSRF_COOKIE).isNullOrEmpty()) {
            Log.d("CSRF", "Token no encontrado. Realizando Warm-up...")
            val warmUpRequest = Request.Builder()
                .url(Constants.BASE_URL + "login/") // Apuntamos a login para forzar el Set-Cookie
                .get()
                .build()
            try {
                chain.proceed(warmUpRequest).close()
            } catch (e: Exception) {
                Log.e("CSRF", "Error en warm-up: ${e.message}")
            }
        }

        // 2. APLICACIÓN DE SEGURIDAD
        if (originalRequest.method in listOf("POST", "PUT", "DELETE", "PATCH")) {
            val csrfToken = CookieManager.getCookie(Constants.CSRF_COOKIE) ?: ""
            Log.d("CSRF", "Aplicando token a ${originalRequest.method}: $csrfToken")
            
            val newRequestBuilder = originalRequest.newBuilder()
                .header("X-CSRFToken", csrfToken)
                .header("Referer", Constants.BASE_URL) // Crítico para Django en HTTPS
                .header("X-Requested-With", "XMLHttpRequest")

            return chain.proceed(newRequestBuilder.build())
        }

        return chain.proceed(originalRequest)
    }
}

object CookieManager {
    private val cookieStore = mutableMapOf<String, Cookie>()

    fun getCookie(name: String): String? = cookieStore[name]?.value
    fun getAllCookies(): List<Cookie> = cookieStore.values.toList()
    fun putCookie(cookie: Cookie) { cookieStore[cookie.name] = cookie }
    fun clear() { cookieStore.clear() }
}

class SessionCookieJar : CookieJar {
    private val cookieStore = mutableMapOf<String, MutableList<Cookie>>()

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
        return cookieStore[url.host] ?: emptyList()
    }
}
