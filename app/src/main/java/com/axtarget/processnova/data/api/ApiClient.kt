package com.axtarget.processnova.data.api

import android.util.Log
import com.axtarget.processnova.core.Constants
import com.axtarget.processnova.data.api.services.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente API central optimizado para arranque rápido.
 * Utiliza inicialización perezosa para no bloquear el inicio de la app.
 */
object ApiClient {

    private var sessionManager: SessionManager? = null

    /**
     * Guarda la referencia del session manager. Muy rápido.
     */
    fun init(sessionManager: SessionManager) {
        this.sessionManager = sessionManager
    }

    private val retrofitInstance: Retrofit by lazy {
        val manager = sessionManager ?: throw IllegalStateException("ApiClient debe inicializarse con SessionManager")
        
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS // Reducido de BODY para mayor velocidad
        }

        val sessionInterceptor = okhttp3.Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Accept", "application/json")
                .header("X-Requested-With", "XMLHttpRequest")
                .header("User-Agent", "ProcessNova-Android/${Constants.APP_VERSION}")
                .build()
            
            var response = chain.proceed(request)
            
            // Reintento automático para errores comunes de Render (502, 503, 504)
            var tryCount = 0
            while (!response.isSuccessful && response.code >= 502 && tryCount < 3) {
                tryCount++
                Log.w("ApiClient", "Error ${response.code} detectado. Reintentando ($tryCount/3)...")
                response.close()
                Thread.sleep(2000L * tryCount) // Espera exponencial
                response = chain.proceed(request)
            }

            val isJsonRequest = request.header("Accept")?.contains("application/json") == true
            val isHtmlResponse = response.body?.contentType()?.toString()?.contains("text/html") == true
            val finalUrl = response.request.url.toString()

            if (isJsonRequest && isHtmlResponse && finalUrl.contains("/login") && !request.url.toString().contains("/login")) {
                // Manejo de sesión expirada
                manager.clearSessionSync()
            }

            response
        }

        val cookieJar = SessionCookieJar(manager)
        // Intentar pre-cargar host de forma segura
        try {
            val host = java.net.URL(Constants.BASE_URL).host
            cookieJar.prewarm(host)
        } catch (e: Exception) {}

        val okHttpClient = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(CsrfInterceptor())
            .addInterceptor(sessionInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()

        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Acceso perezoso a los servicios
    val authService: AuthService by lazy { retrofitInstance.create(AuthService::class.java) }
    val dashboardService: DashboardService by lazy { retrofitInstance.create(DashboardService::class.java) }
    val inventoryService: InventoryService by lazy { retrofitInstance.create(InventoryService::class.java) }
    val salesService: SalesService by lazy { retrofitInstance.create(SalesService::class.java) }
    val crmService: CrmService by lazy { retrofitInstance.create(CrmService::class.java) }
    val financeService: FinanceService by lazy { retrofitInstance.create(FinanceService::class.java) }
    val hrService: HrService by lazy { retrofitInstance.create(HrService::class.java) }
    val logisticsService: LogisticsService by lazy { retrofitInstance.create(LogisticsService::class.java) }
    val notificationService: NotificationService by lazy { retrofitInstance.create(NotificationService::class.java) }
    val aiService: AiService by lazy { retrofitInstance.create(AiService::class.java) }
}
