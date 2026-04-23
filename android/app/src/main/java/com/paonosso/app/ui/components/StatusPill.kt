package com.paonosso.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paonosso.app.ui.theme.Emerald100
import com.paonosso.app.ui.theme.Emerald700
import com.paonosso.app.ui.theme.Gray100
import com.paonosso.app.ui.theme.Gray200
import com.paonosso.app.ui.theme.Gray500
import com.paonosso.app.ui.theme.Gray700
import com.paonosso.app.ui.theme.Orange100
import com.paonosso.app.ui.theme.Orange700
import com.paonosso.app.ui.theme.Red100
import com.paonosso.app.ui.theme.Red700

enum class StatusKind { Confirmado, Pendente, Recusado, Concluido, Cancelado, Aguardando, Neutro }

fun statusKindFor(status: String?): StatusKind = when (status?.uppercase()) {
    "ACEITA", "CONFIRMADO" -> StatusKind.Confirmado
    "PENDENTE" -> StatusKind.Pendente
    "RECUSADA", "RECUSADO" -> StatusKind.Recusado
    "CONCLUIDA", "CONCLUIDO" -> StatusKind.Concluido
    "CANCELADA", "CANCELADO" -> StatusKind.Cancelado
    "AGUARDANDO" -> StatusKind.Aguardando
    else -> StatusKind.Neutro
}

fun displayStatus(status: String?): String = when (status?.uppercase()) {
    "ACEITA" -> "Confirmado"
    "PENDENTE" -> "Pendente"
    "RECUSADA" -> "Recusado"
    "CONCLUIDA" -> "Concluido"
    "CANCELADA" -> "Cancelado"
    "AGUARDANDO" -> "Aguardando"
    else -> status ?: ""
}

@Composable
fun StatusPill(status: String?, modifier: Modifier = Modifier) {
    val kind = statusKindFor(status)
    val (bg, fg) = when (kind) {
        StatusKind.Confirmado -> Emerald100 to Emerald700
        StatusKind.Pendente -> Orange100 to Orange700
        StatusKind.Recusado -> Red100 to Red700
        StatusKind.Cancelado -> Red100 to Red700
        StatusKind.Concluido -> Emerald100 to Emerald700
        StatusKind.Aguardando -> Gray200 to Gray700
        StatusKind.Neutro -> Gray100 to Gray500
    }
    Text(
        text = displayStatus(status).uppercase(),
        color = fg,
        fontSize = 10.sp,
        fontWeight = FontWeight.Black,
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}

@Composable
fun StatusPillRaw(text: String, bg: Color, fg: Color, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        color = fg,
        fontSize = 10.sp,
        fontWeight = FontWeight.Black,
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}
