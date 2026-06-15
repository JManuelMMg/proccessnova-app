package com.axtarget.processnova.ui.hr

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.axtarget.processnova.core.Result
import com.axtarget.processnova.core.toMXN
import com.axtarget.processnova.core.toShortDate
import com.axtarget.processnova.data.models.Employee
import com.axtarget.processnova.data.repository.HrRepository
import com.axtarget.processnova.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailScreen(employeeId: Int, navController: NavController) {
    val repository = remember { HrRepository() }
    var employee by remember { mutableStateOf<Employee?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(employeeId) {
        when (val result = repository.getEmployee(employeeId)) {
            is Result.Success -> employee = result.data
            else -> {}
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del empleado") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryDark, titleContentColor = MaterialTheme.colorScheme.onPrimary, navigationIconContentColor = MaterialTheme.colorScheme.onPrimary)
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (employee != null) {
            val emp = employee!!
            Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = PrimaryDark)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(shape = CircleShape, color = Accent, modifier = Modifier.size(64.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(emp.fullName.take(2).uppercase(), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(emp.fullName, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        Text("${emp.position} • ${emp.department}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    }
                }
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Información personal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryDark)
                        Spacer(modifier = Modifier.height(12.dp))
                        DetailEmpRow("Email", emp.email.ifBlank { "N/A" })
                        DetailEmpRow("Teléfono", emp.phone.ifBlank { "N/A" })
                        DetailEmpRow("RFC", emp.rfc.ifBlank { "N/A" })
                        DetailEmpRow("CURP", emp.curp.ifBlank { "N/A" })
                        DetailEmpRow("NSS", emp.nss.ifBlank { "N/A" })
                    }
                }
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Información laboral", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryDark)
                        Spacer(modifier = Modifier.height(12.dp))
                        DetailEmpRow("Fecha de ingreso", emp.hireDate.toShortDate())
                        DetailEmpRow("Periodicidad", emp.payFrequency)
                        DetailEmpRow("Salario", emp.salary.toMXN())
                        DetailEmpRow("CLABE", emp.clabe.ifBlank { "N/A" })
                        DetailEmpRow("Estado", emp.status)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailEmpRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}



