package com.axtarget.processnova.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "nova_core")
    
    // Restauramos las animaciones de rotación y escaneo
    val rot1 by infiniteTransition.animateFloat(
        initialValue = 0f, 
        targetValue = 360f, 
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "rotation"
    )
    val scannerLine by infiniteTransition.animateFloat(
        initialValue = 0f, 
        targetValue = 1f, 
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "scanner"
    )

    val entranceAlpha = remember { Animatable(0f) }
    val coreScale = remember { Animatable(0.8f) }
    var bootMessage by remember { mutableStateOf("INICIALIZANDO...") }

    LaunchedEffect(Unit) {
        // Animaciones de entrada en paralelo
        launch {
            entranceAlpha.animateTo(1f, tween(1000))
        }
        launch {
            coreScale.animateTo(1f, spring(Spring.DampingRatioLowBouncy))
        }
        
        // Timer de mensajes de carga
        delay(600)
        bootMessage = "SINCRONIZANDO..."
        delay(1000)
        bootMessage = "LISTO"
        delay(200)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1628)),
        contentAlignment = Alignment.Center
    ) {
        // --- EFECTO: GRID NOVA ---
        Canvas(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = 0.2f }) {
            val gridColor = Color(0xFF00D4FF).copy(alpha = 0.1f)
            val centerY = size.height * 0.5f
            for (i in -8..8) {
                drawLine(
                    color = gridColor,
                    start = Offset(size.width / 2, centerY),
                    end = Offset(size.width / 2 + (i * 300f), size.height),
                    strokeWidth = 1f
                )
            }
        }

        // --- EFECTO: SCANNER LINE ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            val lineY = scannerLine * size.height
            drawLine(
                brush = Brush.verticalGradient(
                    listOf(Color.Transparent, Color(0xFF00D4FF).copy(alpha = 0.1f), Color.Transparent)
                ),
                start = Offset(0f, lineY),
                end = Offset(size.width, lineY),
                strokeWidth = 30.dp.toPx()
            )
        }

        // --- EL NÚCLEO (Restaurado con seguridad de visibilidad) ---
        Box(
            contentAlignment = Alignment.Center, 
            modifier = Modifier.graphicsLayer { 
                scaleX = coreScale.value
                scaleY = coreScale.value
                // SEGURO: Si la animación de entrada falla, al menos se ve un 20%
                alpha = entranceAlpha.value.coerceAtLeast(0.2f) 
            }
        ) {
            // Anillos Orbitales
            Canvas(modifier = Modifier.size(240.dp).graphicsLayer { rotationZ = -rot1 * 0.5f }) {
                drawCircle(Color(0xFF00D4FF).copy(alpha = 0.1f), style = Stroke(1f))
            }
            
            Canvas(modifier = Modifier.size(200.dp).graphicsLayer { rotationZ = rot1 }) {
                drawCircle(
                    color = Color(0xFF00D4FF), 
                    style = Stroke(2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(40f, 60f)))
                )
            }

            // El Logo "A"
            Text(
                "A",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 80.sp, 
                    fontWeight = FontWeight.Black,
                    brush = Brush.verticalGradient(listOf(Color.White, Color(0xFF00D4FF)))
                )
            )
        }

        // --- STATUS BAR INFERIOR ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "PROCESS NOVA", 
                color = Color.White.copy(alpha = 0.7f), 
                fontSize = 12.sp, 
                letterSpacing = 6.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                bootMessage, 
                color = Color(0xFF00D4FF).copy(alpha = 0.8f), 
                fontSize = 10.sp
            )
        }
    }
}
