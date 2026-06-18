package com.axtarget.processnova.ui.landing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.axtarget.processnova.ui.navigation.Routes
import com.axtarget.processnova.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(navController: NavController, showTopBar: Boolean = true) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val navMenuItems = listOf(
        NavMenuItem("Inicio", Icons.Default.Home) { coroutineScope.launch { listState.animateScrollToItem(0) } },
        NavMenuItem("Características", Icons.Default.Star) { coroutineScope.launch { listState.animateScrollToItem(1) } },
        NavMenuItem("Planes", Icons.Default.Paid) { coroutineScope.launch { listState.animateScrollToItem(3) } },
        NavMenuItem("Empresa", Icons.Default.Business) { navController.navigate(Routes.EMPRESA) },
        NavMenuItem("Iniciar sesión", Icons.Default.Login) { navController.navigate(Routes.LOGIN) },
    )

    if (showTopBar) {
        Scaffold(
            topBar = {
                TopNavBar(navController = navController, navItems = navMenuItems)
            },
            containerColor = Color(0xFF0B1628)
        ) { padding ->
            LandingContent(
                modifier = Modifier.fillMaxSize().padding(padding),
                listState = listState,
                navController = navController
            )
        }
    } else {
        LandingContent(
            modifier = Modifier.fillMaxSize(),
            listState = listState,
            navController = navController
        )
    }
}

@Composable
private fun LandingContent(
    modifier: Modifier,
    listState: LazyListState,
    navController: NavController
) {
    LazyColumn(
        state = listState,
        modifier = modifier.background(Color(0xFF0B1628)),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.radialGradient(colors = listOf(Accent.copy(alpha = 0.15f), Color.Transparent), radius = 600f))
                    .padding(horizontal = 24.dp, vertical = 56.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Accent.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Accent.copy(alpha = 0.3f)),
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        Text("🚀 ERP Inteligente para PYMES", color = Accent, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    }
                    Text("AxtarGet", fontSize = 44.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                    Text("Gestiona tu negocio completo desde un solo lugar", fontSize = 18.sp, color = Color(0xFF7A9BB5), textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))
                    Text("Inventario, POS, CRM, facturación, RRHH, logística y más — todo integrado con inteligencia artificial.", fontSize = 15.sp, color = Color(0xFF7A9BB5), textAlign = TextAlign.Center, lineHeight = 22.sp)
                    Spacer(modifier = Modifier.height(28.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = { navController.navigate(Routes.REGISTER) }, colors = ButtonDefaults.buttonColors(containerColor = Accent), shape = RoundedCornerShape(10.dp), modifier = Modifier.weight(1f).height(48.dp)) {
                            Text("Comenzar gratis", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        OutlinedButton(onClick = { navController.navigate(Routes.EMPRESA) }, shape = RoundedCornerShape(10.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Accent.copy(alpha = 0.5f)), modifier = Modifier.weight(1f).height(48.dp)) {
                            Text("Conocer más", color = Accent, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }

        item {
            Text("Características", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
        }
        item {
            val features = listOf(
                FeatureData("📦", "Inventario", "Control de stock en tiempo real con alertas automáticas"),
                FeatureData("💳", "POS", "Punto de venta rápido con soporte para múltiples métodos de pago"),
                FeatureData("👥", "CRM", "Gestiona clientes, seguimiento de oportunidades y ventas"),
                FeatureData("💰", "Finanzas", "Facturación CFDI 4.0, cuentas por cobrar y reportes"),
                FeatureData("👷", "RRHH", "Gestión de empleados, nóminas y cumplimiento NOM-035"),
                FeatureData("🚚", "Logística", "Envíos, rutas y seguimiento de paquetes en tiempo real"),
                FeatureData("🤖", "Asistente IA", "Análisis predictivo, recomendaciones y automatización"),
                FeatureData("📧", "Notificaciones", "Comunicación integrada con tu equipo y clientes"),
                FeatureData("📊", "Dashboard", "Métricas en tiempo real con gráficos interactivos")
            )
            Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                features.chunked(3).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { feature -> FeatureCard(modifier = Modifier.weight(1f), icon = feature.icon, title = feature.title, description = feature.description) }
                        repeat(3 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }
        }

        item {
            Text("Planes", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp))
        }
        item {
            Row(modifier = Modifier.padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PricingCard(modifier = Modifier.weight(1f), name = "Starter", price = "Gratis", period = "para siempre", features = listOf("Hasta 100 productos", "1 usuario", "POS básico", "Soporte por email"), isPopular = false, cta = "Comenzar", onCta = { navController.navigate(Routes.REGISTER) })
                PricingCard(modifier = Modifier.weight(1f), name = "Pro", price = "\$499", period = "/mes", features = listOf("Productos ilimitados", "Hasta 10 usuarios", "Todos los módulos", "Asistente IA", "Soporte prioritario"), isPopular = true, cta = "Probar 14 días", onCta = { navController.navigate(Routes.REGISTER) })
                PricingCard(modifier = Modifier.weight(1f), name = "Enterprise", price = "Custom", period = "a medida", features = listOf("Usuarios ilimitados", "Infraestructura dedicada", "API pública", "SLA 99.99%", "Account manager"), isPopular = false, cta = "Contactar", onCta = { navController.navigate(Routes.REGISTER) })
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 32.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1E45))) {
                Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("¿Listo para transformar tu negocio?", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                    Text("Únete a las PYMES que ya gestionan mejor con ProcessNova", color = Color(0xFF7A9BB5), textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp, bottom = 24.dp))
                    Button(onClick = { navController.navigate(Routes.REGISTER) }, colors = ButtonDefaults.buttonColors(containerColor = Accent), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth().height(52.dp)) {
                        Text("Crear cuenta gratis", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("AxtarGet © 2025", color = Color(0xFF4A6580), fontSize = 13.sp)
                Text("Todos los derechos reservados", color = Color(0xFF4A6580), fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopNavBar(navController: NavController, navItems: List<NavMenuItem>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(elevation = 24.dp, shape = RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp)),
            color = Color(0xFF071120).copy(alpha = 0.85f),
            border = androidx.compose.foundation.BorderStroke(
                1.dp, 
                Brush.linearGradient(
                    0.0f to Color.White.copy(alpha = 0.3f),
                    0.5f to Color.Transparent,
                    1.0f to Color(0xFF00D4FF).copy(alpha = 0.4f)
                )
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically, 
                    modifier = Modifier.weight(1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Surface(
                            shape = CircleShape,
                            color = Accent.copy(alpha = 0.2f),
                            modifier = Modifier.size(45.dp)
                        ) {}
                        Surface(
                            shape = RoundedCornerShape(10.dp), 
                            color = Accent, 
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(
                                modifier = Modifier.background(
                                    Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.2f), Color.Transparent))
                                ),
                                contentAlignment = Alignment.Center
                            ) { 
                                Text("A", color = Color.White, fontWeight = FontWeight.Black, fontSize = 17.sp) 
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            "AxtarGet", 
                            fontWeight = FontWeight.ExtraBold, 
                            color = Color.White, 
                            fontSize = 19.sp,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            "PROCESS NOVA", 
                            color = Color(0xFF00D4FF), 
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp
                        )
                    }
                }

                BoxWithConstraints {
                    if (maxWidth > 600.dp) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            navItems.take(4).forEach { item ->
                                TextButton(
                                    onClick = item.onClick,
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp)
                                ) {
                                    Text(
                                        item.label, 
                                        color = Color.White.copy(alpha = 0.7f), 
                                        fontSize = 14.sp, 
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = { navController.navigate(Routes.LOGIN) }, 
                                colors = ButtonDefaults.buttonColors(containerColor = Accent), 
                                shape = RoundedCornerShape(14.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp)
                            ) { 
                                Text("Iniciar Sesión", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp) 
                            }
                        }
                    } else {
                        var expanded by remember { mutableStateOf(false) }
                        IconButton(
                            onClick = { expanded = true },
                            modifier = Modifier.background(Color.White.copy(alpha = 0.05f), CircleShape)
                        ) { 
                            Icon(Icons.Default.Menu, "Menu", tint = Color.White) 
                        }
                        DropdownMenu(
                            expanded = expanded, 
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .background(Color(0xFF071120))
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        ) {
                            navItems.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item.label, color = Color.White) }, 
                                    leadingIcon = { Icon(item.icon, null, tint = Accent) },
                                    onClick = { expanded = false; item.onClick() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureCard(modifier: Modifier, icon: String, title: String, description: String) {
    Card(modifier = modifier, shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E35)), border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.White)
            Text(description, fontSize = 12.sp, color = Color(0xFF7A9BB5), lineHeight = 17.sp)
        }
    }
}

@Composable
private fun PricingCard(modifier: Modifier, name: String, price: String, period: String, features: List<String>, isPopular: Boolean, cta: String, onCta: () -> Unit) {
    Card(
        modifier = modifier.then(if (isPopular) Modifier.border(width = 2.dp, color = Accent.copy(alpha = 0.5f), shape = RoundedCornerShape(18.dp)) else Modifier),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = if (isPopular) Color(0xFF0B1E45) else Color(0xFF0F1E35)),
        border = if (isPopular) null else androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            if (isPopular) {
                Surface(shape = RoundedCornerShape(100.dp), color = Accent.copy(alpha = 0.15f), modifier = Modifier.padding(bottom = 8.dp)) {
                    Text("Más popular", fontSize = 11.sp, color = Accent, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }
            Text(name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(price, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Accent)
                Text(period, fontSize = 13.sp, color = Color(0xFF7A9BB5), modifier = Modifier.padding(bottom = 4.dp, start = 4.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            features.forEach { feature ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                    Icon(Icons.Default.Check, "Incluido", tint = Color(0xFF00E5A0), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(feature, fontSize = 13.sp, color = Color(0xFF7A9BB5))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onCta, colors = ButtonDefaults.buttonColors(containerColor = if (isPopular) Accent else Color.White.copy(alpha = 0.1f)), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth().height(44.dp)) {
                Text(cta, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }
}

private data class NavMenuItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val onClick: () -> Unit)
private data class FeatureData(val icon: String, val title: String, val description: String)
