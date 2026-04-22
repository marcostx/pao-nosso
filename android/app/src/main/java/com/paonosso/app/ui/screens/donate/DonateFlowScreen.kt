package com.paonosso.app.ui.screens.donate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.paonosso.app.data.model.DeliveryMethod
import com.paonosso.app.data.model.DonationCategory
import com.paonosso.app.data.model.Janela
import com.paonosso.app.ui.theme.Emerald100
import com.paonosso.app.ui.theme.Emerald600
import com.paonosso.app.ui.theme.Emerald700
import com.paonosso.app.ui.theme.Gray100
import com.paonosso.app.ui.theme.Gray200
import com.paonosso.app.ui.theme.Gray500
import com.paonosso.app.ui.theme.Gray800
import com.paonosso.app.viewmodel.DonateViewModel

private val timeSlots = listOf("8:00", "10:00", "12:00", "14:00", "16:00", "18:00")

@Composable
fun DonateFlowScreen(
    contentPadding: PaddingValues,
    onClose: () -> Unit,
    onCompleted: () -> Unit,
) {
    val vm: DonateViewModel = viewModel(factory = DonateViewModel.Factory)
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.success) {
        if (state.success) onCompleted()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(contentPadding),
    ) {
        TopBar(step = state.step, onBack = { if (state.step > 1) vm.back() else onClose() })

        Box(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)) {
            AnimatedVisibility(
                visible = state.step == 1,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
            ) { Step1(vm = vm) }
            AnimatedVisibility(
                visible = state.step == 2,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
            ) { Step2(vm = vm) }
            AnimatedVisibility(
                visible = state.step == 3,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
            ) { Step3(vm = vm) }
        }
    }
}

@Composable
private fun TopBar(step: Int, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Text("<", fontSize = 24.sp, color = Gray800)
        }
        Spacer(Modifier.size(8.dp))
        Text(
            "Nova Doacao",
            color = Gray800,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f),
        )
        Text(
            "Passo $step/3",
            color = Gray500,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun Step1(vm: DonateViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("O que voce quer doar?", color = Gray800, style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = state.titulo,
            onValueChange = { v -> vm.update { it.copy(titulo = v) } },
            label = { Text("Item (ex: 5kg de arroz)") },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.quantidade,
            onValueChange = { v -> vm.update { it.copy(quantidade = v) } },
            label = { Text("Quantidade (opcional)") },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.descricao,
            onValueChange = { v -> vm.update { it.copy(descricao = v) } },
            label = { Text("Observacoes") },
            modifier = Modifier.fillMaxWidth().height(96.dp),
        )

        Text("Categoria", fontWeight = FontWeight.SemiBold, color = Gray800)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DonationCategory.ALL.forEach { (key, label) ->
                item {
                    CategoryChip(
                        label = label,
                        selected = state.categoria == key,
                    ) { vm.update { it.copy(categoria = key) } }
                }
            }
        }

        if (state.error != null) {
            Text(state.error!!, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = { if (state.titulo.isNotBlank()) vm.next() },
            colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        ) { Text("Continuar", fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun Step2(vm: DonateViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Como sera a entrega?",
            color = Gray800,
            style = MaterialTheme.typography.titleLarge,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DeliveryChip(
                "Eu Entrego",
                selected = state.metodoEntrega == DeliveryMethod.EU_ENTREGO,
                modifier = Modifier.weight(1f),
            ) { vm.update { it.copy(metodoEntrega = DeliveryMethod.EU_ENTREGO) } }
            DeliveryChip(
                "Solicitar Coleta",
                selected = state.metodoEntrega == DeliveryMethod.SOLICITAR_COLETA,
                modifier = Modifier.weight(1f),
            ) { vm.update { it.copy(metodoEntrega = DeliveryMethod.SOLICITAR_COLETA) } }
        }

        if (state.metodoEntrega == DeliveryMethod.EU_ENTREGO) {
            Text("Ponto de coleta", fontWeight = FontWeight.SemiBold)
            state.instituicoes.forEach { inst ->
                InstitutionRow(
                    nome = inst.nomeInstituicao,
                    bairro = inst.bairro ?: inst.enderecoCompleto,
                    selected = state.instituicaoId == inst.id,
                ) { vm.update { it.copy(instituicaoId = inst.id) } }
            }
        } else {
            OutlinedTextField(
                value = state.enderecoRetirada,
                onValueChange = { v -> vm.update { it.copy(enderecoRetirada = v) } },
                label = { Text("Endereco para retirada") },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(8.dp))
        Text("Janela", fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DeliveryChip(
                "Hoje",
                selected = state.janela == Janela.HOJE,
                modifier = Modifier.weight(1f),
            ) { vm.update { it.copy(janela = Janela.HOJE) } }
            DeliveryChip(
                "Amanha",
                selected = state.janela == Janela.AMANHA,
                modifier = Modifier.weight(1f),
            ) { vm.update { it.copy(janela = Janela.AMANHA) } }
        }

        Spacer(Modifier.height(8.dp))
        Text("Horario", fontWeight = FontWeight.SemiBold)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth().height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            timeSlots.forEach { slot ->
                item {
                    DeliveryChip(slot, selected = state.horario.startsWith(slot)) {
                        vm.update { it.copy(horario = slot) }
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { vm.back() },
                modifier = Modifier.weight(1f).height(52.dp),
            ) { Text("Voltar") }
            Button(
                onClick = { vm.next() },
                colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                modifier = Modifier.weight(1f).height(52.dp),
            ) { Text("Continuar", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun Step3(vm: DonateViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Confirmar doacao",
            color = Gray800,
            style = MaterialTheme.typography.titleLarge,
        )

        SummaryRow("Item", state.titulo)
        if (state.quantidade.isNotBlank()) SummaryRow("Quantidade", state.quantidade)
        SummaryRow(
            "Categoria",
            DonationCategory.ALL.firstOrNull { it.first == state.categoria }?.second
                ?: state.categoria,
        )
        SummaryRow(
            "Metodo",
            if (state.metodoEntrega == DeliveryMethod.EU_ENTREGO) "Eu entrego" else "Solicitar coleta",
        )
        if (state.metodoEntrega == DeliveryMethod.EU_ENTREGO) {
            val inst = state.instituicoes.firstOrNull { it.id == state.instituicaoId }
            SummaryRow("Ponto de coleta", inst?.nomeInstituicao ?: "—")
        } else {
            SummaryRow("Endereco", state.enderecoRetirada.ifBlank { "Endereco a confirmar" })
        }
        SummaryRow(
            "Quando",
            "${if (state.janela == Janela.HOJE) "Hoje" else "Amanha"} - ${state.horario}",
        )

        if (state.error != null) Text(state.error!!, color = MaterialTheme.colorScheme.error)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { vm.back() },
                modifier = Modifier.weight(1f).height(52.dp),
            ) { Text("Voltar") }
            Button(
                onClick = { vm.submit() },
                enabled = !state.submitting,
                colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                modifier = Modifier.weight(1f).height(52.dp),
            ) {
                Text(
                    if (state.submitting) "Enviando..." else "Confirmar",
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Surface(
        color = Color(0xFFF9FAFB),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                label,
                color = Gray500,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
            )
            Text(value, color = Gray800, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) Emerald100 else Gray100
    val fg = if (selected) Emerald700 else Gray500
    Surface(
        color = bg,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier.padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(label, color = fg, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DeliveryChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val bg = if (selected) Emerald600 else Gray100
    val fg = if (selected) Color.White else Gray500
    Surface(
        color = bg,
        shape = RoundedCornerShape(14.dp),
        onClick = onClick,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(label, color = fg, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun InstitutionRow(
    nome: String,
    bairro: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val border = if (selected) Emerald600 else Gray200
    Surface(
        color = if (selected) Emerald100 else Color.White,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, border),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (selected) Emerald600 else Gray100),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (selected) Icons.Filled.Check else Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = if (selected) Color.White else Gray500,
                )
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(nome, fontWeight = FontWeight.Bold, color = Gray800)
                Text(bairro, color = Gray500, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
