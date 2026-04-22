package com.paonosso.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.paonosso.app.ui.nav.AppNavHost
import com.paonosso.app.ui.theme.PaoNossoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PaoNossoTheme {
                AppNavHost()
            }
        }
    }
}
