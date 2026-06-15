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

    // Inicialización perezosa de la base de datos para evitar fallos en onCreate
    private var database: AppDatabase? = null

    fun getDatabase(): AppDatabase {
        return database ?: synchronized(this) {
            val instanceDb = AppDatabase.getInstance(this)
            database = instanceDb
            instanceDb
        }
    }

    lateinit var sessionManager: SessionManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Inicializar componentes (la base de datos se inicializa perezosamente)
        sessionManager = SessionManager(this)
        ApiClient.init(sessionManager)
    }

    companion object {
        lateinit var instance: ProcessNovaApp
            private set
    }
}

