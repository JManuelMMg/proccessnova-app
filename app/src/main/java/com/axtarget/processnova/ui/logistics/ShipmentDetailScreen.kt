package com.axtarget.processnova.ui.logistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.axtarget.processnova.core.Result
import com.axtarget.processnova.core.toShortDate
import com.axtarget.processnova.data.models.ShipmentOrder
import com.axtarget.processnova.data.repository.LogisticsRepository
import com.axtarget.processnova.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShipmentDetailScreen(shipmentId: Int, navController: NavController) {
    val scope = rememberCoroutineScope()
    val repository = remember { LogisticsRepository() }
    var shipment by remember { mutableStateOf<ShipmentOrder?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(shipmentId) {
        when (val result = repository.getOrder(shipmentId)) {
            is Result.Success -> shipment = result.data
            else -> {}
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del envío") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryDark, titleContentColor = MaterialTheme.colorScheme.onPrimary, navigationIconContentColor = MaterialTheme.colorScheme.onPrimary)
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (shipment != null) {
            val s = shipment!!
            val statusColors = mapOf("preparando" to WarningColor, "en_transito" to InfoColor, "entregado" to SuccessColor)
            val statusNames = mapOf("preparando" to "Preparando", "en_transito" to "En tránsito", "entregado" to "Entregado")
            Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = PrimaryDark)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Envío #${s.number}", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                            AssistChip(onClick = { }, label = { Text(statusNames[s.status] ?: s.status) }, colors = AssistChipDefaults.assistChipColors(containerColor = (statusColors[s.status] ?: WarningColor).copy(alpha = 0.2f), labelColor = MaterialTheme.colorScheme.onPrimary))
                        }
                        Text(s.customer, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    }
                }
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Detalles", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryDark)
                        Spacer(modifier = Modifier.height(12.dp))
                        ShipmentDetailRow("Dirección", s.address)
                        ShipmentDetailRow("Fecha de envío", s.date.toShortDate())
                        ShipmentDetailRow("Fecha de entrega", s.deliveryDate.toShortDate())
                        ShipmentDetailRow("Conductor", s.driver.ifBlank { "Sin asignar" })
                        ShipmentDetailRow("Vehículo", s.vehicle.ifBlank { "Sin asignar" })
                    }
                }
                if (s.status != "entregado") {
                    val nextStatus = when (s.status) { "preparando" -> "en_transito"; "en_transito" -> "entregado"; else -> null }
                    nextStatus?.let {
                        Button(
                            onClick = { scope.launch { repository.updateOrderStatus(s.id, it, null) } },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Accent)
                        ) {
                            Text("Marcar como: ${statusNames[it] ?: it}", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShipmentDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}



