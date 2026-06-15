package com.axtarget.processnova.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.axtarget.processnova.ui.auth.LoginScreen
import com.axtarget.processnova.ui.auth.RegisterScreen
import com.axtarget.processnova.ui.dashboard.DashboardScreen
import com.axtarget.processnova.ui.dashboard.DashboardWebScreen
import com.axtarget.processnova.ui.inventory.InventoryScreen
import com.axtarget.processnova.ui.inventory.ProductDetailScreen
import com.axtarget.processnova.ui.inventory.ProductFormScreen
import com.axtarget.processnova.ui.sales.PosScreen
import com.axtarget.processnova.ui.sales.SaleDetailScreen
import com.axtarget.processnova.ui.sales.SalesHistoryScreen
import com.axtarget.processnova.ui.crm.CrmScreen
import com.axtarget.processnova.ui.crm.CustomerDetailScreen
import com.axtarget.processnova.ui.crm.CustomerFormScreen
import com.axtarget.processnova.ui.finance.FinanceScreen
import com.axtarget.processnova.ui.finance.InvoiceDetailScreen
import com.axtarget.processnova.ui.hr.HrScreen
import com.axtarget.processnova.ui.hr.EmployeeDetailScreen
import com.axtarget.processnova.ui.logistics.LogisticsScreen
import com.axtarget.processnova.ui.logistics.ShipmentDetailScreen
import com.axtarget.processnova.ui.notifications.NotificationsScreen
import com.axtarget.processnova.ui.notifications.ComposeNotificationScreen
import com.axtarget.processnova.ui.ai.AiAssistantScreen
import com.axtarget.processnova.ui.users.ProfileScreen
import com.axtarget.processnova.ui.landing.LandingScreen
import com.axtarget.processnova.ui.empresa.EmpresaScreen
import com.axtarget.processnova.ui.base.BaseLayoutScreen

/**
 * Rutas de navegación de la aplicación.
 */
object Routes {
    // Auth
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Principal
    const val DASHBOARD = "dashboard"
    const val PROFILE = "profile"

    // Módulos principales
    const val INVENTORY = "inventory"
    const val POS = "pos"
    const val CRM = "crm"
    const val FINANCE = "finance"
    const val HR = "hr"
    const val LOGISTICS = "logistics"
    const val NOTIFICATIONS = "notifications"
    const val AI_ASSISTANT = "ai_assistant"

    // Vistas públicas (landing, empresa)
    const val LANDING = "landing"
    const val EMPRESA = "empresa"

    // Pantallas de detalle/formulario
    const val PRODUCT_DETAIL = "product_detail/{productId}"
    const val PRODUCT_FORM = "product_form?productId={productId}"
    const val CUSTOMER_DETAIL = "customer_detail/{customerId}"
    const val CUSTOMER_FORM = "customer_form?customerId={customerId}"
    const val SALE_DETAIL = "sale_detail/{saleId}"
    const val SALES_HISTORY = "sales_history"
    const val INVOICE_DETAIL = "invoice_detail/{invoiceId}"
    const val EMPLOYEE_DETAIL = "employee_detail/{employeeId}"
    const val SHIPMENT_DETAIL = "shipment_detail/{shipmentId}"
    const val COMPOSE_NOTIFICATION = "compose_notification"

    // Helpers
    fun productDetail(productId: Int) = "product_detail/$productId"
    fun productForm(productId: Int? = null) = if (productId != null) "product_form?productId=$productId" else "product_form"
    fun customerDetail(customerId: Int) = "customer_detail/$customerId"
    fun customerForm(customerId: Int? = null) = if (customerId != null) "customer_form?customerId=$customerId" else "customer_form"
    fun saleDetail(saleId: Int) = "sale_detail/$saleId"
    fun invoiceDetail(invoiceId: Int) = "invoice_detail/$invoiceId"
    fun employeeDetail(employeeId: Int) = "employee_detail/$employeeId"
    fun shipmentDetail(shipmentId: Int) = "shipment_detail/$shipmentId"
}

/**
 * Navegación principal de ProcessNova.
 * Gestiona todas las rutas y transiciones entre pantallas.
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.LOGIN
) {
    val sessionManager = com.axtarget.processnova.ProcessNovaApp.instance.sessionManager
    val isLoggedIn by sessionManager.isLoggedIn.collectAsState(initial = null)

    // Detectar cierre de sesión global y redirigir
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == false) {
            navController.navigate(Routes.LANDING) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Dashboard principal con sidebar (base.html)
        composable(Routes.DASHBOARD) {
            BaseLayoutScreen(navController = navController)
        }

        // Perfil
        composable(Routes.PROFILE) {
            ProfileScreen(
                navController = navController,
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Inventario
        composable(Routes.INVENTORY) {
            InventoryScreen(navController = navController)
        }
        composable(
            Routes.PRODUCT_DETAIL,
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            ProductDetailScreen(
                productId = productId,
                navController = navController
            )
        }
        composable(
            Routes.PRODUCT_FORM,
            arguments = listOf(navArgument("productId") { type = NavType.IntType; defaultValue = -1 })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: -1
            ProductFormScreen(
                productId = if (productId == -1) null else productId,
                navController = navController
            )
        }

        // POS
        composable(Routes.POS) {
            PosScreen(navController = navController)
        }

        // Ventas - Historial y detalle
        composable(Routes.SALES_HISTORY) {
            SalesHistoryScreen(navController = navController)
        }
        composable(
            Routes.SALE_DETAIL,
            arguments = listOf(navArgument("saleId") { type = NavType.IntType })
        ) { backStackEntry ->
            val saleId = backStackEntry.arguments?.getInt("saleId") ?: 0
            SaleDetailScreen(
                saleId = saleId,
                navController = navController
            )
        }

        // CRM
        composable(Routes.CRM) {
            CrmScreen(navController = navController)
        }
        composable(
            Routes.CUSTOMER_DETAIL,
            arguments = listOf(navArgument("customerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getInt("customerId") ?: 0
            CustomerDetailScreen(
                customerId = customerId,
                navController = navController
            )
        }
        composable(
            Routes.CUSTOMER_FORM,
            arguments = listOf(navArgument("customerId") { type = NavType.IntType; defaultValue = -1 })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getInt("customerId") ?: -1
            CustomerFormScreen(
                customerId = if (customerId == -1) null else customerId,
                navController = navController
            )
        }

        // Finanzas
        composable(Routes.FINANCE) {
            FinanceScreen(navController = navController)
        }
        composable(
            Routes.INVOICE_DETAIL,
            arguments = listOf(navArgument("invoiceId") { type = NavType.IntType })
        ) { backStackEntry ->
            val invoiceId = backStackEntry.arguments?.getInt("invoiceId") ?: 0
            InvoiceDetailScreen(
                invoiceId = invoiceId,
                navController = navController
            )
        }

        // RRHH
        composable(Routes.HR) {
            HrScreen(navController = navController)
        }
        composable(
            Routes.EMPLOYEE_DETAIL,
            arguments = listOf(navArgument("employeeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val employeeId = backStackEntry.arguments?.getInt("employeeId") ?: 0
            EmployeeDetailScreen(
                employeeId = employeeId,
                navController = navController
            )
        }

        // Logística
        composable(Routes.LOGISTICS) {
            LogisticsScreen(navController = navController)
        }
        composable(
            Routes.SHIPMENT_DETAIL,
            arguments = listOf(navArgument("shipmentId") { type = NavType.IntType })
        ) { backStackEntry ->
            val shipmentId = backStackEntry.arguments?.getInt("shipmentId") ?: 0
            ShipmentDetailScreen(
                shipmentId = shipmentId,
                navController = navController
            )
        }

        // Notificaciones
        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(navController = navController)
        }
        composable(Routes.COMPOSE_NOTIFICATION) {
            ComposeNotificationScreen(navController = navController)
        }

        // IA
        composable(Routes.AI_ASSISTANT) {
            AiAssistantScreen(navController = navController)
        }

        // Landing
        composable(Routes.LANDING) {
            LandingScreen(navController = navController)
        }

        // Empresa
        composable(Routes.EMPRESA) {
            EmpresaScreen(navController = navController)
        }
    }
}
