package com.axtarget.processnova.data.api

import com.axtarget.processnova.core.Constants
import com.axtarget.processnova.data.api.services.*
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente API central que configura Retrofit con OkHttp.
 * Maneja cookies de sesión Django, CSRF y logging.
 */
object ApiClient {

    private var retrofit: Retrofit? = null
    private var sessionManager: SessionManager? = null

    /**
     * Inicializa el cliente API con el contexto de la app.
     * Debe llamarse una vez al iniciar la aplicación.
     */
    fun init(sessionManager: SessionManager) {
        this.sessionManager = sessionManager

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val sessionInterceptor = okhttp3.Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Accept", "application/json")
                .header("X-Requested-With", "XMLHttpRequest")
                .build()
            
            val response = chain.proceed(request)

            // Detectar sesión expirada:
            // Si pedimos JSON pero nos devuelven HTML y terminamos en la URL de login
            val isJsonRequest = request.header("Accept")?.contains("application/json") == true
            val isHtmlResponse = response.body?.contentType()?.toString()?.contains("text/html") == true
            val finalUrl = response.request.url.toString()

            // Solo limpiar si NO estamos intentando loguearnos explícitamente
            if (isJsonRequest && isHtmlResponse && finalUrl.contains("/login") && !request.url.toString().contains("/login")) {
                android.util.Log.w("ApiClient", "Sesión expirada detectada. Redirigiendo a Login...")
                sessionManager?.let { sm ->
                    runBlocking {
                        sm.clearSession()
                    }
                }
            }

            response
        }

        val cookieJar = SessionCookieJar()

        val okHttpClient = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(CsrfInterceptor())
            .addInterceptor(sessionInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy {
        retrofit!!.create(AuthService::class.java)
    }

    val dashboardService: DashboardService by lazy {
        retrofit!!.create(DashboardService::class.java)
    }

    val inventoryService: InventoryService by lazy {
        retrofit!!.create(InventoryService::class.java)
    }

    val salesService: SalesService by lazy {
        retrofit!!.create(SalesService::class.java)
    }

    val crmService: CrmService by lazy {
        retrofit!!.create(CrmService::class.java)
    }

    val financeService: FinanceService by lazy {
        retrofit!!.create(FinanceService::class.java)
    }

    val hrService: HrService by lazy {
        retrofit!!.create(HrService::class.java)
    }

    val logisticsService: LogisticsService by lazy {
        retrofit!!.create(LogisticsService::class.java)
    }

    val notificationService: NotificationService by lazy {
        retrofit!!.create(NotificationService::class.java)
    }

    val aiService: AiService by lazy {
        retrofit!!.create(AiService::class.java)
    }
}
