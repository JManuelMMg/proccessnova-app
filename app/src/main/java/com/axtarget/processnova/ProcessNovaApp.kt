package com.axtarget.processnova

import android.app.Application
import com.axtarget.processnova.data.api.ApiClient
import com.axtarget.processnova.data.api.SessionManager
import com.axtarget.processnova.data.local.AppDatabase

/**
 * Clase Application de ProcessNova.
 * Inicializa el cliente API, la base de datos y el session manager.
 */
class ProcessNovaApp : Application() {

    // Inicialización perezosa con doble verificación para hilos
    @Volatile
    private var database: AppDatabase? = null

    /**
     * Retorna la instancia de la base de datos de forma perezosa y segura.
     */
    fun getDatabase(): AppDatabase {
        return database ?: synchronized(this) {
            database ?: AppDatabase.getInstance(this).also { database = it }
        }
    }

    // Usamos lazy para que no se cree hasta que realmente se necesite
    val sessionManager: SessionManager by lazy {
        SessionManager(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Inicialización ligera: ApiClient solo guarda la referencia perezosa
        // No disparamos sessionManager.isLoggedIn aquí para no bloquear el main thread
        ApiClient.init(sessionManager)
    }

    companion object {
        lateinit var instance: ProcessNovaApp
            private set
    }
}

