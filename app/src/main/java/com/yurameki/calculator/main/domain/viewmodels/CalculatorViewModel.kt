package com.yurameki.calculator.main.domain.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.yurameki.calculator.main.domain.models.CalculatorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalculatorViewModel(app: Application) : AndroidViewModel(app) {
    private val uiStateModel = MutableStateFlow(CalculatorState())
    val uiState = uiStateModel.asStateFlow()

    fun onButtonClicked(
        button: String
    ) {
        //Button clicking logic here
    }
}