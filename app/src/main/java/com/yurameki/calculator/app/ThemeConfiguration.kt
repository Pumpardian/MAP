package com.yurameki.calculator.app

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object  ThemeConfiguration {
    private const val CONFIG_NAME = "calculator_config"
    private const val KEY_THEME = "theme"

    fun saveThemeConfiguration(context: Context, theme: String) {
        val cfg: SharedPreferences = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE)
        cfg.edit { putString(KEY_THEME, theme) }
    }
}