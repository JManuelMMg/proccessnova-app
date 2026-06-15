package com.axtarget.processnova.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axtarget.processnova.ProcessNovaApp
import com.axtarget.processnova.core.Result
import com.axtarget.processnova.data.repository.AuthRepository
import com.axtarget.processnova.ui.theme.PrimaryDark
import com.axtarget.processnova.ui.theme.SuccessColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val authRepository = remember {
        AuthRepository(ProcessNovaApp.instance.sessionManager)
    }
    val focusManager = LocalFocusManager.current

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password1 by remember { mutableStateOf("") }
    var password2 by remember { mutableStateOf("") }
    
    var orgName by remember { mutableStateOf("") }
    var rfc by remember { mutableStateOf("") }
    var razonSocial by remember { mutableStateOf("") }
    var regimenFiscal by remember { mutableStateOf("601") }
    var codigoPostal by remember { mutableStateOf("") }
    var branchName by remember { mutableStateOf("Matriz") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta corporativa") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ProcessNova",
                style = MaterialTheme.typography.displayMedium,
                color = PrimaryDark,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Registro de Organización (SAT Ready)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text("Datos de la Empresa", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryDark, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp))

            OutlinedTextField(
                value = orgName, onValueChange = { orgName = it },
                label = { Text("Nombre comercial *") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = razonSocial, onValueChange = { razonSocial = it },
                label = { Text("Razón Social *") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = rfc, onValueChange = { rfc = it.uppercase() },
                    label = { Text("RFC *") },
                    singleLine = true, modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = codigoPostal, onValueChange = { if(it.length <= 5) codigoPostal = it },
                    label = { Text("C.P. *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true, modifier = Modifier.weight(0.6f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = regimenFiscal, onValueChange = { regimenFiscal = it },
                label = { Text("Régimen Fiscal (Clave SAT) *") },
                placeholder = { Text("Ej. 601") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Cuenta de Administrador", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryDark, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp))

            OutlinedTextField(
                value = username, onValueChange = { username = it },
                label = { Text("Usuario *") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email corporativo *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password1, onValueChange = { password1 = it },
                label = { Text("Contraseña *") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password2, onValueChange = { password2 = it },
                label = { Text("Confirmar contraseña *") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            errorMessage?.let {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(12.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            successMessage?.let {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SuccessColor.copy(alpha = 0.1f))) {
                    Text(text = it, color = SuccessColor, modifier = Modifier.padding(12.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    when {
                        orgName.isBlank() || razonSocial.isBlank() || rfc.isBlank() || codigoPostal.isBlank() -> 
                            errorMessage = "Todos los campos de la empresa son obligatorios"
                        username.isBlank() || email.isBlank() -> 
                            errorMessage = "Los datos de acceso son obligatorios"
                        password1.length < 8 -> 
                            errorMessage = "La contraseña debe tener al menos 8 caracteres"
                        password1 != password2 -> 
                            errorMessage = "Las contraseñas no coinciden"
                        else -> {
                            isLoading = true
                            errorMessage = null
                            scope.launch {
                                val result = authRepository.register(
                                    username = username,
                                    email = email,
                                    password1 = password1,
                                    password2 = password2,
                                    organizationName = orgName,
                                    rfc = rfc,
                                    razonSocial = razonSocial,
                                    regimenFiscal = regimenFiscal,
                                    codigoPostal = codigoPostal,
                                    branchName = branchName
                                )
                                when (result) {
                                    is Result.Success<*> -> {
                                        isLoading = false
                                        successMessage = result.data.toString()
                                        kotlinx.coroutines.delay(2000)
                                        onRegisterSuccess()
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
                modifier = Modifier.fillMaxWidth().height(54.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Dar de alta empresa", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


