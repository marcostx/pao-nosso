package com.paonosso.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paonosso.app.ui.theme.Emerald600
import com.paonosso.app.ui.theme.Gray400
import com.paonosso.app.ui.theme.Orange500

data class BottomNavItem(
    val key: String,
    val label: String,
    val icon: ImageVector,
    val showBadge: Boolean = false,
)

/**
 * Scaffold compartilhado pelo doador e pela instituicao.
 * Reproduz a barra inferior do mock: itens de navegacao + um FAB central elevado.
 */
@Composable
fun AppScaffold(
    items: List<BottomNavItem>,
    selectedKey: String,
    onSelect: (String) -> Unit,
    onFabClick: () -> Unit,
    showFab: Boolean = true,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            BottomNavBar(
                items = items,
                selectedKey = selectedKey,
                onSelect = onSelect,
                onFabClick = onFabClick,
                showFab = showFab,
            )
        },
        content = content,
    )
}

@Composable
private fun BottomNavBar(
    items: List<BottomNavItem>,
    selectedKey: String,
    onSelect: (String) -> Unit,
    onFabClick: () -> Unit,
    showFab: Boolean,
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val mid = items.size / 2
                items.take(mid).forEach { item ->
                    NavItem(item = item, selected = selectedKey == item.key) {
                        onSelect(item.key)
                    }
                }
                if (showFab) {
                    Box(modifier = Modifier.size(56.dp))
                }
                items.drop(mid).forEach { item ->
                    NavItem(item = item, selected = selectedKey == item.key) {
                        onSelect(item.key)
                    }
                }
            }

            if (showFab) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-24).dp)
                        .size(56.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Emerald600)
                        .clickable(onClick = onFabClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Nova doacao",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun NavItem(item: BottomNavItem, selected: Boolean, onClick: () -> Unit) {
    val tint = if (selected) Emerald600 else Gray400
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = tint,
                modifier = Modifier.size(22.dp),
            )
            if (item.showBadge) {
                Box(
                    modifier = Modifier
                        .offset(x = 6.dp, y = (-2).dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Orange500),
                )
            }
        }
        Text(
            text = item.label.uppercase(),
            color = tint,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

object DonorNav {
    fun items(hasPendingAppointments: Boolean): List<BottomNavItem> = listOf(
        BottomNavItem("home", "Inicio", Icons.Outlined.Home),
        BottomNavItem(
            "agenda", "Agenda", Icons.Outlined.CalendarMonth,
            showBadge = hasPendingAppointments,
        ),
        BottomNavItem("map", "Mapa", Icons.Outlined.Place),
        BottomNavItem("profile", "Perfil", Icons.Outlined.AccountCircle),
    )
}

object InstitutionNav {
    fun items(hasPendingRequests: Boolean): List<BottomNavItem> = listOf(
        BottomNavItem("home", "Doacoes", Icons.Outlined.Home),
        BottomNavItem(
            "requests", "Pedidos", Icons.Outlined.CalendarMonth,
            showBadge = hasPendingRequests,
        ),
        BottomNavItem("profile", "Perfil", Icons.Outlined.AccountCircle),
    )
}
