package com.gitrepos.android.data.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.*

/**
 * Class to handle biometric authentication actions
 * @author Created by kuashok on 2020-03-01
 */

class BioAuthManager(
    private val activity: FragmentActivity,
    private val bioAuthCallBacks: BioAuthCallBacks,
    private val authBuilder: AuthDialogBuilder
) {

    private lateinit var keyStore: KeyStore
    private lateinit var keyGenerator: KeyGenerator
    private var biometricPrompt: BiometricPrompt
    private var promptInfo: BiometricPrompt.PromptInfo
    private var defaultCipher: Cipher
    private val keyName: String by lazy {
        createKey(DEFAULT_KEY_NAME)
        DEFAULT_KEY_NAME
    }

    init {
        setupKeyStoreAndKeyGenerator()
        defaultCipher = setupCiphers()
        promptInfo = createPromptInfo(activity, authBuilder)
        biometricPrompt = createBiometricPrompt(activity)
    }

    /**
     * Sets up KeyStore and KeyGenerator
     */
    private fun setupKeyStoreAndKeyGenerator() {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        } catch (e: KeyStoreException) {
            throw RuntimeException("Failed to get an instance of KeyStore", e)
        }

        try {
            keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is NoSuchProviderException ->
                    throw RuntimeException("Failed to get an instance of KeyGenerator", e)
                else -> throw e
            }
        }
    }

    /**
     * Sets up default cipher
     */
    private fun setupCiphers(): Cipher {
        val defaultCipher: Cipher
        try {
            val cipherString =
                "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"
            defaultCipher = Cipher.getInstance(cipherString)
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is NoSuchPaddingException ->
                    throw RuntimeException("Failed to get an instance of Cipher", e)
                else -> throw e
            }
        }
        return defaultCipher
    }

    /**
     * Initialize the [Cipher] instance with the created key in the [createKey] method.
     *
     * @param keyName the key name to init the cipher
     * @return `true` if initialization succeeded, `false` if the lock screen has been disabled or
     * reset after key generation, or if a fingerprint was enrolled after key generation.
     */
    private fun initCipher(cipher: Cipher, keyName: String): Boolean {
        try {
            keyStore.load(null)
            cipher.init(Cipher.ENCRYPT_MODE, keyStore.getKey(keyName, null) as SecretKey)
            return true
        } catch (e: Exception) {
            when (e) {
                is KeyPermanentlyInvalidatedException -> return false
                is KeyStoreException,
                is CertificateException,
                is UnrecoverableKeyException,
                is IOException,
                is NoSuchAlgorithmException,
                is InvalidKeyException -> throw RuntimeException("Failed to init Cipher", e)
                else -> throw e
            }
        }
    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with a fingerprint.
     *
     * @param keyName the name of the key to be created
     * @param invalidatedByBiometricEnrollment if `false` is passed, the created key will not be
     * invalidated even if a new fingerprint is enrolled. The default value is `true` - the key will
     * be invalidated if a new fingerprint is enrolled.
     */
    private fun createKey(keyName: String, invalidatedByBiometricEnrollment: Boolean = true) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of enrolled
        // fingerprints has changed.
        try {
            keyStore.load(null)

            val keyProperties = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            val builder = KeyGenParameterSpec.Builder(keyName, keyProperties)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment)

            keyGenerator.run {
                init(builder.build())
                generateKey()
            }
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is InvalidAlgorithmParameterException,
                is CertificateException,
                is IOException -> throw RuntimeException(e)
                else -> throw e
            }
        }
    }

    /**
     * Creates Biometric prompt with callbacks for fragment
     */
    private fun createBiometricPrompt(activity: FragmentActivity): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(activity.applicationContext)

        val callback = object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.d(TAG, "$errorCode :: $errString")
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    bioAuthCallBacks.onAuthNegativeButtonClicked()
                } else {
                    bioAuthCallBacks.onAuthenticationError(errorCode, errString)
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d(TAG, "Authentication failed for an unknown reason")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Log.d(TAG, "Authentication was successful")
                bioAuthCallBacks.onAuthenticationSucceeded(result)
            }
        }

        return BiometricPrompt(activity, executor, callback)
    }

    /**
     * Creates info for Biometric dialog
     */
    private fun createPromptInfo(
        context: Context,
        authBuilder: AuthDialogBuilder
    ): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(authBuilder.title))
            .setSubtitle(context.getString(authBuilder.subTitle))
            .setDescription(context.getString(authBuilder.description))
            .setConfirmationRequired(false)
            .setNegativeButtonText(context.getString(authBuilder.negativeButtonText))
            .build()
    }

    /**
     * Shows biometric prompt to user
     */
    fun authenticate() {
        val canAuthenticate = BiometricManager.from(activity).canAuthenticate()
        Log.d(TAG, "authenticate() : canAuthenticate: $canAuthenticate")

        when (canAuthenticate) {

            BiometricManager.BIOMETRIC_SUCCESS -> {
                if (initCipher(defaultCipher, keyName)) {
                    biometricPrompt.authenticate(
                        promptInfo,
                        BiometricPrompt.CryptoObject(defaultCipher)
                    )
                } else {
                    Log.d(TAG, "authenticate() : unable to initialize the cipher.")
                    bioAuthCallBacks.onKeyInvalidated()
                }
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                bioAuthCallBacks.onFingerPrintsNotEnrolled()
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                bioAuthCallBacks.onFingerPrintHardwareUnavailable()
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                bioAuthCallBacks.onNoFingerPrintSensorOnDevice()
            }
        }
    }

    fun canAuthenticate(): Boolean {
        val canAuthenticate = BiometricManager.from(activity).canAuthenticate()
        Log.d(TAG, "authenticate() : canAuthenticate: $canAuthenticate")

        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            if (initCipher(defaultCipher, keyName)) {
                return true
            } else {
                Log.d(TAG, "authenticate() : unable to initialize the cipher.")
                bioAuthCallBacks.onKeyInvalidated()
            }
        }
        return false
    }

    /**
     * Tries to encrypt given data with the generated key from [createKey]. This only works if the
     * user just authenticated via fingerprint.
     */
    fun encryptData(cipher: Cipher, data: String) {
        try {
            cipher.doFinal(data.toByteArray())
        } catch (e: Exception) {
            when (e) {
                is BadPaddingException,
                is IllegalBlockSizeException -> {
                    Log.e(
                        TAG,
                        "Failed to encrypt the data with the generated key. ${e.message}"
                    )
                }
                else -> throw e
            }
        }
    }


    companion object {
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val DEFAULT_KEY_NAME = "default_key"
        private const val TAG = "BioAuthManager"

    }
}