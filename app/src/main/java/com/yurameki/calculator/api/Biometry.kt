package com.yurameki.calculator.api

import androidx.fragment.app.FragmentActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

fun showBiometryPrompt(
    activity: FragmentActivity,
    onAuthenticated: () -> Unit,
    onError: (String) -> Unit = {}
) {
    val biometricManager = BiometricManager.from(activity)
    if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        != BiometricManager.BIOMETRIC_SUCCESS
    ) {
        onError("Biometry is unavailable")
        return
    }

    val executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            onError(errString.toString())
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            onAuthenticated()
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            onError("Couldn't recognize biometry")
        }
    })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Biometry Auth")
        .setSubtitle("Use fingerprint")
        .setNegativeButtonText("Use PIN")
        .build()

    biometricPrompt.authenticate(promptInfo)
}