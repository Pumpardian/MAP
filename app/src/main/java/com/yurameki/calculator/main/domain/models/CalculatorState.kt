package com.yurameki.calculator.main.domain.models

data class CalculatorState(
    val primaryValue: String = "",
    val secondaryValue: String = "",
    val lastBinaryOperator: String? = null,
    val lastOperand: String? = null,
    val isResult: Boolean = false
)