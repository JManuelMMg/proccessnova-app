package com.axtarget.processnova.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.axtarget.processnova.ProcessNovaApp
import com.axtarget.processnova.core.toShortDate
import com.axtarget.processnova.core.toMXN
import com.axtarget.processnova.data.models.DashboardData
import com.axtarget.processnova.data.models.SaleSummary
import com.axtarget.processnova.ui.navigation.Routes
import com.axtarget.processnova.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Pantalla principal del Dashboard con Bottom Navigation.
 * 7 tabs: Inicio | Inventario | POS | CRM | Finanzas | RRHH | Logística
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val sessionManager = ProcessNovaApp.instance.sessionManager
    val userName by sessionManager.userName.collectAsState(initial = "")
    val orgName by sessionManager.orgName.collectAsState(initial = "")
    val branchName by sessionManager.branchName.collectAsState(initial = "Principal")

    var dashboardData by remember { mutableStateOf<DashboardData?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val dashboardService = remember { com.axtarget.processnova.data.api.ApiClient.dashboardService }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val response = dashboardService.getDashboard()
            if (response.isSuccessful) {
                dashboardData = response.body()
            }
        } catch (e: Exception) {
            android.util.Log.e("Dashboard", "Error fetching data", e)
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "ProcessNova",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            orgName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.NOTIFICATIONS) }) {
                        Icon(Icons.Default.Notifications, "Notificaciones")
                    }
                    IconButton(onClick = { navController.navigate(Routes.AI_ASSISTANT) }) {
                        Icon(Icons.Default.AutoAwesome, "Asistente IA")
                    }
                    IconButton(onClick = { navController.navigate(Routes.PROFILE) }) {
                        Icon(Icons.Default.Person, "Perfil")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryDark,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Inventory, "Inventario") },
                    label = { Text("Inventario") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.PointOfSale, "POS") },
                    label = { Text("POS") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.People, "CRM") },
                    label = { Text("CRM") }
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.AccountBalance, "Finanzas") },
                    label = { Text("Finanzas") }
                )
            }
        }
    ) { padding ->
        if (isLoading && selectedTab == 0) {
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            when (selectedTab) {
                0 -> DashboardHomeContent(
                    modifier = Modifier.padding(padding),
                    userName = userName,
                    orgName = orgName,
                    branchName = branchName,
                    data = dashboardData ?: DashboardData(),
                    navController = navController
                )
                1 -> InventoryTabContent(modifier = Modifier.padding(padding), navController = navController)
                2 -> PosTabContent(modifier = Modifier.padding(padding), navController = navController)
                3 -> CrmTabContent(modifier = Modifier.padding(padding), navController = navController)
                4 -> FinanceTabContent(modifier = Modifier.padding(padding), navController = navController)
            }
        }
    }
}

/**
 * Contenido del tab Inicio del Dashboard.
 */
@Composable
fun DashboardHomeContent(
    modifier: Modifier = Modifier,
    userName: String,
    orgName: String,
    branchName: String,
    data: DashboardData,
    navController: NavController
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Saludo personalizado
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryDark)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "¡Hola, ${userName.ifBlank { "Usuario" }}! 👋",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$orgName • Sucursal $branchName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // KPIs del día
        item {
            Text(
                "Resumen del día",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Ventas hoy",
                    value = data.todaySales.toMXN(),
                    icon = Icons.Default.AttachMoney,
                    color = SuccessColor
                )
                KpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Stock crítico",
                    value = "${data.criticalStock} productos",
                    icon = Icons.Default.Warning,
                    color = StockCritical
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Clientes nuevos",
                    value = "${data.newCustomers}",
                    icon = Icons.Default.PersonAdd,
                    color = InfoColor
                )
                KpiCard(
                    modifier = Modifier.weight(1f),
                    title = "Alertas",
                    value = "${data.pendingAlerts} pendientes",
                    icon = Icons.Default.NotificationsActive,
                    color = WarningColor
                )
            }
        }

        // Acceso rápido a todos los módulos
        item {
            Text(
                "Módulos del sistema",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            // Fila 1: Inventario, POS, CRM
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ModuleCard(
                    modifier = Modifier.weight(1f),
                    title = "Inventario",
                    subtitle = "Productos y stock",
                    icon = Icons.Default.Inventory,
                    color = Primary,
                    onClick = { navController.navigate(Routes.INVENTORY) }
                )
                ModuleCard(
                    modifier = Modifier.weight(1f),
                    title = "POS",
                    subtitle = "Punto de venta",
                    icon = Icons.Default.PointOfSale,
                    color = Accent,
                    onClick = { navController.navigate(Routes.POS) }
                )
                ModuleCard(
                    modifier = Modifier.weight(1f),
                    title = "CRM",
                    subtitle = "Clientes",
                    icon = Icons.Default.People,
                    color = InfoColor,
                    onClick = { navController.navigate(Routes.CRM) }
                )
            }
        }

        item {
            // Fila 2: Finanzas, RRHH, Logística
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ModuleCard(
                    modifier = Modifier.weight(1f),
                    title = "Finanzas",
                    subtitle = "Facturas y cuentas",
                    icon = Icons.Default.AccountBalance,
                    color = SuccessColor,
                    onClick = { navController.navigate(Routes.FINANCE) }
                )
                ModuleCard(
                    modifier = Modifier.weight(1f),
                    title = "RRHH",
                    subtitle = "Empleados",
                    icon = Icons.Default.Groups,
                    color = WarningColor,
                    onClick = { navController.navigate(Routes.HR) }
                )
                ModuleCard(
                    modifier = Modifier.weight(1f),
                    title = "Logística",
                    subtitle = "Envíos",
                    icon = Icons.Default.LocalShipping,
                    color = StockLow,
                    onClick = { navController.navigate(Routes.LOGISTICS) }
                )
            }
        }

        // Acceso rápido horizontal
        item {
            Text(
                "Acceso rápido",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    QuickAccessCard(
                        title = "Nueva Venta",
                        icon = Icons.Default.PointOfSale,
                        color = Accent,
                        onClick = { navController.navigate(Routes.POS) }
                    )
                }
                item {
                    QuickAccessCard(
                        title = "Nuevo Producto",
                        icon = Icons.Default.AddBox,
                        color = Primary,
                        onClick = { navController.navigate(Routes.productForm()) }
                    )
                }
                item {
                    QuickAccessCard(
                        title = "Nuevo Cliente",
                        icon = Icons.Default.PersonAdd,
                        color = InfoColor,
                        onClick = { navController.navigate(Routes.customerForm()) }
                    )
                }
                item {
                    QuickAccessCard(
                        title = "Asistente IA",
                        icon = Icons.Default.AutoAwesome,
                        color = PrimaryLight,
                        onClick = { navController.navigate(Routes.AI_ASSISTANT) }
                    )
                }
                item {
                    QuickAccessCard(
                        title = "Notificaciones",
                        icon = Icons.Default.Notifications,
                        color = WarningColor,
                        onClick = { navController.navigate(Routes.NOTIFICATIONS) }
                    )
                }
            }
        }

        // Últimas ventas
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Últimas ventas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = { navController.navigate(Routes.SALES_HISTORY) }) {
                    Text("Ver todas", color = Accent)
                }
            }
        }

        if (data.recentSales.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "No hay ventas recientes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        } else {
            items(data.recentSales) { sale ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(sale.number, fontWeight = FontWeight.Bold)
                            Text(sale.customer, style = MaterialTheme.typography.bodySmall)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(sale.total.toMXN(), fontWeight = FontWeight.Bold, color = SuccessColor)
                            Text(sale.date.toShortDate(), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModuleCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, title, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun KpiCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, title, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun QuickAccessCard(
    title: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, title, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Contenidos placeholder para cada tab
@Composable
fun InventoryTabContent(modifier: Modifier = Modifier, navController: NavController) {
    LaunchedEffect(Unit) { navController.navigate(Routes.INVENTORY) }
    Box(modifier = modifier.fillMaxSize()) {
        com.axtarget.processnova.ui.components.LoadingScreen()
    }
}

@Composable
fun PosTabContent(modifier: Modifier = Modifier, navController: NavController) {
    LaunchedEffect(Unit) { navController.navigate(Routes.POS) }
    Box(modifier = modifier.fillMaxSize()) {
        com.axtarget.processnova.ui.components.LoadingScreen()
    }
}

@Composable
fun CrmTabContent(modifier: Modifier = Modifier, navController: NavController) {
    LaunchedEffect(Unit) { navController.navigate(Routes.CRM) }
    Box(modifier = modifier.fillMaxSize()) {
        com.axtarget.processnova.ui.components.LoadingScreen()
    }
}

@Composable
fun FinanceTabContent(modifier: Modifier = Modifier, navController: NavController) {
    LaunchedEffect(Unit) { navController.navigate(Routes.FINANCE) }
    Box(modifier = modifier.fillMaxSize()) {
        com.axtarget.processnova.ui.components.LoadingScreen()
    }
}

