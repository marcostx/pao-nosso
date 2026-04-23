package com.paonosso.app.ui.screens.agenda

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paonosso.app.data.model.Appointment
import com.paonosso.app.ui.components.EmptyState
import com.paonosso.app.ui.components.StatusPill
import com.paonosso.app.ui.theme.Emerald600
import com.paonosso.app.ui.theme.Emerald700
import com.paonosso.app.ui.theme.Gray500
import com.paonosso.app.ui.theme.Gray800
import com.paonosso.app.ui.theme.Red500
import com.paonosso.app.viewmodel.AgendaViewModel

@Composable
fun AgendaScreen(contentPadding: PaddingValues) {
    val vm: AgendaViewModel = viewModel(factory = AgendaViewModel.Factory)
    val state by vm.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(contentPadding)
            .padding(horizontal = 24.dp, vertical = 24.dp),
    ) {
        Text(
            "Meus Agendamentos",
            fontWeight = FontWeight.Black,
            color = Gray800,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.height(16.dp))

        if (!state.loading && state.items.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.CalendarMonth,
                title = "Nenhum agendamento",
                message = "Suas doacoes aparecerao aqui",
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.items) { ap ->
                    AppointmentItem(
                        ap = ap,
                        onCancel = { vm.cancel(ap.id) },
                        onConclude = { vm.conclude(ap.id) },
                        onAccept = { vm.accept(ap.id) },
                        onRefuse = { vm.refuse(ap.id) },
                        onCancelDoacao = { ap.doacaoId?.let(vm::cancelDoacao) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AppointmentItem(
    ap: Appointment,
    onCancel: () -> Unit,
    onConclude: () -> Unit,
    onAccept: () -> Unit,
    onRefuse: () -> Unit,
    onCancelDoacao: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF9FAFB),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        ap.item ?: "Doacao",
                        fontWeight = FontWeight.Bold,
                        color = Gray800,
                    )
                    val subtitle = ap.instituicaoNome
                        ?: if (ap.status.uppercase() == "AGUARDANDO") {
                            "Aguardando interesse de uma instituicao"
                        } else null
                    subtitle?.let {
                        Text(it, color = Gray500, style = MaterialTheme.typography.bodySmall)
                    }
                    Text(
                        listOfNotNull(
                            ap.janela?.let { if (it == "HOJE") "Hoje" else "Amanha" },
                            ap.horario?.take(5),
                        ).joinToString(" - "),
                        color = Emerald700,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                StatusPill(ap.status)
            }

            Spacer(Modifier.height(12.dp))
            when (ap.status.uppercase()) {
                "PENDENTE" -> Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                        modifier = Modifier.weight(1f),
                    ) { Text("Aceitar") }
                    OutlinedButton(
                        onClick = onRefuse,
                        modifier = Modifier.weight(1f),
                    ) { Text("Recusar", color = Red500) }
                }
                "ACEITA" -> Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        onClick = onConclude,
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                        modifier = Modifier.weight(1f),
                    ) { Text("Concluir") }
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                    ) { Text("Cancelar", color = Red500) }
                }
                "AGUARDANDO" -> OutlinedButton(
                    onClick = onCancelDoacao,
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Cancelar doacao", color = Red500) }
                else -> Spacer(Modifier.size(0.dp))
            }
        }
    }
}
