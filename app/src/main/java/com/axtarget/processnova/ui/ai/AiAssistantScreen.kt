package com.axtarget.processnova.ui.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.axtarget.processnova.core.Result
import com.axtarget.processnova.data.models.AiChatResponse
import com.axtarget.processnova.data.repository.AiRepository
import com.axtarget.processnova.ui.theme.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(navController: NavController, showTopBar: Boolean = false) {
    val scope = rememberCoroutineScope()
    val repository = remember { AiRepository() }
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Mensaje de bienvenida
    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages = listOf(
                ChatMessage("¡Hola! Soy tu asistente IA de ProcessNova. Puedo ayudarte a analizar inventario, finanzas, clientes y más. ¿En qué puedo ayudarte?", false)
            )
        }
    }

    if (showTopBar) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Asistente IA") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryDark, titleContentColor = MaterialTheme.colorScheme.onPrimary, navigationIconContentColor = MaterialTheme.colorScheme.onPrimary)
                )
            }
        ) { padding ->
            AiAssistantContent(
                modifier = Modifier.padding(padding),
                messages = messages,
                inputText = inputText,
                isLoading = isLoading,
                listState = listState,
                onInputTextChange = { inputText = it },
                onSendClick = { msg ->
                    val userMsg = ChatMessage(msg, true)
                    messages = messages + userMsg
                    isLoading = true
                    scope.launch {
                        when (val result = repository.chat(msg)) {
                            is Result.Success<*> -> {
                                val data = result.data as? AiChatResponse
                                if (data != null) {
                                    messages = messages + ChatMessage(data.response, false)
                                }
                            }
                            is Result.Error -> {
                                messages = messages + ChatMessage("Lo siento, hubo un problema: ${result.message}", false)
                            }
                            else -> {}
                        }
                        isLoading = false
                        listState.animateScrollToItem(messages.size - 1)
                    }
                },
                onQuickAnalysis = { type ->
                    isLoading = true
                    scope.launch {
                        sendAnalysis(repository, type) { 
                            messages = messages + it 
                            isLoading = false
                            scope.launch { listState.animateScrollToItem(messages.size - 1) }
                        }
                    }
                }
            )
        }
    } else {
        AiAssistantContent(
            modifier = Modifier.fillMaxSize(),
            messages = messages,
            inputText = inputText,
            isLoading = isLoading,
            listState = listState,
            onInputTextChange = { inputText = it },
            onSendClick = { msg ->
                val userMsg = ChatMessage(msg, true)
                messages = messages + userMsg
                isLoading = true
                scope.launch {
                    when (val result = repository.chat(msg)) {
                        is Result.Success<*> -> {
                            val data = result.data as? AiChatResponse
                            if (data != null) {
                                messages = messages + ChatMessage(data.response, false)
                            }
                        }
                        is Result.Error -> {
                            messages = messages + ChatMessage("Lo siento, hubo un problema: ${result.message}", false)
                        }
                        else -> {}
                    }
                    isLoading = false
                    listState.animateScrollToItem(messages.size - 1)
                }
            },
            onQuickAnalysis = { type ->
                isLoading = true
                scope.launch {
                    sendAnalysis(repository, type) { 
                        messages = messages + it 
                        isLoading = false
                        scope.launch { listState.animateScrollToItem(messages.size - 1) }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AiAssistantContent(
    modifier: Modifier = Modifier,
    messages: List<ChatMessage>,
    inputText: String,
    isLoading: Boolean,
    listState: LazyListState,
    onInputTextChange: (String) -> Unit,
    onSendClick: (String) -> Unit,
    onQuickAnalysis: (String) -> Unit
) {
    Column(modifier = modifier) {
        // Botones de análisis rápido
        if (messages.size <= 1 && !isLoading) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text("Análisis rápido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                }
                items(
                    listOf(
                        "Analizar inventario" to "inventory",
                        "Revisar finanzas" to "finance",
                        "Sugerir precios" to "prices",
                        "Analizar clientes" to "crm"
                    )
                ) { (label, type) ->
                    AssistChip(
                        onClick = { onQuickAnalysis(type) },
                        label = { Text(label) },
                        leadingIcon = { Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Chat
        LazyColumn(
            modifier = Modifier.weight(1f),
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(message = msg)
            }
            if (isLoading) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(PrimaryDark.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.AutoAwesome, null, tint = PrimaryDark, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Escribiendo...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Input
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputTextChange,
                    placeholder = { Text("Escribe un mensaje...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank() && !isLoading) {
                            val msg = inputText
                            onInputTextChange("")
                            onSendClick(msg)
                        }
                    },
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(PrimaryDark),
                    enabled = inputText.isNotBlank() && !isLoading
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, "Enviar", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(PrimaryDark.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.AutoAwesome, null, tint = PrimaryDark, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser) PrimaryDark else MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                color = if (message.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (message.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Accent.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, null, tint = Accent, modifier = Modifier.size(18.dp))
            }
        }
    }
}

private suspend fun sendAnalysis(repository: AiRepository, type: String, onResult: (ChatMessage) -> Unit) {
    val result = when (type) {
        "inventory" -> repository.analyzeInventory()
        "finance" -> repository.analyzeFinance()
        "prices" -> repository.suggestPrices()
        "crm" -> repository.analyzeCrm()
        else -> repository.chat("Analiza $type")
    }
    when (result) {
        is Result.Success<*> -> {
            val data = result.data as? AiChatResponse
            if (data != null) {
                onResult(ChatMessage(data.response, false))
            }
        }
        is Result.Error -> onResult(ChatMessage("Lo siento, no pude completar el análisis: ${result.message}", false))
        else -> {}
    }
}


