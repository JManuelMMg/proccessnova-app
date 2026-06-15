package com.axtarget.processnova.ui.sales

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
import com.axtarget.processnova.ProcessNovaApp
import com.axtarget.processnova.core.Result
import com.axtarget.processnova.core.toMXN
import com.axtarget.processnova.core.toShortDate
import com.axtarget.processnova.data.models.SaleDetail
import com.axtarget.processnova.data.repository.SalesRepository
import com.axtarget.processnova.ui.navigation.Routes
import com.axtarget.processnova.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesHistoryScreen(navController: NavController, showTopBar: Boolean = false) {
    val scope = rememberCoroutineScope()
    val repository = remember { SalesRepository(ProcessNovaApp.instance.getDatabase().saleDao()) }
    var sales by remember { mutableStateOf<List<SaleDetail>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        when (val result = repository.getSalesHistory()) {
            is Result.Success -> sales = result.data
            else -> {}
        }
        isLoading = false
    }

    if (showTopBar) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Historial de ventas") },
                    navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryDark, titleContentColor = MaterialTheme.colorScheme.onPrimary, navigationIconContentColor = MaterialTheme.colorScheme.onPrimary)
                )
            }
        ) { padding ->
            SalesHistoryContent(
                modifier = Modifier.padding(padding).fillMaxSize(),
                isLoading = isLoading,
                sales = sales,
                navController = navController
            )
        }
    } else {
        SalesHistoryContent(
            modifier = Modifier.fillMaxSize(),
            isLoading = isLoading,
            sales = sales,
            navController = navController
        )
    }
}

@Composable
private fun SalesHistoryContent(
    modifier: Modifier,
    isLoading: Boolean,
    sales: List<SaleDetail>,
    navController: NavController
) {
    if (isLoading) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    } else {
        LazyColumn(modifier = modifier, contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(sales) { sale ->
                Card(modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Routes.saleDetail(sale.id)) }, shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Receipt, null, tint = PrimaryDark, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("#${sale.number}", fontWeight = FontWeight.Bold, color = PrimaryDark)
                            Text(sale.customer.ifBlank { "Sin cliente" }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Text(sale.date.toShortDate(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(sale.total.toMXN(), fontWeight = FontWeight.Bold, color = PrimaryDark)
                            val statusColor = when (sale.status) {
                                "completed" -> SuccessColor
                                "cancelled" -> ErrorColor
                                else -> WarningColor
                            }
                            AssistChip(onClick = { }, label = { Text(sale.status) }, colors = AssistChipDefaults.assistChipColors(containerColor = statusColor.copy(alpha = 0.1f), labelColor = statusColor))
                        }
                    }
                }
            }
            if (sales.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ReceiptLong, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No hay ventas registradas", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}
