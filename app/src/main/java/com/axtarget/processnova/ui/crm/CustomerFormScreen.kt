package com.axtarget.processnova.ui.crm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.axtarget.processnova.core.Result
import com.axtarget.processnova.data.models.Customer
import com.axtarget.processnova.data.repository.CrmRepository
import com.axtarget.processnova.ui.theme.PrimaryDark
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerFormScreen(
    customerId: Int?,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val repository = remember { CrmRepository() }
    val isEditing = customerId != null

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var rfc by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(customerId) {
        if (customerId != null) {
            when (val result = repository.getCustomer(customerId)) {
                is Result.Success -> {
                    val c = result.data
                    name = c.name; email = c.email; phone = c.phone; rfc = c.rfc; address = c.address
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar cliente" else "Nuevo cliente") },
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
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre completo *") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo electrónico") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Teléfono") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = rfc, onValueChange = { rfc = it.uppercase() }, label = { Text("RFC") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth(), minLines = 2, shape = RoundedCornerShape(12.dp))

            if (errorMessage != null) Text(errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            if (successMessage != null) Text(successMessage!!, color = com.axtarget.processnova.ui.theme.SuccessColor, style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    when {
                        name.isBlank() -> errorMessage = "El nombre es obligatorio"
                        else -> {
                            isLoading = true; errorMessage = null
                            scope.launch {
                                val customer = Customer(id = customerId ?: 0, name = name, email = email, phone = phone, rfc = rfc, address = address)
                                val result = if (isEditing && customerId != null) repository.updateCustomer(customerId, customer) else repository.createCustomer(customer)
                                when (result) {
                                    is Result.Success -> { isLoading = false; successMessage = if (isEditing) "Cliente actualizado" else "Cliente creado"; kotlinx.coroutines.delay(1000); navController.popBackStack() }
                                    is Result.Error -> { isLoading = false; errorMessage = result.message }
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
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                else Text(if (isEditing) "Guardar cambios" else "Crear cliente", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}



