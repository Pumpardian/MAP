package com.yurameki.calculator.domain.models

data class CalculatorHistoryItem(
    val id: String = "",
    val expression: String = "",
    val result: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val deviceId: String = ""
)