package com.axtarget.processnova.ui.crm

import androidx.compose.foundation.background
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
import com.axtarget.processnova.ProcessNovaApp
import com.axtarget.processnova.core.toMXN
import com.axtarget.processnova.core.Result
import com.axtarget.processnova.data.models.*
import com.axtarget.processnova.data.repository.CrmRepository
import com.axtarget.processnova.ui.navigation.Routes
import com.axtarget.processnova.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmScreen(navController: NavController, showTopBar: Boolean = false) {
    val scope = rememberCoroutineScope()
    val repository = remember { CrmRepository() }

    var selectedTab by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var customers by remember { mutableStateOf<List<Customer>>(emptyList()) }
    var leads by remember { mutableStateOf<List<Lead>>(emptyList()) }
    var opportunities by remember { mutableStateOf<List<Opportunity>>(emptyList()) }
    var campaigns by remember { mutableStateOf<List<Campaign>>(emptyList()) }

    val tabs = listOf("Clientes", "Leads", "Oportunidades", "Campañas")

    LaunchedEffect(selectedTab) {
        isLoading = true
        when (selectedTab) {
            0 -> when (val r = repository.getCustomers()) { is Result.Success<*> -> customers = r.data as List<Customer>; else -> {} }
            1 -> when (val r = repository.getLeads()) { is Result.Success<*> -> leads = r.data as List<Lead>; else -> {} }
            2 -> when (val r = repository.getOpportunities()) { is Result.Success<*> -> opportunities = r.data as List<Opportunity>; else -> {} }
            3 -> when (val r = repository.getCampaigns()) { is Result.Success<*> -> campaigns = r.data as List<Campaign>; else -> {} }
        }
        isLoading = false
    }

    if (showTopBar) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("CRM") },
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
                CrmFab(selectedTab) {
                    when (selectedTab) {
                        0 -> navController.navigate(Routes.customerForm())
                    }
                }
            }
        ) { padding ->
            CrmContent(
                modifier = Modifier.padding(padding).fillMaxSize(),
                selectedTab = selectedTab,
                onTabSelect = { selectedTab = it },
                tabs = tabs,
                isLoading = isLoading,
                customers = customers,
                leads = leads,
                opportunities = opportunities,
                campaigns = campaigns,
                onCustomerClick = { navController.navigate(Routes.customerDetail(it)) }
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            CrmContent(
                modifier = Modifier.fillMaxSize(),
                selectedTab = selectedTab,
                onTabSelect = { selectedTab = it },
                tabs = tabs,
                isLoading = isLoading,
                customers = customers,
                leads = leads,
                opportunities = opportunities,
                campaigns = campaigns,
                onCustomerClick = { navController.navigate(Routes.customerDetail(it)) }
            )
            
            CrmFab(
                selectedTab = selectedTab,
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                onClick = {
                    when (selectedTab) {
                        0 -> navController.navigate(Routes.customerForm())
                    }
                }
            )
        }
    }
}

@Composable
private fun CrmFab(selectedTab: Int, modifier: Modifier = Modifier, onClick: () -> Unit) {
    if (selectedTab == 0) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = Accent,
            modifier = modifier
        ) {
            Icon(Icons.Default.Add, "Nuevo", tint = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
private fun CrmContent(
    modifier: Modifier,
    selectedTab: Int,
    onTabSelect: (Int) -> Unit,
    tabs: List<String>,
    isLoading: Boolean,
    customers: List<Customer>,
    leads: List<Lead>,
    opportunities: List<Opportunity>,
    campaigns: List<Campaign>,
    onCustomerClick: (Int) -> Unit
) {
    Column(modifier = modifier) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = selectedTab == index, onClick = { onTabSelect(index) }, text = { Text(title) })
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            when (selectedTab) {
                0 -> CustomerList(customers, onCustomerClick)
                1 -> LeadList(leads)
                2 -> OpportunityPipeline(opportunities)
                3 -> CampaignList(campaigns)
            }
        }
    }
}

@Composable
fun CustomerList(customers: List<Customer>, onCustomerClick: (Int) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(customers) { customer ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onCustomerClick(customer.id) },
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(44.dp).clip(CircleShape).background(PrimaryDark.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(customer.name.take(2).uppercase(), fontWeight = FontWeight.Bold, color = PrimaryDark)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(customer.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(customer.phone.ifBlank { customer.email }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(customer.lifetimeValue.toMXN(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryDark)
                        Text("LTV", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            }
        }
        if (customers.isEmpty()) { item { EmptyState("No hay clientes registrados", Icons.Default.People) } }
    }
}

@Composable
fun LeadList(leads: List<Lead>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(leads) { lead ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(lead.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(lead.company, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    AssistChip(onClick = { }, label = { Text(lead.status) })
                }
            }
        }
        if (leads.isEmpty()) { item { EmptyState("No hay leads registrados", Icons.Default.PersonSearch) } }
    }
}

@Composable
fun OpportunityPipeline(opportunities: List<Opportunity>) {
    val stages = listOf("prospectacion", "calificacion", "propuesta", "negociacion", "cerrado")
    val stageNames = listOf("Prospectación", "Calificación", "Propuesta", "Negociación", "Cerrado")
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(stages.zip(stageNames)) { (stage, name) ->
            val stageOpps = opportunities.filter { it.stage == stage }
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = PrimaryDark.copy(alpha = 0.05f))) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("$name (${stageOpps.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryDark)
                    stageOpps.forEach { opp ->
                        Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), shape = RoundedCornerShape(8.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(opp.name, fontWeight = FontWeight.SemiBold)
                                Text("${opp.customer} • ${opp.amount.toMXN()} • ${opp.probability}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }
        }
        if (opportunities.isEmpty()) { item { EmptyState("No hay oportunidades", Icons.Default.TrendingUp) } }
    }
}

@Composable
fun CampaignList(campaigns: List<Campaign>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(campaigns) { campaign ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(campaign.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        AssistChip(onClick = { }, label = { Text(campaign.status) })
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tipo: ${campaign.type} • Presupuesto: ${campaign.budget.toMXN()}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text("ROI: ${"%.1f".format(campaign.roi)}%", style = MaterialTheme.typography.bodySmall, color = if (campaign.roi > 0) SuccessColor else ErrorColor)
                }
            }
        }
        if (campaigns.isEmpty()) { item { EmptyState("No hay campañas", Icons.Default.Campaign) } }
    }
}

@Composable
fun EmptyState(message: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))
            Text(message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
    }
}
