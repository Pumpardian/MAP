package com.yurameki.calculator.domain.models

data class CalculatorState(
    val primaryValue: String = "",
    val secondaryValue: String = "",
    val lastBinaryOperator: String? = null,
    val lastOperand: String? = null,
    val history: List<CalculatorHistoryItem> = emptyList(),
    val isResult: Boolean = false,
    val isHistoryVisible: Boolean = false
)