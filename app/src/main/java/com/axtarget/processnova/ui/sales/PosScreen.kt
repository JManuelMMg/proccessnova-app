package com.axtarget.processnova.ui.sales

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.axtarget.processnova.ProcessNovaApp
import com.axtarget.processnova.core.toMXN
import com.axtarget.processnova.core.Result
import com.axtarget.processnova.data.models.*
import com.axtarget.processnova.data.repository.SalesRepository
import com.axtarget.processnova.ui.theme.*
import com.axtarget.processnova.core.scanner.BarcodeScannerDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosScreen(navController: NavController, showTopBar: Boolean = false) {
    val scope = rememberCoroutineScope()
    val repository = remember {
        SalesRepository(ProcessNovaApp.instance.getDatabase().saleDao())
    }

    var searchQuery by remember { mutableStateOf("") }
    var showScanner by remember { mutableStateOf(false) }
    var cart by remember { mutableStateOf(Cart()) }
    var isLoading by remember { mutableStateOf(false) }
    var showCheckout by remember { mutableStateOf(false) }
    var checkoutResult by remember { mutableStateOf<CheckoutResponse?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (showScanner) {
        BarcodeScannerDialog(
            onBarcodeDetected = { barcode ->
                showScanner = false
                scope.launch {
                    isLoading = true
                    when (val result = repository.scanProduct(ScanProductRequest(barcode))) {
                        is Result.Success -> {
                            val product = result.data
                            val existingItem = cart.items.find { it.productId == product.id }
                            if (existingItem != null) {
                                val updatedItems = cart.items.map {
                                    if (it.productId == product.id)
                                        it.copy(quantity = it.quantity + 1, subtotal = (it.quantity + 1) * it.price)
                                    else it
                                }
                                cart = calculateCart(updatedItems)
                            } else {
                                val newItem = CartItem(
                                    productId = product.id,
                                    name = product.name,
                                    price = product.salePrice,
                                    quantity = 1,
                                    taxRate = product.taxRate,
                                    subtotal = product.salePrice
                                )
                                cart = calculateCart(cart.items + newItem)
                            }
                        }
                        is Result.Error -> errorMessage = result.message
                        else -> {}
                    }
                    isLoading = false
                }
            },
            onDismiss = { showScanner = false }
        )
    } else if (showCheckout && checkoutResult != null) {
        CheckoutTicketScreen(
            checkout = checkoutResult!!,
            onDismiss = {
                showCheckout = false
                checkoutResult = null
                cart = Cart()
            }
        )
    } else {
        if (showTopBar) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Punto de Venta") },
                        actions = {
                            IconButton(onClick = {
                                scope.launch {
                                    repository.clearCart()
                                    cart = Cart()
                                }
                            }) {
                                Icon(Icons.Default.DeleteSweep, "Limpiar carrito")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = PrimaryDark,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            ) { padding ->
                PosContent(
                    modifier = Modifier.padding(padding).fillMaxSize(),
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onScannerClick = { showScanner = true },
                    errorMessage = errorMessage,
                    cart = cart,
                    onCartUpdate = { cart = it },
                    isLoading = isLoading,
                    onCheckoutClick = {
                        scope.launch {
                            isLoading = true
                            val request = CheckoutRequest(
                                sale_type = "pos",
                                payments = listOf(PaymentRequest("efectivo", cart.total))
                            )
                            when (val result = repository.checkout(request)) {
                                is Result.Success -> {
                                    checkoutResult = result.data
                                    showCheckout = true
                                }
                                is Result.Error -> errorMessage = result.message
                                else -> {}
                            }
                            isLoading = false
                        }
                    }
                )
            }
        } else {
            PosContent(
                modifier = Modifier.fillMaxSize(),
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onScannerClick = { showScanner = true },
                errorMessage = errorMessage,
                cart = cart,
                onCartUpdate = { cart = it },
                isLoading = isLoading,
                onCheckoutClick = {
                    scope.launch {
                        isLoading = true
                        val request = CheckoutRequest(
                            sale_type = "pos",
                            payments = listOf(PaymentRequest("efectivo", cart.total))
                        )
                        when (val result = repository.checkout(request)) {
                            is Result.Success -> {
                                checkoutResult = result.data
                                showCheckout = true
                            }
                            is Result.Error -> errorMessage = result.message
                            else -> {}
                        }
                        isLoading = false
                    }
                }
            )
        }
    }
}

@Composable
private fun PosContent(
    modifier: Modifier,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onScannerClick: () -> Unit,
    errorMessage: String?,
    cart: Cart,
    onCartUpdate: (Cart) -> Unit,
    isLoading: Boolean,
    onCheckoutClick: () -> Unit
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Buscar producto...") },
                leadingIcon = { Icon(Icons.Default.Search, "Buscar") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onScannerClick,
                modifier = Modifier.size(48.dp).clip(CircleShape).background(Accent)
            ) {
                Icon(Icons.Default.QrCodeScanner, "Escanear", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }

        if (errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = ErrorColor.copy(alpha = 0.1f))
            ) {
                Text(text = errorMessage, color = ErrorColor, modifier = Modifier.padding(12.dp))
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cart.items) { item ->
                CartItemCard(
                    item = item,
                    onIncrease = {
                        val updated = cart.items.map {
                            if (it.productId == item.productId)
                                it.copy(quantity = it.quantity + 1, subtotal = (it.quantity + 1) * it.price)
                            else it
                        }
                        onCartUpdate(calculateCart(updated))
                    },
                    onDecrease = {
                        val updated = cart.items.map {
                            if (it.productId == item.productId && it.quantity > 1)
                                it.copy(quantity = it.quantity - 1, subtotal = (it.quantity - 1) * it.price)
                            else it
                        }.filter { it.quantity > 0 }
                        onCartUpdate(calculateCart(updated))
                    },
                    onRemove = {
                        val updated = cart.items.filter { it.productId != item.productId }
                        onCartUpdate(calculateCart(updated))
                    }
                )
            }

            if (cart.items.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Carrito vacío", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal", style = MaterialTheme.typography.bodyMedium)
                    Text(cart.subtotal.toMXN(), style = MaterialTheme.typography.bodyMedium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("IVA", style = MaterialTheme.typography.bodyMedium)
                    Text(cart.tax.toMXN(), style = MaterialTheme.typography.bodyMedium)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(cart.total.toMXN(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PrimaryDark)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onCheckoutClick,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = cart.items.isNotEmpty() && !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Payment, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cobrar ${cart.total.toMXN()}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(item: CartItem, onIncrease: () -> Unit, onDecrease: () -> Unit, onRemove: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(text = "${item.price.toMXN()} c/u", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Remove, "Quitar", modifier = Modifier.size(18.dp))
                }
                Text("${item.quantity}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Add, "Agregar", modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(item.subtotal.toMXN(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryDark)
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, "Eliminar", tint = ErrorColor, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun CheckoutTicketScreen(checkout: CheckoutResponse, onDismiss: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(80.dp), tint = SuccessColor)
        Spacer(modifier = Modifier.height(24.dp))
        Text("¡Venta realizada!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = SuccessColor)
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Venta #${checkout.sale_number}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(checkout.total.toMXN(), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = PrimaryDark)
                if (checkout.change > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Cambio: ${checkout.change.toMXN()}", style = MaterialTheme.typography.titleMedium, color = Accent)
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Share, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Compartir")
            }
            Button(onClick = onDismiss, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)) {
                Text("Nueva venta")
            }
        }
    }
}

fun calculateCart(items: List<CartItem>): Cart {
    val subtotal = items.sumOf { it.subtotal }
    val tax = items.sumOf { it.taxAmount }
    return Cart(items = items, subtotal = subtotal, tax = tax, total = subtotal + tax)
}
