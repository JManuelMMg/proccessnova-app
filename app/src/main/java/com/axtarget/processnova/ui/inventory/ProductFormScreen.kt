package com.axtarget.processnova.ui.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.axtarget.processnova.ProcessNovaApp
import com.axtarget.processnova.core.Result
import com.axtarget.processnova.data.models.Product
import com.axtarget.processnova.data.models.QuickCreateRequest
import com.axtarget.processnova.data.repository.InventoryRepository
import com.axtarget.processnova.ui.theme.PrimaryDark
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    productId: Int?,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val repository = remember { InventoryRepository(ProcessNovaApp.instance.getDatabase().productDao()) }
    val isEditing = productId != null

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var salePrice by remember { mutableStateOf("") }
    var costPrice by remember { mutableStateOf("") }
    var currentStock by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Si es edición, cargar datos
    LaunchedEffect(productId) {
        if (productId != null) {
            when (val result = repository.getProduct(productId)) {
                is Result.Success -> {
                    val p = result.data
                    name = p.name
                    description = p.description
                    sku = p.sku
                    barcode = p.barcode
                    salePrice = p.salePrice.toString()
                    costPrice = p.costPrice.toString()
                    currentStock = p.currentStock.toString()
                    minStock = p.minStock.toString()
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar producto" else "Nuevo producto") },
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del producto *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                shape = RoundedCornerShape(12.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = sku,
                    onValueChange = { sku = it },
                    label = { Text("SKU") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = barcode,
                    onValueChange = { barcode = it },
                    label = { Text("Código de barras") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Text("Precios", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryDark)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = salePrice,
                    onValueChange = { salePrice = it },
                    label = { Text("Precio venta *") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = costPrice,
                    onValueChange = { costPrice = it },
                    label = { Text("Precio costo") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Text("Inventario", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryDark)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = currentStock,
                    onValueChange = { currentStock = it },
                    label = { Text("Stock actual") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = minStock,
                    onValueChange = { minStock = it },
                    label = { Text("Stock mínimo") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            if (errorMessage != null) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            if (successMessage != null) {
                Text(successMessage!!, color = com.axtarget.processnova.ui.theme.SuccessColor, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    when {
                        name.isBlank() -> errorMessage = "El nombre es obligatorio"
                        salePrice.isBlank() -> errorMessage = "El precio de venta es obligatorio"
                        else -> {
                            isLoading = true
                            errorMessage = null
                            scope.launch {
                                val request = QuickCreateRequest(
                                    name = name,
                                    sale_price = salePrice.toDoubleOrNull() ?: 0.0,
                                    cost_price = costPrice.toDoubleOrNull() ?: 0.0,
                                    sku = sku,
                                    barcode = barcode
                                )
                                val result = if (isEditing && productId != null) {
                                    repository.updateProduct(productId, Product(
                                        id = productId,
                                        name = name,
                                        description = description,
                                        sku = sku,
                                        barcode = barcode,
                                        salePrice = salePrice.toDoubleOrNull() ?: 0.0,
                                        costPrice = costPrice.toDoubleOrNull() ?: 0.0,
                                        currentStock = currentStock.toIntOrNull() ?: 0,
                                        minStock = minStock.toIntOrNull() ?: 0
                                    ))
                                } else {
                                    repository.quickCreate(request)
                                }
                                when (result) {
                                    is Result.Success -> {
                                        isLoading = false
                                        successMessage = if (isEditing) "Producto actualizado" else "Producto creado"
                                        kotlinx.coroutines.delay(1000)
                                        navController.popBackStack()
                                    }
                                    is Result.Error -> {
                                        isLoading = false
                                        errorMessage = result.message
                                    }
                                    else -> {}
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Text(if (isEditing) "Guardar cambios" else "Crear producto", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}



