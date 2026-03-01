package com.yurameki.calculator

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import com.yurameki.calculator.api.showBiometryPrompt
import com.yurameki.calculator.domain.viewmodels.CalculatorViewModel
import com.yurameki.calculator.ui.Calculator
import com.yurameki.calculator.ui.calculatorScreens.CredentialsScreen
import com.yurameki.calculator.ui.theme.CalculatorTheme

class MainActivity : FragmentActivity() {
    private val viewModel by viewModels<CalculatorViewModel>()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ContextCastToActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        actionBar?.hide()
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !viewModel.isThemeLoaded.value }

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val activity = LocalContext.current as androidx.fragment.app.FragmentActivity
            var isAuthenticated by rememberSaveable { mutableStateOf(false) }
            var biometricPromptShown by rememberSaveable { mutableStateOf(false) }
            val useDarkTheme by viewModel.useDarkTheme.collectAsState(initial = true)

            LaunchedEffect(Unit) {
                if (!isAuthenticated && !biometricPromptShown) {
                    biometricPromptShown = true
                    showBiometryPrompt(
                        activity = activity,
                        onAuthenticated = { isAuthenticated = true },
                        onError = { error ->
                        }
                    )
                }
            }

            CalculatorTheme(useDarkTheme = useDarkTheme) {
                Scaffold(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
                    if (isAuthenticated) {
                        Calculator(viewModel)
                    } else {
                        CredentialsScreen(onAuthenticated = { isAuthenticated = true })
                    }
                }
            }
        }
    }
}