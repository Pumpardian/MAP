package com.yurameki.calculator

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.yurameki.calculator.domain.viewmodels.CalculatorViewModel
import com.yurameki.calculator.ui.theme.CalculatorTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<CalculatorViewModel>()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        actionBar?.hide()
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !viewModel.isThemeLoaded.value }

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val useDarkTheme by viewModel.useDarkTheme.collectAsState(initial = true)

            CalculatorTheme(useDarkTheme = useDarkTheme) {
                Scaffold(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
                    Calculator()
                }
            }
        }
    }
}