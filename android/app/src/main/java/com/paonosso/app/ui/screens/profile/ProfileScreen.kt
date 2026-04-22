package com.paonosso.app.ui.screens.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paonosso.app.ui.theme.Emerald100
import com.paonosso.app.ui.theme.Emerald600
import com.paonosso.app.ui.theme.Emerald700
import com.paonosso.app.ui.theme.Gray500
import com.paonosso.app.ui.theme.Gray800
import com.paonosso.app.ui.theme.Orange100
import com.paonosso.app.ui.theme.Orange700
import com.paonosso.app.ui.theme.Red500
import com.paonosso.app.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(contentPadding: PaddingValues, onLoggedOut: () -> Unit) {
    val vm: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
    val state by vm.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Emerald100),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                state.nome?.firstOrNull()?.toString()?.uppercase() ?: "?",
                color = Emerald700,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            state.nome ?: "—",
            style = MaterialTheme.typography.titleLarge,
            color = Gray800,
        )
        Text(
            state.email ?: "",
            color = Gray500,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(
                label = "Doacoes",
                value = state.stats?.doacoesTotal?.toString() ?: "0",
                bg = Emerald100,
                fg = Emerald700,
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = "Peso Total",
                value = "${state.stats?.pesoTotalKg ?: 0.0} kg",
                bg = Orange100,
                fg = Orange700,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = { /* edit em fase 2 */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        ) { Text("Editar perfil") }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { vm.logout(onLoggedOut) },
            colors = ButtonDefaults.buttonColors(containerColor = Red500),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        ) { Text("Sair", color = Color.White) }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    bg: Color,
    fg: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = bg,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.height(96.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label, color = fg, fontWeight = FontWeight.SemiBold)
            Text(value, color = fg, fontSize = 22.sp, fontWeight = FontWeight.Black)
        }
    }
}
