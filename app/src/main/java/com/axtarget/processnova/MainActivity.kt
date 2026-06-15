package com.axtarget.processnova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
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
        enableEdgeToEdge()

        val sessionManager = SessionManager(this)
        ApiClient.init(sessionManager)

        setContent {
            ProcessNovaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // Estado: 0 = splash, 1 = navegación principal
                    var showSplash by remember { mutableStateOf(true) }

                    if (showSplash) {
                        SplashScreen(
                            onSplashFinished = {
                                showSplash = false
                            }
                        )
                    } else {
                        val isLoggedIn by sessionManager.isLoggedIn.collectAsState(initial = false)

                        // Si está logueado → BaseLayout (dashboard con sidebar)
                        // Si no → Landing page pública
                        AppNavigation(
                            startDestination = if (isLoggedIn) Routes.DASHBOARD else Routes.LANDING
                        )
                    }
                }
            }
        }
    }
}
