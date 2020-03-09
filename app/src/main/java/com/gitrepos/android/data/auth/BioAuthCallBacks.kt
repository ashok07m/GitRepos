package com.gitrepos.android.data.auth

import androidx.biometric.BiometricPrompt

interface BioAuthCallBacks {

    fun onAuthenticationError(errorCode: Int, errString: CharSequence)
    fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult)
    fun onAuthNegativeButtonClicked()
    fun onKeyInvalidated()
    fun onFingerPrintsNotEnrolled()
    fun onFingerPrintsEnrolled()
    fun onFingerPrintHardwareUnavailable()
    fun onNoFingerPrintSensorOnDevice()
}