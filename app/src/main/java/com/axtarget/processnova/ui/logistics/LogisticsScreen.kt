package com.axtarget.processnova.ui.logistics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.axtarget.processnova.core.toShortDate
import com.axtarget.processnova.core.Result
import com.axtarget.processnova.data.models.*
import com.axtarget.processnova.data.repository.LogisticsRepository
import com.axtarget.processnova.ui.navigation.Routes
import com.axtarget.processnova.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogisticsScreen(navController: NavController, showTopBar: Boolean = false) {
    val scope = rememberCoroutineScope()
    val repository = remember { LogisticsRepository() }
    var selectedTab by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var orders by remember { mutableStateOf<List<ShipmentOrder>>(emptyList()) }
    var vehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }

    val tabs = listOf("Envíos", "Vehículos")

    LaunchedEffect(selectedTab) {
        isLoading = true
        when (selectedTab) {
            0 -> when (val r = repository.getOrders()) { is Result.Success -> orders = r.data; else -> {} }
            1 -> when (val r = repository.getVehicles()) { is Result.Success -> vehicles = r.data; else -> {} }
        }
        isLoading = false
    }

    if (showTopBar) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Logística") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryDark, titleContentColor = MaterialTheme.colorScheme.onPrimary, navigationIconContentColor = MaterialTheme.colorScheme.onPrimary)
                )
            }
        ) { padding ->
            LogisticsContent(
                modifier = Modifier.padding(padding).fillMaxSize(),
                selectedTab = selectedTab,
                onTabSelect = { selectedTab = it },
                tabs = tabs,
                isLoading = isLoading,
                orders = orders,
                vehicles = vehicles,
                onStatusChange = { id, status ->
                    scope.launch { repository.updateOrderStatus(id, status, null) }
                },
                onOrderClick = { navController.navigate(Routes.shipmentDetail(it)) }
            )
        }
    } else {
        LogisticsContent(
            modifier = Modifier.fillMaxSize(),
            selectedTab = selectedTab,
            onTabSelect = { selectedTab = it },
            tabs = tabs,
            isLoading = isLoading,
            orders = orders,
            vehicles = vehicles,
            onStatusChange = { id, status ->
                scope.launch { repository.updateOrderStatus(id, status, null) }
            },
            onOrderClick = { navController.navigate(Routes.shipmentDetail(it)) }
        )
    }
}

@Composable
private fun LogisticsContent(
    modifier: Modifier,
    selectedTab: Int,
    onTabSelect: (Int) -> Unit,
    tabs: List<String>,
    isLoading: Boolean,
    orders: List<ShipmentOrder>,
    vehicles: List<Vehicle>,
    onStatusChange: (Int, String) -> Unit,
    onOrderClick: (Int) -> Unit
) {
    Column(modifier = modifier) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title -> Tab(selected = selectedTab == index, onClick = { onTabSelect(index) }, text = { Text(title) }) }
        }
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            when (selectedTab) {
                0 -> OrderList(orders, onStatusChange, onOrderClick)
                1 -> VehicleList(vehicles)
            }
        }
    }
}

@Composable
fun OrderList(orders: List<ShipmentOrder>, onStatusChange: (Int, String) -> Unit, onOrderClick: (Int) -> Unit) {
    val statusNames = mapOf("preparando" to "Preparando", "en_transito" to "En tránsito", "entregado" to "Entregado")
    val statusColors = mapOf("preparando" to WarningColor, "en_transito" to InfoColor, "entregado" to SuccessColor)

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(orders) { order ->
            Card(modifier = Modifier.fillMaxWidth().clickable { onOrderClick(order.id) }, shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(order.number, fontWeight = FontWeight.Bold, color = PrimaryDark)
                        val sColor = statusColors[order.status] ?: WarningColor
                        AssistChip(onClick = { }, label = { Text(statusNames[order.status] ?: order.status) }, colors = AssistChipDefaults.assistChipColors(containerColor = sColor.copy(alpha = 0.1f), labelColor = sColor))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(order.customer, style = MaterialTheme.typography.bodyMedium)
                    Text(order.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(order.date.toShortDate(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        if (order.status != "entregado") {
                            val nextStatus = when (order.status) {
                                "preparando" -> "en_transito"
                                "en_transito" -> "entregado"
                                else -> null
                            }
                            nextStatus?.let {
                                TextButton(onClick = { onStatusChange(order.id, it) }) {
                                    Text("Marcar: ${statusNames[it] ?: it}")
                                }
                            }
                        }
                    }
                }
            }
        }
        if (orders.isEmpty()) { item { EmptyLogistics("No hay envíos registrados") } }
    }
}

@Composable
fun VehicleList(vehicles: List<Vehicle>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(vehicles) { vehicle ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalShipping, null, tint = PrimaryDark)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${vehicle.plate} • ${vehicle.model}", fontWeight = FontWeight.SemiBold)
                        Text("Conductor: ${vehicle.driver}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    val vColor = if (vehicle.status == "disponible") SuccessColor else WarningColor
                    AssistChip(onClick = { }, label = { Text(vehicle.status) }, colors = AssistChipDefaults.assistChipColors(containerColor = vColor.copy(alpha = 0.1f), labelColor = vColor))
                }
            }
        }
        if (vehicles.isEmpty()) { item { EmptyLogistics("No hay vehículos registrados") } }
    }
}

@Composable
fun EmptyLogistics(message: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.LocalShipping, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))
            Text(message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
    }
}
