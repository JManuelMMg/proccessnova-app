package com.axtarget.processnova.ui.hr

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.axtarget.processnova.core.toMXN
import com.axtarget.processnova.core.toShortDate
import com.axtarget.processnova.core.Result
import com.axtarget.processnova.data.models.*
import com.axtarget.processnova.data.repository.HrRepository
import com.axtarget.processnova.ui.navigation.Routes
import com.axtarget.processnova.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HrScreen(navController: NavController, showTopBar: Boolean = false) {
    val scope = rememberCoroutineScope()
    val repository = remember { HrRepository() }
    var selectedTab by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var employees by remember { mutableStateOf<List<Employee>>(emptyList()) }
    var attendance by remember { mutableStateOf<List<AttendanceRecord>>(emptyList()) }
    var payroll by remember { mutableStateOf<List<PayrollEntry>>(emptyList()) }
    var departments by remember { mutableStateOf<List<Department>>(emptyList()) }

    val tabs = listOf("Empleados", "Asistencia", "Nómina", "Departamentos")

    LaunchedEffect(selectedTab) {
        isLoading = true
        when (selectedTab) {
            0 -> when (val r = repository.getEmployees()) { is Result.Success<*> -> employees = r.data as List<Employee>; else -> {} }
            1 -> when (val r = repository.getAttendance(null, null)) { is Result.Success<*> -> attendance = r.data as List<AttendanceRecord>; else -> {} }
            2 -> when (val r = repository.getPayroll(null)) { is Result.Success<*> -> payroll = r.data as List<PayrollEntry>; else -> {} }
            3 -> when (val r = repository.getDepartments()) { is Result.Success<*> -> departments = r.data as List<Department>; else -> {} }
        }
        isLoading = false
    }

    if (showTopBar) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Recursos Humanos") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryDark, titleContentColor = MaterialTheme.colorScheme.onPrimary, navigationIconContentColor = MaterialTheme.colorScheme.onPrimary)
                )
            },
            floatingActionButton = {
                HrFab(selectedTab) { }
            }
        ) { padding ->
            HrContent(
                modifier = Modifier.padding(padding).fillMaxSize(),
                selectedTab = selectedTab,
                onTabSelect = { selectedTab = it },
                tabs = tabs,
                isLoading = isLoading,
                employees = employees,
                attendance = attendance,
                payroll = payroll,
                departments = departments,
                onEmployeeClick = { navController.navigate(Routes.employeeDetail(it)) }
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            HrContent(
                modifier = Modifier.fillMaxSize(),
                selectedTab = selectedTab,
                onTabSelect = { selectedTab = it },
                tabs = tabs,
                isLoading = isLoading,
                employees = employees,
                attendance = attendance,
                payroll = payroll,
                departments = departments,
                onEmployeeClick = { navController.navigate(Routes.employeeDetail(it)) }
            )
            HrFab(
                selectedTab = selectedTab,
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                onClick = { }
            )
        }
    }
}

@Composable
private fun HrFab(selectedTab: Int, modifier: Modifier = Modifier, onClick: () -> Unit) {
    if (selectedTab == 0) {
        FloatingActionButton(onClick = onClick, containerColor = Accent, modifier = modifier) {
            Icon(Icons.Default.PersonAdd, "Nuevo empleado", tint = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
private fun HrContent(
    modifier: Modifier,
    selectedTab: Int,
    onTabSelect: (Int) -> Unit,
    tabs: List<String>,
    isLoading: Boolean,
    employees: List<Employee>,
    attendance: List<AttendanceRecord>,
    payroll: List<PayrollEntry>,
    departments: List<Department>,
    onEmployeeClick: (Int) -> Unit
) {
    Column(modifier = modifier) {
        ScrollableTabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title -> Tab(selected = selectedTab == index, onClick = { onTabSelect(index) }, text = { Text(title) }) }
        }
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            when (selectedTab) {
                0 -> EmployeeList(employees, onEmployeeClick)
                1 -> AttendanceList(attendance)
                2 -> PayrollList(payroll)
                3 -> DepartmentList(departments)
            }
        }
    }
}

@Composable
fun EmployeeList(employees: List<Employee>, onEmployeeClick: (Int) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(employees) { emp ->
            Card(modifier = Modifier.fillMaxWidth().clickable { onEmployeeClick(emp.id) }, shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = PrimaryDark.copy(alpha = 0.1f), modifier = Modifier.size(44.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(emp.fullName.take(2).uppercase(), fontWeight = FontWeight.Bold, color = PrimaryDark)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(emp.fullName, fontWeight = FontWeight.SemiBold)
                        Text("${emp.position} • ${emp.department}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    AssistChip(onClick = { }, label = { Text(emp.status) })
                }
            }
        }
        if (employees.isEmpty()) { item { EmptyHr("No hay empleados registrados") } }
    }
}

@Composable
fun AttendanceList(records: List<AttendanceRecord>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(records) { rec ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, null, tint = PrimaryDark)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(rec.employeeName, fontWeight = FontWeight.SemiBold)
                        Text(rec.date.toShortDate(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${rec.hoursWorked}h", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryDark)
                        Text("trabajadas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            }
        }
        if (records.isEmpty()) { item { EmptyHr("No hay registros de asistencia") } }
    }
}

@Composable
fun PayrollList(entries: List<PayrollEntry>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(entries) { entry ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(entry.employeeName, fontWeight = FontWeight.SemiBold)
                        AssistChip(onClick = { }, label = { Text(entry.status) })
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Percepciones", style = MaterialTheme.typography.bodySmall, color = SuccessColor)
                        Text(entry.perceptions.toMXN(), style = MaterialTheme.typography.bodySmall, color = SuccessColor)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Deducciones", style = MaterialTheme.typography.bodySmall, color = ErrorColor)
                        Text("-${entry.deductions.toMXN()}", style = MaterialTheme.typography.bodySmall, color = ErrorColor)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Neto", fontWeight = FontWeight.Bold)
                        Text(entry.netPay.toMXN(), fontWeight = FontWeight.Bold, color = PrimaryDark)
                    }
                }
            }
        }
        if (entries.isEmpty()) { item { EmptyHr("No hay nóminas registradas") } }
    }
}

@Composable
fun DepartmentList(departments: List<Department>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(departments) { dept ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Business, null, tint = PrimaryDark)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(dept.name, fontWeight = FontWeight.SemiBold)
                        Text("Encargado: ${dept.manager}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    Text("${dept.employeeCount}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PrimaryDark)
                }
            }
        }
        if (departments.isEmpty()) { item { EmptyHr("No hay departamentos registrados") } }
    }
}

@Composable
fun EmptyHr(message: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Groups, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))
            Text(message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
    }
}
