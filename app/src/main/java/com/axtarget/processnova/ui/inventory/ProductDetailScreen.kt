package com.axtarget.processnova.ui.inventory

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
import com.axtarget.processnova.ProcessNovaApp
import com.axtarget.processnova.core.Result
import com.axtarget.processnova.core.toMXN
import com.axtarget.processnova.data.models.Product
import com.axtarget.processnova.data.models.StockStatus
import com.axtarget.processnova.data.repository.InventoryRepository
import com.axtarget.processnova.ui.navigation.Routes
import com.axtarget.processnova.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val repository = remember { InventoryRepository(ProcessNovaApp.instance.getDatabase().productDao()) }
    var product by remember { mutableStateOf<Product?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(productId) {
        isLoading = true
        when (val result = repository.getProduct(productId)) {
            is Result.Success -> product = result.data
            is Result.Error -> errorMessage = result.message
            else -> {}
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    if (product != null) {
                        IconButton(onClick = {
                            navController.navigate(Routes.productForm(productId))
                        }) {
                            Icon(Icons.Default.Edit, "Editar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryDark,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (product != null) {
            val p = product!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header con nombre y precio
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryDark)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = p.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = p.priceWithTax.toMXN(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = Accent,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Precio con IVA (${p.taxRate}%)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                    }
                }

                // Stock
                val stockColor = when (p.stockStatus) {
                    StockStatus.OK -> StockOk
                    StockStatus.LOW -> StockLow
                    StockStatus.CRITICAL -> StockCritical
                }
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Inventario", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryDark)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            StockInfo("Stock actual", "${p.currentStock}", stockColor)
                            StockInfo("Stock mínimo", "${p.minStock}", StockLow)
                            StockInfo("Stock máximo", "${p.maxStock}", InfoColor)
                        }
                    }
                }

                // Información general
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Información general", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryDark)
                        Spacer(modifier = Modifier.height(12.dp))
                        DetailRow("SKU", p.sku.ifBlank { "N/A" })
                        DetailRow("Código de barras", p.barcode.ifBlank { "N/A" })
                        DetailRow("Categoría", p.category.ifBlank { "Sin categoría" })
                        DetailRow("Tipo", p.type)
                        if (p.description.isNotBlank()) {
                            DetailRow("Descripción", p.description)
                        }
                    }
                }

                // Precios
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Precios", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryDark)
                        Spacer(modifier = Modifier.height(12.dp))
                        DetailRow("Precio de venta", p.salePrice.toMXN())
                        DetailRow("Precio de costo", p.costPrice.toMXN())
                        DetailRow("Tasa de IVA", "${p.taxRate}%")
                        DetailRow("Precio con IVA", p.priceWithTax.toMXN())
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Error, null, modifier = Modifier.size(64.dp), tint = ErrorColor)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(errorMessage ?: "Producto no encontrado", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun StockInfo(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}



