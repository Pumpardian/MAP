package com.yurameki.calculator.domain.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.yurameki.calculator.api.FlashlightHandler
import com.yurameki.calculator.api.getDeviceId
import com.yurameki.calculator.app.CalculatorLogic
import com.yurameki.calculator.app.NotificationsCenter
import com.yurameki.calculator.domain.models.CalculatorHistoryItem
import com.yurameki.calculator.domain.models.CalculatorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.firestore.FirebaseFirestore
import com.yurameki.calculator.app.ThemeConfiguration
import com.yurameki.calculator.domain.models.AppTheme

class CalculatorViewModel(app: Application) : AndroidViewModel(app) {
    private val calculatorLogic = CalculatorLogic()
    private val fb = FirebaseFirestore.getInstance()
    private val deviceId: String by lazy { getDeviceId(app) }

    private val historyCollection = "CalculatorHistory"
    private val themeCollection = "CalculatorAppTheme"

    private val uiStateFlow = MutableStateFlow(CalculatorState())
    val uiState = uiStateFlow.asStateFlow()

    private val useDarkThemeFlow = MutableStateFlow(true)
    val useDarkTheme = useDarkThemeFlow.asStateFlow()

    init {
        loadHistoryFromFirebase()
        loadThemeFromFirebase()
    }

    fun onButtonClicked(
        button: String,
        flashlightHandler: FlashlightHandler? = null,
        notificationsCenter: NotificationsCenter? = null
    ) {
        val oldState = uiStateFlow.value
        val newState = calculatorLogic.onButtonClicked(oldState,
            button,
            flashlightHandler,
            notificationsCenter)
        uiStateFlow.value = newState

        if (button == "=" && newState.primaryValue != "Error") {
            val expression = oldState.primaryValue
            val result = newState.primaryValue
            addHistoryItem(expression, result)
        }
    }

    fun toggleHistory() {
        val current = uiStateFlow.value
        val newHistoryState = !current.isHistoryVisible
        if (newHistoryState) {
            loadHistoryFromFirebase()
        }
        uiStateFlow.value = current.copy(isHistoryVisible = newHistoryState)
    }

    fun clearHistory() {
        uiStateFlow.value.history.forEach { item ->
            if (item.id.isNotEmpty()) {
                fb.collection(historyCollection).document(item.id).delete()
            }
        }
        uiStateFlow.value = uiStateFlow.value.copy(history = emptyList())
    }

    private fun addHistoryItem(expression: String, result: String) {
        if (expression.isBlank()) return

        val newItem = CalculatorHistoryItem(
            expression = expression,
            result = result,
            timestamp = System.currentTimeMillis(),
            deviceId = deviceId
        )

        uiStateFlow.value = uiStateFlow.value.copy(history = uiStateFlow.value.history + newItem)

        fb.collection(historyCollection)
            .add(newItem)
            .addOnSuccessListener { docRef ->
                val itemWithId = newItem.copy(id = docRef.id)
                val updatedList = uiStateFlow.value.history.map {
                    if (it.timestamp == newItem.timestamp) itemWithId else it
                }
                uiStateFlow.value = uiStateFlow.value.copy(history = updatedList)
            }
            .addOnFailureListener { exception -> }
    }

    private fun loadHistoryFromFirebase() {
        fb.collection(historyCollection)
            .whereEqualTo("deviceId", deviceId)
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { snapshot ->
                val items = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(CalculatorHistoryItem::class.java)?.copy(id = doc.id)
                }
                uiStateFlow.value = uiStateFlow.value.copy(history = items)
            }
            .addOnFailureListener { exception ->
            }
    }

    private val isThemeLoadedFlow = MutableStateFlow(false)
    val isThemeLoaded = isThemeLoadedFlow.asStateFlow()

    private fun loadThemeFromFirebase() {
        fb.collection(themeCollection)
            .document(deviceId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val theme = document.getString("themeName")
                    useDarkThemeFlow.value = theme == "dark"
                    ThemeConfiguration.saveThemeConfiguration(getApplication(), theme ?: "light")
                } else {
                    useDarkThemeFlow.value = false
                    fb.collection(themeCollection)
                        .document(deviceId)
                        .set(AppTheme("light", deviceId))
                    ThemeConfiguration.saveThemeConfiguration(getApplication(), "light")
                }
                isThemeLoadedFlow.value = true
            }
            .addOnFailureListener { exception ->
                useDarkThemeFlow.value = false
                ThemeConfiguration.saveThemeConfiguration(getApplication(), "light")
                isThemeLoadedFlow.value = true
            }
    }

    fun toggleTheme() {
        val newTheme = if (useDarkThemeFlow.value) "light" else "dark"
        useDarkThemeFlow.value = !useDarkThemeFlow.value

        fb.collection(themeCollection)
            .document(deviceId)
            .set(AppTheme(newTheme, deviceId))
            .addOnSuccessListener {
                ThemeConfiguration.saveThemeConfiguration(getApplication(), newTheme)
            }
            .addOnFailureListener { exception ->
            }
    }
}