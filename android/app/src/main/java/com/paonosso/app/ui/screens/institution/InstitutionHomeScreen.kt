package com.paonosso.app.ui.screens.institution

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paonosso.app.data.model.Donation
import com.paonosso.app.ui.components.EmptyState
import com.paonosso.app.ui.theme.Emerald100
import com.paonosso.app.ui.theme.Emerald600
import com.paonosso.app.ui.theme.Emerald700
import com.paonosso.app.ui.theme.Gray300
import com.paonosso.app.ui.theme.Gray500
import com.paonosso.app.ui.theme.Gray800
import com.paonosso.app.viewmodel.InstitutionHomeViewModel

@Composable
fun InstitutionHomeScreen(contentPadding: PaddingValues) {
    val vm: InstitutionHomeViewModel = viewModel(factory = InstitutionHomeViewModel.Factory)
    val state by vm.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(contentPadding)
            .padding(horizontal = 24.dp, vertical = 24.dp),
    ) {
        Text(
            "Doacoes Disponiveis",
            color = Gray800,
            fontWeight = FontWeight.Black,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.height(16.dp))

        if (!state.loading && state.available.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.Inventory2,
                title = "Nada por aqui",
                message = "Doacoes disponiveis aparecerao nesta lista",
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.available) { d ->
                    DonationCard(
                        donation = d,
                        alreadyRequested = state.sentIds.contains(d.id),
                    ) { vm.request(d.id) }
                }
            }
        }
    }
}

@Composable
private fun DonationCard(
    donation: Donation,
    alreadyRequested: Boolean,
    onRequest: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF9FAFB),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Emerald100),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Outlined.Inventory2,
                        contentDescription = null,
                        tint = Emerald600,
                    )
                }
                Spacer(Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(donation.titulo, fontWeight = FontWeight.Bold, color = Gray800)
                    Text(
                        donation.doadorNome ?: "—",
                        color = Gray500,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        listOfNotNull(donation.bairro, donation.quantidade).joinToString(" - "),
                        color = Emerald700,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onRequest,
                enabled = !alreadyRequested,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (alreadyRequested) Gray300 else Emerald600,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
            ) {
                Text(if (alreadyRequested) "Pedido enviado" else "Solicitar coleta")
            }
        }
    }
}
