package com.axtarget.processnova.ui.base

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.axtarget.processnova.ProcessNovaApp
import com.axtarget.processnova.data.repository.AuthRepository
import com.axtarget.processnova.ui.ai.AiAssistantScreen
import com.axtarget.processnova.ui.crm.CrmScreen
import com.axtarget.processnova.ui.dashboard.DashboardWebScreen
import com.axtarget.processnova.ui.empresa.EmpresaScreen
import com.axtarget.processnova.ui.finance.FinanceScreen
import com.axtarget.processnova.ui.hr.HrScreen
import com.axtarget.processnova.ui.inventory.InventoryScreen
import com.axtarget.processnova.ui.landing.LandingScreen
import com.axtarget.processnova.ui.logistics.LogisticsScreen
import com.axtarget.processnova.ui.notifications.NotificationsScreen
import com.axtarget.processnova.ui.sales.PosScreen
import com.axtarget.processnova.ui.sales.SalesHistoryScreen
import com.axtarget.processnova.ui.users.ProfileScreen
import com.axtarget.processnova.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseLayoutScreen(navController: NavController) {
    val sessionManager = ProcessNovaApp.instance.sessionManager
    val authRepository = remember { AuthRepository(sessionManager) }
    val userName by sessionManager.userName.collectAsState(initial = "")
    val orgName by sessionManager.orgName.collectAsState(initial = "")
    val branchName by sessionManager.branchName.collectAsState(initial = "Principal")

    var selectedModule by remember { mutableStateOf("dashboard") }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val sidebarItems = listOf(
        SidebarItem("dashboard", "Inicio", Icons.Default.Home),
        SidebarItem("pos", "POS", Icons.Default.PointOfSale),
        SidebarItem("sales_history", "Historial de Ventas", Icons.Default.Receipt),
        SidebarItem("inventory", "Inventario", Icons.Default.Inventory),
        SidebarItem("crm", "CRM", Icons.Default.People),
        SidebarItem("finance", "Finanzas", Icons.Default.AccountBalance),
        SidebarItem("hr", "RRHH", Icons.Default.Groups),
        SidebarItem("logistics", "Logística", Icons.Default.LocalShipping),
        SidebarItem("ai", "Asistente IA", Icons.Default.AutoAwesome),
        SidebarItem("notifications", "Correo", Icons.Default.Email),
        SidebarItem("landing", "Landing", Icons.Default.Public),
        SidebarItem("empresa", "Empresa", Icons.Default.Business),
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF0B1628),
                drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
                modifier = Modifier.width(300.dp)
            ) {
                SidebarContent(
                    userName = userName,
                    orgName = orgName,
                    branchName = branchName,
                    selectedModule = selectedModule,
                    sidebarItems = sidebarItems,
                    onModuleSelect = { moduleId ->
                        selectedModule = moduleId
                        scope.launch { drawerState.close() }
                    },
                    onLogoutClick = { showLogoutDialog = true }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            when (selectedModule) {
                                "dashboard" -> "Dashboard"
                                "pos" -> "Punto de Venta"
                                "sales_history" -> "Historial de Ventas"
                                "inventory" -> "Inventario"
                                "crm" -> "CRM"
                                "finance" -> "Finanzas"
                                "hr" -> "Recursos Humanos"
                                "logistics" -> "Logística"
                                "ai" -> "Asistente IA"
                                "notifications" -> "Notificaciones"
                                "landing" -> "Landing"
                                "empresa" -> "Empresa"
                                else -> "ProcessNova"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Menú", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("profile") }) {
                            Icon(Icons.Default.Person, "Perfil", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PrimaryDark,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF5F5F5))
            ) {
                when (selectedModule) {
                    "dashboard" -> DashboardWebScreen(navController)
                    "pos" -> PosScreen(navController)
                    "sales_history" -> SalesHistoryScreen(navController)
                    "inventory" -> InventoryScreen(navController)
                    "crm" -> CrmScreen(navController)
                    "finance" -> FinanceScreen(navController)
                    "hr" -> HrScreen(navController)
                    "logistics" -> LogisticsScreen(navController)
                    "ai" -> AiAssistantScreen(navController)
                    "notifications" -> NotificationsScreen(navController)
                    "landing" -> LandingScreen(navController)
                    "empresa" -> EmpresaScreen(navController)
                    "profile" -> ProfileScreen(
                        navController = navController,
                        onLogout = {
                            scope.launch {
                                authRepository.logout()
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    scope.launch {
                        authRepository.logout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }) {
                    Text("Cerrar sesión", color = Color(0xFFE74C3C))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun SidebarContent(
    userName: String,
    orgName: String,
    branchName: String,
    selectedModule: String,
    sidebarItems: List<SidebarItem>,
    onModuleSelect: (String) -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1A3C2E), Color(0xFF0B1628))
                    )
                )
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Accent,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("P", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("ProcessNova", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            if (orgName.isNotBlank()) {
                Text(orgName, fontSize = 14.sp, color = Color(0xFF7A9BB5),
                    modifier = Modifier.padding(top = 8.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text("📍 $branchName", fontSize = 12.sp, color = Color(0xFF4A6580),
                modifier = Modifier.padding(top = 4.dp))
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(sidebarItems) { item ->
                SidebarMenuItem(
                    item = item,
                    isSelected = selectedModule == item.id,
                    onClick = { onModuleSelect(item.id) }
                )
            }

            item {
                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.08f),
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Text(
                    "ADMINISTRACIÓN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4A6580),
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
            item {
                SidebarMenuItem(
                    item = SidebarItem("profile", "Mi Perfil", Icons.Default.Person),
                    isSelected = selectedModule == "profile",
                    onClick = { onModuleSelect("profile") }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF020812))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Accent,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            userName.take(2).uppercase().ifBlank { "U" },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    userName.ifBlank { "Usuario" },
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onLogoutClick() }
                    .background(Color(0xFFE74C3C).copy(alpha = 0.1f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, "Cerrar sesión",
                    tint = Color(0xFFE74C3C), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Cerrar Sesión", fontSize = 14.sp, color = Color(0xFFE74C3C), fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun SidebarMenuItem(
    item: SidebarItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) Accent.copy(alpha = 0.2f) else Color.Transparent
    val textColor = if (isSelected) Color.White else Color(0xFF7A9BB5)
    val iconColor = if (isSelected) Accent else Color(0xFF7A9BB5)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(item.icon, item.label, tint = iconColor, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            item.label,
            fontSize = 14.sp,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private data class SidebarItem(
    val id: String,
    val label: String,
    val icon: ImageVector
)
