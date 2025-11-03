package com.paonosso.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.paonosso.app.data.api.ApiClient
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var tvConnectionStatus: MaterialTextView
    private lateinit var tvApiStatus: MaterialTextView
    private lateinit var btnDoador: MaterialButton
    private lateinit var btnInstituicao: MaterialButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        checkApiConnection()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        tvConnectionStatus = findViewById(R.id.tv_connection_status)
        tvApiStatus = findViewById(R.id.tv_api_status)
        btnDoador = findViewById(R.id.btn_doador)
        btnInstituicao = findViewById(R.id.btn_instituicao)
    }
    
    private fun checkApiConnection() {
        tvConnectionStatus.text = "Verificando conexão com API..."
        
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.healthCheck()
                
                if (response.status == "ok") {
                    tvConnectionStatus.text = "✅ Conectado ao servidor"
                    tvApiStatus.text = "Backend: ${response.message}\nVersão: ${response.version}"
                    tvApiStatus.setTextColor(getColor(R.color.success))
                    
                    // Habilita botões
                    btnDoador.isEnabled = true
                    btnInstituicao.isEnabled = true
                } else {
                    showConnectionError("Status inesperado: ${response.status}")
                }
            } catch (e: Exception) {
                showConnectionError(e.message ?: "Erro desconhecido")
            }
        }
    }
    
    private fun showConnectionError(error: String) {
        tvConnectionStatus.text = "❌ Erro de conexão"
        tvApiStatus.text = "Não foi possível conectar ao backend.\n\nErro: $error\n\nVerifique se o servidor está rodando em:\n${getString(R.string.api_base_url)}"
        tvApiStatus.setTextColor(getColor(R.color.error))
        
        // Desabilita botões
        btnDoador.isEnabled = false
        btnInstituicao.isEnabled = false
    }
    
    private fun setupClickListeners() {
        btnDoador.setOnClickListener {
            Toast.makeText(this, "🎉 Fluxo de Doador (em breve)", Toast.LENGTH_SHORT).show()
        }
        
        btnInstituicao.setOnClickListener {
            Toast.makeText(this, "🏢 Fluxo de Instituição (em breve)", Toast.LENGTH_SHORT).show()
        }
    }
}

