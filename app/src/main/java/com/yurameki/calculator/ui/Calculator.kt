package com.yurameki.calculator

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yurameki.calculator.calculatorScreens.orientation.HorizontalScreen
import com.yurameki.calculator.calculatorScreens.orientation.VerticalScreen
import com.yurameki.calculator.domain.viewmodels.CalculatorViewModel

@Composable
fun Calculator(viewModel: CalculatorViewModel = viewModel()) {
    val localConfig = LocalConfiguration.current
    if (localConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        VerticalScreen(viewModel)
    }
    else {
        HorizontalScreen(viewModel)
    }
}