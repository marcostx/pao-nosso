package com.paonosso.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.text.KeyboardOptions
import com.paonosso.app.viewmodel.AuthViewModel
import com.paonosso.app.ui.theme.Emerald100
import com.paonosso.app.ui.theme.Emerald600
import com.paonosso.app.ui.theme.Gray500

@Composable
fun LoginScreen(
    onLoggedIn: (tipo: String) -> Unit,
    onRegisterClick: () -> Unit,
) {
    val vm: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
    val state by vm.state.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    LaunchedEffect(state.resultTipo) {
        state.resultTipo?.let(onLoggedIn)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Emerald100),
            contentAlignment = Alignment.Center,
        ) {
            Text("PN", color = Emerald600, fontWeight = FontWeight.Black, fontSize = 22.sp)
        }
        Spacer(Modifier.height(16.dp))
        Text("Pao Nosso", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Entre com sua conta para continuar",
            color = Gray500,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(state.error!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { vm.login(email, senha) },
            enabled = !state.loading,
            colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        ) {
            if (state.loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp),
                )
            } else {
                Text("Entrar", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onRegisterClick, modifier = Modifier.fillMaxWidth()) {
            Text("Nao tem conta? Cadastre-se", color = Emerald600)
        }
    }
}
