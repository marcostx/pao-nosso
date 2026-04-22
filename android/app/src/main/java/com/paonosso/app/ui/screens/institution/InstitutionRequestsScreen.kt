package com.paonosso.app.ui.screens.institution

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
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
import com.paonosso.app.data.model.Solicitacao
import com.paonosso.app.ui.components.EmptyState
import com.paonosso.app.ui.components.StatusPill
import com.paonosso.app.ui.theme.Emerald600
import com.paonosso.app.ui.theme.Gray500
import com.paonosso.app.ui.theme.Gray800
import com.paonosso.app.ui.theme.Red500
import com.paonosso.app.viewmodel.InstitutionRequestsViewModel

@Composable
fun InstitutionRequestsScreen(contentPadding: PaddingValues) {
    val vm: InstitutionRequestsViewModel = viewModel(factory = InstitutionRequestsViewModel.Factory)
    val state by vm.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(contentPadding)
            .padding(horizontal = 24.dp, vertical = 24.dp),
    ) {
        Text(
            "Meus Pedidos",
            color = Gray800,
            fontWeight = FontWeight.Black,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.height(16.dp))

        if (!state.loading && state.items.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.AssignmentTurnedIn,
                title = "Nenhum pedido",
                message = "Suas solicitacoes aparecerao aqui",
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.items) { item ->
                    RequestCard(
                        item = item,
                        onConclude = { vm.conclude(item.id) },
                        onCancel = { vm.cancel(item.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun RequestCard(
    item: Solicitacao,
    onConclude: () -> Unit,
    onCancel: () -> Unit,
) {
    val titulo = item.doacao?.titulo ?: "Doacao"
    val doador = item.doacao?.doadorNome ?: "—"
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF9FAFB),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(titulo, fontWeight = FontWeight.Bold, color = Gray800)
                    Text(
                        doador,
                        color = Gray500,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                StatusPill(item.status)
            }
            if (item.status.uppercase() == "ACEITA") {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onConclude,
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                        modifier = Modifier.weight(1f).height(44.dp),
                    ) { Text("Marcar como retirado") }
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f).height(44.dp),
                    ) { Text("Cancelar", color = Red500) }
                }
            }
        }
    }
}
