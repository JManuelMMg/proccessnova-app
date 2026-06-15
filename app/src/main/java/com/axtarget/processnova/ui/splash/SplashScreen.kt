package com.axtarget.processnova.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
    
    // Animaciones Cinemáticas
    val rot1 by infiniteTransition.animateFloat(0f, 360f, infiniteRepeatable(tween(5000, easing = LinearEasing)))
    val rot2 by infiniteTransition.animateFloat(360f, 0f, infiniteRepeatable(tween(10000, easing = LinearEasing)))
    val orbitAlpha by infiniteTransition.animateFloat(0.2f, 0.8f, infiniteRepeatable(tween(2000), RepeatMode.Reverse))
    val gridMotion by infiniteTransition.animateFloat(0f, 200f, infiniteRepeatable(tween(2000, easing = LinearEasing)))
    val scannerLine by infiniteTransition.animateFloat(0f, 1f, infiniteRepeatable(tween(3000, easing = LinearEasing)))

    val entranceAlpha = remember { Animatable(0f) }
    val coreScale = remember { Animatable(0.85f) }
    var bootMessage by remember { mutableStateOf("INITIALIZING...") }

    LaunchedEffect(Unit) {
        launch { entranceAlpha.animateTo(1f, tween(1500)) }
        launch { coreScale.animateTo(1f, spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow)) }
        
        val logs = listOf("LINKING BACKEND...", "SECURITY HANDSHAKE...", "QUANTUM SYNC...", "SYSTEM ONLINE")
        logs.forEach { bootMessage = it; delay(900) }
        onSplashFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF01040A)),
        contentAlignment = Alignment.Center
    ) {
        // --- CAPA 1: GRID 3D EVOLUCIONADO ---
        Canvas(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = 0.3f }) {
            val color = Color(0xFF00D4FF).copy(alpha = 0.15f)
            val centerY = size.height * 0.45f
            
            // Líneas de Perspectiva Dinámica
            for (i in -12..12) {
                drawLine(
                    color = color,
                    start = Offset(size.width / 2, centerY),
                    end = Offset(size.width / 2 + (i * 450f), size.height),
                    strokeWidth = 1.5f
                )
            }
            // Ondas de Datos Horizontales
            for (i in 0..12) {
                val y = centerY + (i * 70f + gridMotion) * (i * 0.4f)
                if (y < size.height) {
                    drawLine(color, Offset(0f, y), Offset(size.width, y), 1f)
                }
            }
        }

        // --- CAPA 2: EFECTO SCANLINE ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            val lineY = scannerLine * size.height
            drawLine(
                brush = Brush.verticalGradient(listOf(Color.Transparent, Color(0xFF00D4FF).copy(alpha = 0.1f), Color.Transparent)),
                start = Offset(0f, lineY),
                end = Offset(size.width, lineY),
                strokeWidth = 40.dp.toPx()
            )
        }

        // --- CAPA 3: NÚCLEO MECATRÓNICO ---
        Box(contentAlignment = Alignment.Center, modifier = Modifier.graphicsLayer { 
            scaleX = coreScale.value; scaleY = coreScale.value; alpha = entranceAlpha.value 
        }) {
            // Partículas Orbitales (Simuladas en Canvas)
            Canvas(modifier = Modifier.size(350.dp)) {
                val center = Offset(size.width / 2, size.height / 2)
                repeat(8) { i ->
                    val angle = (rot1 + i * 45) * (Math.PI / 180).toFloat()
                    val radius = 140f + sin(rot2 * 0.05f + i) * 20f
                    drawCircle(
                        color = Color(0xFF00E5A0).copy(alpha = orbitAlpha),
                        radius = 2.dp.toPx(),
                        center = Offset(center.x + cos(angle) * radius, center.y + sin(angle) * radius)
                    )
                }
            }

            // Anillos de Control
            Canvas(modifier = Modifier.size(260.dp).graphicsLayer { rotationZ = rot1 }) {
                drawCircle(Color(0xFF00D4FF), style = Stroke(2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(100f, 50f))))
            }
            Canvas(modifier = Modifier.size(220.dp).graphicsLayer { rotationZ = rot2 }) {
                drawCircle(Color.White.copy(alpha = 0.3f), style = Stroke(1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 20f))))
            }

            // Isotipo
            Text(
                "A",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 95.sp, fontWeight = FontWeight.Black,
                    brush = Brush.verticalGradient(listOf(Color.White, Color(0xFF00D4FF)))
                )
            )
        }

        // --- CAPA 4: STATUS CONSOLE ---
        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 70.dp).graphicsLayer { alpha = entranceAlpha.value },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("A X T A R G E T", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.ExtraLight, letterSpacing = 8.sp)
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                modifier = Modifier.width(180.dp).height(1.dp),
                color = Color(0xFF00D4FF),
                trackColor = Color.White.copy(alpha = 0.05f)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(bootMessage, color = Color(0xFF00D4FF).copy(alpha = 0.7f), fontSize = 9.sp, fontWeight = FontWeight.Medium)
        }
    }
}
