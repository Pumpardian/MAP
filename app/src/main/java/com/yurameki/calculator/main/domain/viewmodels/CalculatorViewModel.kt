package com.yurameki.calculator.main.domain.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.yurameki.calculator.main.api.FlashlightHandler
import com.yurameki.calculator.main.app.CalculatorUseCase
import com.yurameki.calculator.main.domain.models.CalculatorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalculatorViewModel(app: Application) : AndroidViewModel(app) {
    private val uiStateModel = MutableStateFlow(CalculatorState())
    val uiState = uiStateModel.asStateFlow()
    private val calculatorUseCase = CalculatorUseCase()

    fun onButtonClicked(
        button: String,
        flashlightHandler: FlashlightHandler? = null
    ) {
        val oldState = uiStateModel.value
        val newState = calculatorUseCase.onButtonClicked(oldState, button, flashlightHandler)
        uiStateModel.value = newState

        if (button == "=" && newState.primaryValue != "Error") {
            val expression = oldState.primaryValue
            val result = newState.primaryValue
        }
    }
}