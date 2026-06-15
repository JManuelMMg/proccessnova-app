package com.axtarget.processnova.ui.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.axtarget.processnova.ProcessNovaApp
import com.axtarget.processnova.core.toMXN
import com.axtarget.processnova.core.truncate
import com.axtarget.processnova.core.Result
import com.axtarget.processnova.data.models.Product
import com.axtarget.processnova.data.models.StockStatus
import com.axtarget.processnova.data.repository.InventoryRepository
import com.axtarget.processnova.ui.navigation.Routes
import com.axtarget.processnova.ui.theme.*
import com.axtarget.processnova.core.scanner.BarcodeScannerDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(navController: NavController, showTopBar: Boolean = false) {
    val scope = rememberCoroutineScope()
    val repository = remember { InventoryRepository(ProcessNovaApp.instance.getDatabase().productDao()) }

    var searchQuery by remember { mutableStateOf("") }
    var showScanner by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf("Todas") }

    LaunchedEffect(Unit) {
        isLoading = true
        when (val result = repository.getProducts()) {
            is Result.Success<*> -> products = result.data as List<Product>
            is Result.Error -> errorMessage = result.message
            else -> {}
        }
        isLoading = false
    }

    if (showScanner) {
        BarcodeScannerDialog(
            onBarcodeDetected = { barcode ->
                showScanner = false
                searchQuery = barcode
            },
            onDismiss = { showScanner = false }
        )
    } else {
        if (showTopBar) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Inventario") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = PrimaryDark,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { navController.navigate(Routes.productForm()) },
                        containerColor = Accent
                    ) {
                        Icon(Icons.Default.Add, "Nuevo producto", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            ) { padding ->
                InventoryContent(
                    modifier = Modifier.padding(padding).fillMaxSize(),
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onScannerClick = { showScanner = true },
                    selectedCategory = selectedCategory,
                    onCategorySelect = { selectedCategory = it },
                    errorMessage = errorMessage,
                    isLoading = isLoading,
                    products = products,
                    navController = navController
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                InventoryContent(
                    modifier = Modifier.fillMaxSize(),
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onScannerClick = { showScanner = true },
                    selectedCategory = selectedCategory,
                    onCategorySelect = { selectedCategory = it },
                    errorMessage = errorMessage,
                    isLoading = isLoading,
                    products = products,
                    navController = navController
                )
                
                FloatingActionButton(
                    onClick = { navController.navigate(Routes.productForm()) },
                    containerColor = Accent,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, "Nuevo producto", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@Composable
private fun InventoryContent(
    modifier: Modifier,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onScannerClick: () -> Unit,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    products: List<Product>,
    navController: NavController
) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = searchQuery, onValueChange = onSearchQueryChange,
                placeholder = { Text("Buscar producto...") },
                leadingIcon = { Icon(Icons.Default.Search, "Buscar") },
                singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onScannerClick,
                modifier = Modifier.size(48.dp).clip(CircleShape).background(PrimaryDark)
            ) {
                Icon(Icons.Default.QrCodeScanner, "Escanear", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }

        LazyRow(modifier = Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("Todas", "Productos", "Servicios", "Digitales")) { category ->
                FilterChip(selected = selectedCategory == category, onClick = { onCategorySelect(category) }, label = { Text(category) })
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (errorMessage != null) {
            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = ErrorColor.copy(alpha = 0.1f))) {
                Text(text = errorMessage, color = ErrorColor, modifier = Modifier.padding(12.dp))
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val filtered = products.filter {
                    searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) || it.sku.contains(searchQuery, ignoreCase = true) || it.barcode.contains(searchQuery, ignoreCase = true)
                }
                items(filtered) { product ->
                    ProductCard(
                        product = product,
                        onClick = { navController.navigate(Routes.productDetail(product.id)) }
                    )
                }
                if (filtered.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Inventory2, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("No se encontraron productos", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    val stockColor = when (product.stockStatus) {
        StockStatus.OK -> StockOk
        StockStatus.LOW -> StockLow
        StockStatus.CRITICAL -> StockCritical
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(stockColor))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name.truncate(30), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text = "SKU: ${product.sku.ifBlank { "N/A" }}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Text(text = "Stock: ${product.currentStock} / min: ${product.minStock}", style = MaterialTheme.typography.bodySmall, color = stockColor, fontWeight = FontWeight.Medium)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = product.priceWithTax.toMXN(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryDark)
                Text(text = "+ IVA", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        }
    }
}
