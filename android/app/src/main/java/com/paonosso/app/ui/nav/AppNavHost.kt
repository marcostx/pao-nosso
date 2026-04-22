package com.paonosso.app.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.paonosso.app.PaoNossoApplication
import com.paonosso.app.data.model.UserType
import com.paonosso.app.ui.components.AppScaffold
import com.paonosso.app.ui.components.DonorNav
import com.paonosso.app.ui.components.InstitutionNav
import com.paonosso.app.ui.screens.agenda.AgendaScreen
import com.paonosso.app.ui.screens.auth.LoginScreen
import com.paonosso.app.ui.screens.auth.RegisterScreen
import com.paonosso.app.ui.screens.donate.DonateFlowScreen
import com.paonosso.app.ui.screens.home.HomeScreen
import com.paonosso.app.ui.screens.institution.InstitutionHomeScreen
import com.paonosso.app.ui.screens.institution.InstitutionRequestsScreen
import com.paonosso.app.ui.screens.map.MapPlaceholderScreen
import com.paonosso.app.ui.screens.profile.ProfileScreen

@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val container = PaoNossoApplication.container()
        val token = container.tokenStore.getToken()
        val tipo = container.tokenStore.getTipo()
        startDestination = when {
            token.isNullOrEmpty() -> Routes.AUTH_LOGIN
            tipo == UserType.INSTITUICAO -> Routes.INST_HOME
            else -> Routes.APP_HOME
        }
    }

    val start = startDestination ?: run {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.White),
            contentAlignment = Alignment.Center,
        ) { CircularProgressIndicator() }
        return
    }

    NavHost(navController = nav, startDestination = start) {
        composable(Routes.AUTH_LOGIN) {
            LoginScreen(
                onLoggedIn = { tipo -> nav.routeAfterAuth(tipo) },
                onRegisterClick = { nav.navigate(Routes.AUTH_REGISTER) },
            )
        }
        composable(Routes.AUTH_REGISTER) {
            RegisterScreen(
                onRegistered = { tipo -> nav.routeAfterAuth(tipo) },
                onLoginClick = { nav.popBackStack() },
            )
        }

        // Donor shell — cada aba é uma rota independente reusando o AppScaffold.
        composable(Routes.APP_HOME) { DonorShell(nav, "home") }
        composable(Routes.APP_AGENDA) { DonorShell(nav, "agenda") }
        composable(Routes.APP_MAP) { DonorShell(nav, "map") }
        composable(Routes.APP_PROFILE) { DonorShell(nav, "profile") }
        composable(Routes.APP_DONATE) {
            DonateFlowScreen(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(),
                onClose = { nav.popBackStack() },
                onCompleted = {
                    nav.popBackStack()
                    nav.navigate(Routes.APP_AGENDA) {
                        popUpTo(Routes.APP_HOME) { inclusive = false }
                    }
                },
            )
        }

        // Institution shell
        composable(Routes.INST_HOME) { InstitutionShell(nav, "home") }
        composable(Routes.INST_REQUESTS) { InstitutionShell(nav, "requests") }
        composable(Routes.INST_PROFILE) { InstitutionShell(nav, "profile") }
    }
}

private fun NavHostController.routeAfterAuth(tipo: String) {
    val target = if (tipo == UserType.INSTITUICAO) Routes.INST_HOME else Routes.APP_HOME
    navigate(target) {
        popUpTo(Routes.AUTH_LOGIN) { inclusive = true }
        popUpTo(Routes.AUTH_REGISTER) { inclusive = true }
        launchSingleTop = true
    }
}

@Composable
private fun DonorShell(nav: NavHostController, selected: String) {
    val items = remember { DonorNav.items(hasPendingAppointments = false) }
    AppScaffold(
        items = items,
        selectedKey = selected,
        onSelect = { key ->
            val route = when (key) {
                "home" -> Routes.APP_HOME
                "agenda" -> Routes.APP_AGENDA
                "map" -> Routes.APP_MAP
                else -> Routes.APP_PROFILE
            }
            nav.navigate(route) {
                popUpTo(Routes.APP_HOME) { inclusive = false }
                launchSingleTop = true
            }
        },
        onFabClick = { nav.navigate(Routes.APP_DONATE) },
    ) { padding ->
        when (selected) {
            "home" -> HomeScreen(
                onNewDonation = { nav.navigate(Routes.APP_DONATE) },
                onOpenAgenda = { nav.navigate(Routes.APP_AGENDA) },
                contentPadding = padding,
            )
            "agenda" -> AgendaScreen(contentPadding = padding)
            "map" -> MapPlaceholderScreen(contentPadding = padding)
            "profile" -> ProfileScreen(
                contentPadding = padding,
                onLoggedOut = {
                    nav.navigate(Routes.AUTH_LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
    }
}

@Composable
private fun InstitutionShell(nav: NavHostController, selected: String) {
    val items = remember { InstitutionNav.items(hasPendingRequests = false) }
    AppScaffold(
        items = items,
        selectedKey = selected,
        onSelect = { key ->
            val route = when (key) {
                "home" -> Routes.INST_HOME
                "requests" -> Routes.INST_REQUESTS
                else -> Routes.INST_PROFILE
            }
            nav.navigate(route) {
                popUpTo(Routes.INST_HOME) { inclusive = false }
                launchSingleTop = true
            }
        },
        onFabClick = { nav.navigate(Routes.INST_HOME) },
        showFab = false,
    ) { padding ->
        when (selected) {
            "home" -> InstitutionHomeScreen(contentPadding = padding)
            "requests" -> InstitutionRequestsScreen(contentPadding = padding)
            "profile" -> ProfileScreen(
                contentPadding = padding,
                onLoggedOut = {
                    nav.navigate(Routes.AUTH_LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
    }
}
