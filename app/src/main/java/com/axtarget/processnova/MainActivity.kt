package com.axtarget.processnova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.axtarget.processnova.data.api.ApiClient
import com.axtarget.processnova.data.api.SessionManager
import com.axtarget.processnova.ui.navigation.AppNavigation
import com.axtarget.processnova.ui.navigation.Routes
import com.axtarget.processnova.ui.splash.SplashScreen
import com.axtarget.processnova.ui.theme.ProcessNovaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge() // Deshabilitado temporalmente para diagnosticar pantalla negra

        setContent {
            ProcessNovaTheme {
                // Surface principal con color de fondo del tema para evitar pantallas negras
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Estado: true = splash activo, false = navegación cargada
                    var showSplash by remember { mutableStateOf(true) }
                    
                    // Referencia al sessionManager
                    val sessionManager = remember { ProcessNovaApp.instance.sessionManager }

                    if (showSplash) {
                        SplashScreen(
                            onSplashFinished = {
                                showSplash = false
                            }
                        )
                    } else {
                        val isLoggedIn by sessionManager.isLoggedIn.collectAsState(initial = false)

                        // Si está logueado → Dashboard
                        // Si no → Landing / Login
                        AppNavigation(
                            startDestination = if (isLoggedIn) Routes.DASHBOARD else Routes.LANDING
                        )
                    }
                }
            }
        }
    }
}
