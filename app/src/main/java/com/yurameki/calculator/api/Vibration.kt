package com.yurameki.calculator.api

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class VibrationHandler(context: Context) {
    private val vibrator: Vibrator =
        context.getSystemService(VibratorManager::class.java).defaultVibrator

    fun vibrate(milliseconds: Long = 25) {
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    milliseconds,
                    VibrationEffect.EFFECT_TICK
                )
            )
        }
    }
}

@Composable
fun rememberVibrationHandler(): VibrationHandler {
    val context = LocalContext.current
    return remember { VibrationHandler(context) }
}