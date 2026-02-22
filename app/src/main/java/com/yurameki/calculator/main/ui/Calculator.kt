package com.yurameki.calculator.main.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yurameki.calculator.main.domain.viewmodels.CalculatorViewModel
import com.yurameki.calculator.main.ui.calculatorScreens.orientation.HorizontalScreen
import com.yurameki.calculator.main.ui.calculatorScreens.orientation.VerticalScreen

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