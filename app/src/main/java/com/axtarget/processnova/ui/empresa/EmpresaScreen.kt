package com.axtarget.processnova.ui.empresa

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.axtarget.processnova.ui.navigation.Routes
import com.axtarget.processnova.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpresaScreen(navController: NavController, showTopBar: Boolean = true) {
    if (showTopBar) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("AxtarGet") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                        }
                    },
                    actions = {
                        TextButton(onClick = { navController.navigate(Routes.LOGIN) }) {
                            Text("Iniciar sesión", color = Accent)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Button(
                            onClick = { navController.navigate(Routes.REGISTER) },
                            colors = ButtonDefaults.buttonColors(containerColor = Accent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Crear cuenta", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0B1628),
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            },
            containerColor = Color(0xFF0B1628)
        ) { padding ->
            EmpresaContent(
                modifier = Modifier.fillMaxSize().padding(padding),
                navController = navController
            )
        }
    } else {
        EmpresaContent(
            modifier = Modifier.fillMaxSize(),
            navController = navController
        )
    }
}

@Composable
private fun EmpresaContent(
    modifier: Modifier,
    navController: NavController
) {
    LazyColumn(
        modifier = modifier.background(Color(0xFF0B1628)),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Accent.copy(alpha = 0.12f), Color.Transparent),
                            radius = 500f
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Accent.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Accent.copy(alpha = 0.3f)),
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        Text("🏢 Sobre la empresa", color = Accent, fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    }
                    Text("AxtarGet", fontSize = 40.sp, fontWeight = FontWeight.Bold,
                        color = Color.White, textAlign = TextAlign.Center)
                    Text("Inteligencia en Soluciones Digitales",
                        fontSize = 16.sp, color = Color(0xFF7A9BB5), textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))
                    Text(
                        "Desarrollamos tecnología empresarial de clase mundial desde México, "
                                + "democratizando el acceso a herramientas de gestión inteligente para PYMES.",
                        fontSize = 15.sp, color = Color(0xFF7A9BB5), textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        item {
            Text("Misión y Visión", fontSize = 24.sp, fontWeight = FontWeight.Bold,
                color = Color.White, modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
        }
        item {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MvCard(
                    modifier = Modifier.weight(1f),
                    title = "Misión",
                    description = "Empoderar a las PYMES mexicanas con tecnología de gestión empresarial inteligente, accesible y fácil de usar.",
                    isMision = true
                )
                MvCard(
                    modifier = Modifier.weight(1f),
                    title = "Visión",
                    description = "Ser el ERP líder para PYMES en Latinoamérica, impulsando la transformación digital con inteligencia artificial.",
                    isMision = false
                )
            }
        }

        item {
            Text("Nuestros Valores", fontSize = 24.sp, fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp))
        }
        item {
            val valores = listOf(
                ValorData("💡", "Innovación", "Tecnología de vanguardia al alcance de todos"),
                ValorData("🤝", "Cercanía", "Atención personalizada y soporte humano"),
                ValorData("🔒", "Confianza", "Datos seguros y transparencia total"),
                ValorData("⚡", "Agilidad", "Implementación rápida sin complicaciones"),
                ValorData("🌎", "Impacto Social", "Impulsando el crecimiento de PYMES mexicanas"),
                ValorData("📈", "Mejora Continua", "Escuchamos a nuestros usuarios para evolucionar")
            )
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                valores.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        row.forEach { valor ->
                            ValorCard(
                                modifier = Modifier.weight(1f),
                                icon = valor.icon,
                                name = valor.name,
                                description = valor.description
                            )
                        }
                        if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            Text("Roadmap", fontSize = 24.sp, fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp))
        }
        item {
            val phases = listOf(
                PhaseData("Fase 1", "Fundamentos", "ERP core con inventario, POS, CRM y facturación", true),
                PhaseData("Fase 2", "Inteligencia", "Asistente IA, análisis predictivo y recomendaciones", true),
                PhaseData("Fase 3", "Expansión", "App móvil nativa, API pública y marketplace de integraciones", false),
                PhaseData("Fase 4", "Ecosistema", "Banca integrada, logística avanzada y expansión LATAM", false)
            )
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                phases.forEach { phase ->
                    TimelineItem(
                        phase = phase.phase,
                        title = phase.title,
                        description = phase.description,
                        isDone = phase.isDone
                    )
                }
            }
        }

        item {
            Text("Nuestro Equipo", fontSize = 24.sp, fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp))
        }
        item {
            val team = listOf(
                TeamData("JM", "J. Manuel M.", "CEO & Fundador", "Visionario tecnológico con pasión por transformar PYMES", listOf("Estrategia", "Producto", "IA")),
                TeamData("AT", "A. Torres", "CTO", "Arquitecto de software especializado en sistemas distribuidos", listOf("Backend", "Cloud", "DevOps")),
                TeamData("LC", "L. Castillo", "Head of Design", "Diseñadora UX/UI enfocada en experiencias intuitivas", listOf("UX", "UI", "Research")),
                TeamData("MR", "M. Rodríguez", "Lead Developer", "Desarrollador full-stack apasionado por el código limpio", listOf("Mobile", "Frontend", "QA"))
            )
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                team.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { member ->
                            TeamCard(
                                modifier = Modifier.weight(1f),
                                initials = member.initials,
                                name = member.name,
                                role = member.role,
                                description = member.description,
                                skills = member.skills
                            )
                        }
                        if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            Text("Normatividad y Cumplimiento", fontSize = 24.sp, fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp))
        }
        item {
            val legals = listOf(
                LegalData("📜", "CFDI 4.0", "Facturación electrónica conforme al SAT"),
                LegalData("🔐", "Protección de Datos", "Cumplimiento con LFPDPPP"),
                LegalData("☁️", "Alta Disponibilidad", "Infraestructura cloud con 99.9% uptime"),
                LegalData("📊", "NOM-035", "Herramientas para cumplimiento de NOM-035")
            )
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                legals.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        row.forEach { legal ->
                            LegalCard(
                                modifier = Modifier.weight(1f),
                                icon = legal.icon,
                                title = legal.title,
                                description = legal.description
                            )
                        }
                        if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1E45))
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("¿Quieres ser parte de la revolución?",
                        fontSize = 24.sp, fontWeight = FontWeight.Bold,
                        color = Color.White, textAlign = TextAlign.Center)
                    Text("Únete a las PYMES que ya transforman su negocio con ProcessNova",
                        color = Color(0xFF7A9BB5), textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, bottom = 24.dp))
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Accent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text("Volver al inicio", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("AxtarGet © 2025", color = Color(0xFF4A6580), fontSize = 13.sp)
                Text("Todos los derechos reservados", color = Color(0xFF4A6580), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun MvCard(modifier: Modifier, title: String, description: String, isMision: Boolean) {
    val gradientColors = if (isMision)
        listOf(Accent, Color(0xFF00D4FF))
    else
        listOf(Color(0xFF9B6CF6), Accent)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E35)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(Brush.horizontalGradient(gradientColors))
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(description, fontSize = 14.sp, color = Color(0xFF7A9BB5), lineHeight = 20.sp)
        }
    }
}

@Composable
private fun ValorCard(modifier: Modifier, icon: String, name: String, description: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1628)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.White)
            Text(description, fontSize = 12.sp, color = Color(0xFF7A9BB5), lineHeight = 17.sp)
        }
    }
}

@Composable
private fun TimelineItem(phase: String, title: String, description: String, isDone: Boolean) {
    Row(modifier = Modifier.padding(bottom = 24.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDone) Color(0xFF00E5A0) else Color(0xFF162A45)
                    )
                    .border(
                        width = 3.dp,
                        color = if (isDone) Color(0xFF00E5A0) else Color.White.copy(alpha = 0.08f),
                        shape = CircleShape
                    )
            )
            if (!isDone) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(Color.White.copy(alpha = 0.08f))
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(phase, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4A6580), letterSpacing = 1.5.sp)
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.White)
            Text(description, fontSize = 14.sp, color = Color(0xFF7A9BB5), lineHeight = 20.sp)
        }
    }
}

@Composable
private fun TeamCard(modifier: Modifier, initials: String, name: String, role: String, description: String, skills: List<String>) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E35)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(listOf(Accent, Color(0xFF9B6CF6)))),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(role, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                color = Accent, letterSpacing = 1.5.sp)
            Text(name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.White)
            Text(description, fontSize = 13.sp, color = Color(0xFF7A9BB5),
                lineHeight = 18.sp, modifier = Modifier.padding(top = 4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 10.dp)
            ) {
                skills.forEach { skill ->
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Accent.copy(alpha = 0.1f)
                    ) {
                        Text(skill, fontSize = 10.sp, color = Accent, fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun LegalCard(modifier: Modifier, icon: String, title: String, description: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E35)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.White)
            Text(description, fontSize = 12.sp, color = Color(0xFF7A9BB5), lineHeight = 17.sp)
        }
    }
}

private data class ValorData(val icon: String, val name: String, val description: String)
private data class PhaseData(val phase: String, val title: String, val description: String, val isDone: Boolean)
private data class TeamData(val initials: String, val name: String, val role: String, val description: String, val skills: List<String>)
private data class LegalData(val icon: String, val title: String, val description: String)
