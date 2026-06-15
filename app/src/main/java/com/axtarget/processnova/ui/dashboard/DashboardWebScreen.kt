package com.axtarget.processnova.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.axtarget.processnova.ProcessNovaApp
import com.axtarget.processnova.core.toMXN
import com.axtarget.processnova.data.models.SaleSummary
import com.axtarget.processnova.ui.navigation.Routes
import com.axtarget.processnova.ui.theme.*
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardWebScreen(navController: NavController, showTopBar: Boolean = false) {
    val sessionManager = ProcessNovaApp.instance.sessionManager
    val userName by sessionManager.userName.collectAsState(initial = "")
    val orgName by sessionManager.orgName.collectAsState(initial = "")
    val branchName by sessionManager.branchName.collectAsState(initial = "Principal")

    if (showTopBar) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Dashboard") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PrimaryDark,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            DashboardContent(
                modifier = Modifier.padding(padding),
                userName = userName,
                orgName = orgName,
                branchName = branchName,
                navController = navController
            )
        }
    } else {
        DashboardContent(
            modifier = Modifier.fillMaxSize(),
            userName = userName,
            orgName = orgName,
            branchName = branchName,
            navController = navController
        )
    }
}

@Composable
private fun DashboardContent(
    modifier: Modifier,
    userName: String,
    orgName: String,
    branchName: String,
    navController: NavController
) {
    LazyColumn(
        modifier = modifier
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryDark)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "¡Bienvenido, ${userName.ifBlank { "Usuario" }}! 👋",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "$orgName • Sucursal $branchName",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        item {
            Text("Resumen de ventas", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A))
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiDashboardCard(
                    modifier = Modifier.weight(1f),
                    period = "Hoy",
                    amount = 0.0.toMXN(),
                    transactions = "0 transacciones",
                    color = SuccessColor
                )
                KpiDashboardCard(
                    modifier = Modifier.weight(1f),
                    period = "Semana",
                    amount = 0.0.toMXN(),
                    transactions = "0 transacciones",
                    color = Accent
                )
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiDashboardCard(
                    modifier = Modifier.weight(1f),
                    period = "Mes",
                    amount = 0.0.toMXN(),
                    transactions = "0 transacciones",
                    color = Color(0xFF6C63FF)
                )
                Card(
                    modifier = Modifier.weight(1f).clickable { navController.navigate(Routes.INVENTORY) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Stock Crítico", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            color = Color.Gray, letterSpacing = 1.sp)
                        Text("0", fontSize = 28.sp, fontWeight = FontWeight.Bold,
                            color = Color(0xFFE74C3C), modifier = Modifier.padding(top = 4.dp))
                        Text("Productos en alerta", fontSize = 12.sp, color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp))
                        Text("Revisar →", fontSize = 12.sp, color = Color(0xFFE74C3C),
                            fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("⭐ Productos Más Vendidos (Mes)", fontSize = 18.sp,
                        fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "No hay registros de ventas para este mes todavía.",
                        fontSize = 14.sp, color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Acciones Rápidas", fontSize = 18.sp,
                        fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Routes.POS) }
                            .background(SuccessColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PointOfSale, null, tint = SuccessColor, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Abrir Punto de Venta", fontWeight = FontWeight.SemiBold,
                                color = SuccessColor, fontSize = 15.sp)
                        }
                        Text("→", color = SuccessColor, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Routes.productForm()) }
                            .background(Accent.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AddBox, null, tint = Accent, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Nuevo Producto", fontWeight = FontWeight.SemiBold,
                                color = Accent, fontSize = 15.sp)
                        }
                        Text("→", color = Accent, fontSize = 20.sp)
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Sesión Activa", fontSize = 18.sp,
                        fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    Spacer(modifier = Modifier.height(12.dp))
                    SessionInfoRow("Empresa", orgName.ifBlank { "Sin nombre" })
                    SessionInfoRow("Sucursal", branchName)
                    SessionInfoRow("Usuario", userName.ifBlank { "Sin nombre" })
                }
            }
        }
    }
}

@Composable
private fun KpiDashboardCard(
    modifier: Modifier, period: String, amount: String,
    transactions: String, color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(period, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = Color.Gray, letterSpacing = 1.sp)
            Text(amount, fontSize = 22.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A), modifier = Modifier.padding(top = 4.dp))
            Text(transactions, fontSize = 12.sp, color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
private fun SessionInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
    }
}
