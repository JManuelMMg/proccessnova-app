package com.axtarget.processnova.ui.notifications

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
import com.axtarget.processnova.core.toRelativeTime
import com.axtarget.processnova.core.Result
import com.axtarget.processnova.data.models.AppNotification
import com.axtarget.processnova.data.repository.NotificationRepository
import com.axtarget.processnova.ui.navigation.Routes
import com.axtarget.processnova.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val repository = remember { NotificationRepository() }
    var notifications by remember { mutableStateOf<List<AppNotification>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = true
        when (val r = repository.getNotifications()) {
            is Result.Success -> notifications = r.data
            else -> {}
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.COMPOSE_NOTIFICATION) }) {
                        Icon(Icons.Default.Edit, "Nueva")
                    }
                    TextButton(onClick = {
                        scope.launch {
                            repository.markAllAsRead()
                            notifications = notifications.map { it.copy(isRead = true) }
                        }
                    }) {
                        Text("Marcar todas", color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryDark, titleContentColor = MaterialTheme.colorScheme.onPrimary, navigationIconContentColor = MaterialTheme.colorScheme.onPrimary)
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notifications) { notification ->
                    NotificationCard(
                        notification = notification,
                        onClick = {
                            scope.launch {
                                repository.markAsRead(notification.id)
                                notifications = notifications.map {
                                    if (it.id == notification.id) it.copy(isRead = true) else it
                                }
                            }
                        }
                    )
                }
                if (notifications.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.NotificationsOff, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("No hay notificaciones", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: AppNotification, onClick: () -> Unit) {
    val icon = when (notification.type) {
        "stock_critical" -> Icons.Default.Warning
        "sale" -> Icons.Default.CheckCircle
        "ai_alert" -> Icons.Default.AutoAwesome
        "payment" -> Icons.Default.Payment
        "logistics" -> Icons.Default.LocalShipping
        else -> Icons.Default.Notifications
    }
    val iconColor = when (notification.type) {
        "stock_critical" -> StockCritical
        "sale" -> SuccessColor
        "ai_alert" -> InfoColor
        "payment" -> Accent
        "logistics" -> WarningColor
        else -> PrimaryDark
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) MaterialTheme.colorScheme.surface else PrimaryDark.copy(alpha = 0.05f)
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(notification.message, style = MaterialTheme.typography.bodyMedium, fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.SemiBold)
                Text(notification.timestamp.toRelativeTime(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
            if (!notification.isRead) {
                Badge(containerColor = Accent) { Text(" ", modifier = Modifier.size(8.dp)) }
            }
        }
    }
}


