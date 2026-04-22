package com.paonosso.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paonosso.app.data.model.UserType
import com.paonosso.app.ui.theme.Emerald600
import com.paonosso.app.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onRegistered: (tipo: String) -> Unit,
    onLoginClick: () -> Unit,
) {
    val vm: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
    val state by vm.state.collectAsStateWithLifecycle()

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf(UserType.DOADOR) }

    LaunchedEffect(state.resultTipo) {
        state.resultTipo?.let(onRegistered)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Criar conta", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = telefone,
            onValueChange = { telefone = it },
            label = { Text("Telefone") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha (min. 6)") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))

        Text("Eu sou:")
        Spacer(Modifier.height(4.dp))
        Row {
            TipoChip("Doador", selected = tipo == UserType.DOADOR) { tipo = UserType.DOADOR }
            Spacer(Modifier.size(8.dp))
            TipoChip(
                "Instituicao",
                selected = tipo == UserType.INSTITUICAO,
            ) { tipo = UserType.INSTITUICAO }
        }

        if (state.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(state.error!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { vm.register(nome, email, senha, telefone, tipo) },
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
                Text("Criar conta", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onLoginClick, modifier = Modifier.fillMaxWidth()) {
            Text("Ja tem conta? Entre", color = Emerald600)
        }
    }
}

@Composable
private fun TipoChip(label: String, selected: Boolean, onClick: () -> Unit) {
    if (selected) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
        ) { Text(label, color = Color.White) }
    } else {
        OutlinedButton(onClick = onClick) { Text(label) }
    }
}
