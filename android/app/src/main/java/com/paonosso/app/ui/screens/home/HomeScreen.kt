package com.paonosso.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paonosso.app.data.model.Appointment
import com.paonosso.app.ui.components.StatusPill
import com.paonosso.app.ui.theme.Emerald100
import com.paonosso.app.ui.theme.Emerald600
import com.paonosso.app.ui.theme.Emerald700
import com.paonosso.app.ui.theme.Gray500
import com.paonosso.app.ui.theme.Gray800
import com.paonosso.app.ui.theme.Orange100
import com.paonosso.app.ui.theme.Orange700
import com.paonosso.app.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onNewDonation: () -> Unit,
    onOpenAgenda: () -> Unit,
    contentPadding: androidx.compose.foundation.layout.PaddingValues,
) {
    val vm: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    val state by vm.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(contentPadding),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 24.dp, end = 24.dp, top = 24.dp, bottom = 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { Header(nome = state.nome ?: "Doador", refeicoes = state.stats?.refeicoesSalvas ?: 0) }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                QuickAction(
                    title = "Nova\nDoacao",
                    bg = Emerald100,
                    fg = Emerald700,
                    modifier = Modifier.weight(1f),
                    onClick = onNewDonation,
                )
                QuickAction(
                    title = "Ver\nColetas",
                    bg = Orange100,
                    fg = Orange700,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenAgenda,
                )
            }
        }
        item {
            Text(
                "Proximas Coletas",
                fontWeight = FontWeight.Bold,
                color = Gray800,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        if (state.proximas.isEmpty()) {
            item {
                Surface(
                    color = Color(0xFFF9FAFB),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth().height(96.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("Sem coletas agendadas", color = Gray500)
                    }
                }
            }
        } else {
            items(state.proximas) { ap -> AppointmentCard(ap) }
        }
    }
}

@Composable
private fun Header(nome: String, refeicoes: Int) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.linearGradient(listOf(Emerald600, Color(0xFF14B8A6))),
                )
                .padding(24.dp),
        ) {
            Column {
                Text("Ola, $nome!", color = Color.White.copy(alpha = 0.9f))
                Spacer(Modifier.height(8.dp))
                Text(
                    "$refeicoes refeicoes salvas",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Continue fazendo a diferenca!",
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun QuickAction(
    title: String,
    bg: Color,
    fg: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .height(112.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = bg,
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = fg,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(title, color = fg, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AppointmentCard(ap: Appointment) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF9FAFB),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Emerald100),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = Emerald600)
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(ap.item ?: "Doacao", fontWeight = FontWeight.Bold, color = Gray800)
                Text(
                    ap.instituicaoNome ?: ap.endereco ?: "",
                    color = Gray500,
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    listOfNotNull(
                        ap.janela?.let { if (it == "HOJE") "Hoje" else "Amanha" },
                        ap.horario?.take(5),
                    ).joinToString(" - "),
                    color = Emerald700,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            StatusPill(ap.status)
        }
    }
}
