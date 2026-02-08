package com.yurameki.calculator.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.yurameki.calculator.ui.theme.CalculatorTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CalculatorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        actionBar?.hide()
        val splashScreen = installSplashScreen()
        var keepSplashVisible = true
        splashScreen.setKeepOnScreenCondition { keepSplashVisible }

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            delay(2000)
            keepSplashVisible = false
        }

        setContent {
            CalculatorTheme {
                Scaffold(containerColor = Color.Black) { innerPadding ->
                    CalculatorView(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}