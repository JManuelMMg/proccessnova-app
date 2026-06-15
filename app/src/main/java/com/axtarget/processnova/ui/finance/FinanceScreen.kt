package com.axtarget.processnova.ui.finance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.axtarget.processnova.core.toMXN
import com.axtarget.processnova.core.toShortDate
import com.axtarget.processnova.core.Result
import com.axtarget.processnova.data.models.*
import com.axtarget.processnova.data.repository.FinanceRepository
import com.axtarget.processnova.ui.navigation.Routes
import com.axtarget.processnova.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(navController: NavController, showTopBar: Boolean = false) {
    val scope = rememberCoroutineScope()
    val repository = remember { FinanceRepository() }
    var selectedTab by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var summary by remember { mutableStateOf<FinanceSummary?>(null) }
    var accounts by remember { mutableStateOf<List<Account>>(emptyList()) }
    var income by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var expenses by remember { mutableStateOf<List<Transaction>>(emptyList()) }
    var invoices by remember { mutableStateOf<List<Invoice>>(emptyList()) }

    val tabs = listOf("Resumen", "Cuentas", "Ingresos", "Egresos", "Facturas")

    LaunchedEffect(selectedTab) {
        isLoading = true
        when (selectedTab) {
            0 -> when (val r = repository.getSummary()) { is Result.Success<*> -> { val data = r.data as FinanceSummary; summary = data; accounts = data.accounts }; else -> {} }
            1 -> when (val r = repository.getAccounts()) { is Result.Success<*> -> accounts = r.data as List<Account>; else -> {} }
            2 -> when (val r = repository.getIncome(null, null)) { is Result.Success<*> -> income = r.data as List<Transaction>; else -> {} }
            3 -> when (val r = repository.getExpenses(null, null)) { is Result.Success<*> -> expenses = r.data as List<Transaction>; else -> {} }
            4 -> when (val r = repository.getInvoices()) { is Result.Success<*> -> invoices = r.data as List<Invoice>; else -> {} }
        }
        isLoading = false
    }

    if (showTopBar) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Finanzas") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryDark, titleContentColor = MaterialTheme.colorScheme.onPrimary, navigationIconContentColor = MaterialTheme.colorScheme.onPrimary)
                )
            },
            floatingActionButton = {
                FinanceFab(selectedTab) { /* TODO */ }
            }
        ) { padding ->
            FinanceContent(
                modifier = Modifier.padding(padding).fillMaxSize(),
                selectedTab = selectedTab,
                onTabSelect = { selectedTab = it },
                tabs = tabs,
                isLoading = isLoading,
                summary = summary,
                accounts = accounts,
                income = income,
                expenses = expenses,
                invoices = invoices,
                onInvoiceClick = { navController.navigate(Routes.invoiceDetail(it)) }
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            FinanceContent(
                modifier = Modifier.fillMaxSize(),
                selectedTab = selectedTab,
                onTabSelect = { selectedTab = it },
                tabs = tabs,
                isLoading = isLoading,
                summary = summary,
                accounts = accounts,
                income = income,
                expenses = expenses,
                invoices = invoices,
                onInvoiceClick = { navController.navigate(Routes.invoiceDetail(it)) }
            )
            FinanceFab(
                selectedTab = selectedTab,
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                onClick = { /* TODO */ }
            )
        }
    }
}

@Composable
private fun FinanceFab(selectedTab: Int, modifier: Modifier = Modifier, onClick: () -> Unit) {
    if (selectedTab in listOf(2, 3)) {
        FloatingActionButton(onClick = onClick, containerColor = Accent, modifier = modifier) {
            Icon(Icons.Default.Add, "Nuevo", tint = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
private fun FinanceContent(
    modifier: Modifier,
    selectedTab: Int,
    onTabSelect: (Int) -> Unit,
    tabs: List<String>,
    isLoading: Boolean,
    summary: FinanceSummary?,
    accounts: List<Account>,
    income: List<Transaction>,
    expenses: List<Transaction>,
    invoices: List<Invoice>,
    onInvoiceClick: (Int) -> Unit
) {
    Column(modifier = modifier) {
        ScrollableTabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title -> Tab(selected = selectedTab == index, onClick = { onTabSelect(index) }, text = { Text(title) }) }
        }
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            when (selectedTab) {
                0 -> FinanceSummaryContent(summary ?: FinanceSummary())
                1 -> AccountList(accounts)
                2 -> TransactionList(income, "ingreso")
                3 -> TransactionList(expenses, "egreso")
                4 -> InvoiceList(invoices, onInvoiceClick)
            }
        }
    }
}

@Composable
fun FinanceSummaryContent(summary: FinanceSummary) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard(modifier = Modifier.weight(1f), title = "Ingresos", value = summary.totalIncome.toMXN(), color = SuccessColor)
                SummaryCard(modifier = Modifier.weight(1f), title = "Egresos", value = summary.totalExpense.toMXN(), color = ErrorColor)
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard(modifier = Modifier.weight(1f), title = "Margen", value = "${"%.1f".format(summary.profitMargin)}%", color = InfoColor)
                SummaryCard(modifier = Modifier.weight(1f), title = "Impuestos", value = summary.taxes.toMXN(), color = WarningColor)
            }
        }
        item { Text("Cuentas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        items(summary.accounts) { account ->
            Card(modifier = Modifier.fillMaxWidth().clickable { }, shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBalance, null, tint = PrimaryDark)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(account.name, fontWeight = FontWeight.SemiBold)
                        Text("${account.type} • ${account.bank}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    Text(account.balance.toMXN(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryDark)
                }
            }
        }
    }
}

@Composable
fun SummaryCard(modifier: Modifier, title: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun AccountList(accounts: List<Account>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(accounts) { account ->
            Card(modifier = Modifier.fillMaxWidth().clickable { }, shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBalance, null, tint = PrimaryDark)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(account.name, fontWeight = FontWeight.SemiBold)
                        Text("${account.type} • ${account.bank}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    Text(account.balance.toMXN(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryDark)
                }
            }
        }
        if (accounts.isEmpty()) { item { EmptyFinance("No hay cuentas registradas") } }
    }
}

@Composable
fun TransactionList(transactions: List<Transaction>, type: String) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(transactions) { tx ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (type == "ingreso") Icons.Default.ArrowDownward else Icons.Default.ArrowUpward, null, tint = if (type == "ingreso") SuccessColor else ErrorColor)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(tx.description, fontWeight = FontWeight.SemiBold)
                        Text(tx.date.toShortDate(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    Text(tx.amount.toMXN(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = if (type == "ingreso") SuccessColor else ErrorColor)
                }
            }
        }
        if (transactions.isEmpty()) { item { EmptyFinance("No hay ${if (type == "ingreso") "ingresos" else "egresos"} registrados") } }
    }
}

@Composable
fun InvoiceList(invoices: List<Invoice>, onInvoiceClick: (Int) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(invoices) { invoice ->
            Card(modifier = Modifier.fillMaxWidth().clickable { onInvoiceClick(invoice.id) }, shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(invoice.number, fontWeight = FontWeight.SemiBold)
                        AssistChip(onClick = { }, label = { Text(invoice.status) })
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(invoice.customer, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(invoice.date.toShortDate(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        Text(invoice.amount.toMXN(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryDark)
                    }
                }
            }
        }
        if (invoices.isEmpty()) { item { EmptyFinance("No hay facturas registradas") } }
    }
}

@Composable
fun EmptyFinance(message: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.AccountBalanceWallet, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))
            Text(message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
    }
}
