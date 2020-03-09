package com.gitrepos.android.data.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.gitrepos.android.R
import com.gitrepos.android.data.persistence.PreferenceManger
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Class to handle biometric authentication actions
 * @author Created by kuashok on 2020-03-01
 */

class BioAuthManager(
    private val activity: FragmentActivity,
    private val bioAuthCallBacks: BioAuthCallBacks,
    authBuilder: AuthDialogBuilder,
    private val preferenceManager: PreferenceManger
) {

    private lateinit var keyStore: KeyStore
    private lateinit var keyGenerator: KeyGenerator
    private var biometricPrompt: BiometricPrompt
    private var promptInfo: BiometricPrompt.PromptInfo
    private var cryptoObject: BiometricPrompt.CryptoObject? = null
    private lateinit var decodedKey: ByteArray
    private val encryptionKey: ByteArray by lazy {
        java.util.UUID.randomUUID().toString()
            .replace("-", "")
            .substring(0, KEY_LENGTH)
            .toByteArray()
    }

    private val keyName: String by lazy {
        createKey(KEY_ALIAS)
        KEY_ALIAS
    }

    private val prefKeyIV by lazy {
        activity.getString(R.string.pref_key_iv)
    }

    private val prefKeyEncData by lazy {
        activity.getString(R.string.pref_key_enc_data)
    }

    private val initKeyStoreAndKeyGenerator by lazy {
        setupKeyStoreAndKeyGenerator()
    }


    init {

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
        Log.d(TAG, "setupKeyStoreAndKeyGenerator")
    }

    /**
     * Gets key from keystore
     */
    private fun geSecretKey(): SecretKey {
        initKeyStoreAndKeyGenerator
        keyStore.apply {
            load(null)
        }
        return keyStore.getKey(keyName, null) as SecretKey
    }

    private fun deleteSecretKey() {
        keyStore.apply {
            load(null)
        }

        keyStore.deleteEntry(KEY_ALIAS)
    }

    /**
     * Sets up ciphers for encryption/decryption
     */
    private fun getCipher(): Cipher {
        var cipher: Cipher
        try {
            val cipherString =
                "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"
            cipher = Cipher.getInstance(cipherString)
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is NoSuchPaddingException ->
                    throw RuntimeException("Failed to get an instance of Cipher", e)
                else -> throw e
            }
        }

        return cipher
    }

    /**
     * Initialize the [Cipher] instance with the created key in the [createKey] method.
     *
     * @param keyName the key name to init the cipher
     * @return `true` if initialization succeeded, `false` if the lock screen has been disabled or
     * reset after key generation, or if a fingerprint was enrolled after key generation.
     */

    private fun initCipher(
        cipher: Cipher,
        iv: ByteArray? = null,
        mode: Int = Cipher.ENCRYPT_MODE
    ): Boolean {
        try {
            if (mode == Cipher.DECRYPT_MODE) {
                cipher.init(mode, geSecretKey(), IvParameterSpec(iv))
            } else {
                cipher.init(mode, geSecretKey())
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
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
        try {
            keyStore.load(null)

            if (!keyStore.containsAlias(keyName)) {
                val keyProperties = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                val builder = KeyGenParameterSpec.Builder(keyName, keyProperties)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(true)
                    .setRandomizedEncryptionRequired(false)
                    .setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment)

                keyGenerator.init(builder.build())
                val key = keyGenerator.generateKey()
                Log.d(TAG, "Key generated : $key")

            } else {
                Log.d(TAG, "Key already generated..")
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
                if ((errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) or
                    (errorCode == BiometricPrompt.ERROR_USER_CANCELED)
                ) {
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
                Log.d(TAG, "Authentication successful")
                bioAuthCallBacks.onAuthenticationSucceeded(result)
                cryptoObject = result.cryptoObject
                cryptoObject?.cipher?.let {
                    val key =
                        preferenceManager.getStringValue(activity.getString(R.string.pref_key_enc_data))
                    if (key.isNullOrEmpty()) {
                        Log.e(TAG, "Encrypt auth key")
                        val iv = it.iv
                        decodedKey = encryptionKey
                        // perform bio auth encryption over the plain encryption key
                        val authEncrypted = it.doFinal(encryptionKey)
                        saveEncryptedData(authEncrypted, iv)
                    } else {
                        Log.e(TAG, "Decrypt auth key")
                        val decodeBase64Key = Base64.decode(key, Base64.DEFAULT)
                        // get plain key after bio authentication
                        decodedKey = it.doFinal(decodeBase64Key)
                    }

                    Log.e(TAG, "decoded key :${String(decodedKey)}")
                }
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

        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {

            val encCipher = getCipher()
            val iv = preferenceManager.getStringValue(prefKeyIV)

            if (!iv.isNullOrEmpty()) {
                Log.d(TAG, "authenticate() : Decryption mode.")

                val decodeBase64IV = Base64.decode(iv, Base64.DEFAULT)

                if (initCipher(
                        cipher = encCipher,
                        iv = decodeBase64IV,
                        mode = Cipher.DECRYPT_MODE
                    )
                ) {
                    biometricPrompt.authenticate(
                        promptInfo,
                        BiometricPrompt.CryptoObject(encCipher)
                    )
                } else {
                    Log.d(TAG, "authenticate() : unable to initialize the cipher.")
                    bioAuthCallBacks.onKeyInvalidated()
                }

            } else {
                Log.d(TAG, "authenticate() : Encryption mode.")

                if (initCipher(cipher = encCipher)) {
                    biometricPrompt.authenticate(
                        promptInfo,
                        BiometricPrompt.CryptoObject(encCipher)
                    )
                } else {
                    Log.d(TAG, "authenticate() : unable to initialize the cipher.")
                    bioAuthCallBacks.onKeyInvalidated()
                }
            }

        }
    }

    /**
     * Check whether key is valid for authentication or not
     */
    fun canAuthenticate(): Boolean {
        val canAuthenticate = BiometricManager.from(activity).canAuthenticate()
        Log.d(TAG, "canAuthenticate() : canAuthenticate: $canAuthenticate")

        when (canAuthenticate) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                bioAuthCallBacks.onFingerPrintsEnrolled()

                val encCipher = getCipher()
                if (initCipher(cipher = encCipher)) {
                    return true
                } else {
                    Log.d(TAG, "canAuthenticate() : unable to initialize the cipher.")
                    deleteSecretKey()
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

        return false
    }


    /**
     * Saves base64 encryption format of secret key & initializationVector in sharedPrefs
     */
    private fun saveEncryptedData(dataEncrypted: ByteArray, initializationVector: ByteArray) {
        preferenceManager.run {
            putStringValue(prefKeyEncData, Base64.encodeToString(dataEncrypted, Base64.DEFAULT))
            putStringValue(prefKeyIV, Base64.encodeToString(initializationVector, Base64.DEFAULT))
        }
    }

    /**
     * Tries to encrypt given data with the generated key from [createKey]. This only works if the
     * user just authenticated via fingerprint.
     */
    fun encryptData(data: String): String? {
        try {

            val iv = preferenceManager.getStringValue(prefKeyIV)
            if (!::decodedKey.isInitialized or iv.isNullOrEmpty()) return null

            val decodeBase64IV = Base64.decode(iv, Base64.DEFAULT)

            decodeBase64IV?.let {
                val encryptedBytes = encrypt(data.toByteArray(), decodedKey, decodeBase64IV)
                val encBase64 = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
                Log.d(TAG, "encryptData() : encBase64 :$encBase64")
                return encBase64
            }

        } catch (e: Exception) {
            e.printStackTrace()
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
        return null
    }

    /**
     * Tries to decrypt given data with the generated key from [createKey]. This only works if the
     * user just authenticated via fingerprint.
     */
    fun decryptData(encryptedInput: String): String? {
        try {
            val iv = preferenceManager.getStringValue(prefKeyIV)
            if (!::decodedKey.isInitialized or iv.isNullOrEmpty()) return null

            val decodeBase64IV = Base64.decode(iv, Base64.DEFAULT)

            decodeBase64IV?.let {
                Log.d(TAG, "decryptData() : encryptedInput :$encryptedInput")
                val encryptedBytes = Base64.decode(encryptedInput, Base64.DEFAULT)
                val decryptedBytes = decrypt(encryptedBytes, decodedKey, decodeBase64IV)
                val decData = String(decryptedBytes)
                Log.d(TAG, "decryptData() : decData :$decData")
                return decData
            }
        } catch (e: Exception) {
            e.printStackTrace()
            when (e) {
                is BadPaddingException,
                is IllegalBlockSizeException -> {
                    Log.e(
                        TAG,
                        "Failed to decrypt the data with the generated key. ${e.message}"
                    )
                }
                else -> throw e
            }
        }
        return null
    }

    /**
     * Tries to encrypt the input data
     */
    private fun encrypt(data: ByteArray, key: ByteArray, ivs: ByteArray): ByteArray? {
        val cipher: Cipher = getCipher()
        val secretKeySpec = SecretKeySpec(key, KeyProperties.KEY_ALGORITHM_AES)
        val finalIvs = ByteArray(IV_SIZE)
        ivs.copyInto(finalIvs)
        val iv = IvParameterSpec(finalIvs)
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv)
        return cipher.doFinal(data)
    }

    /**
     * Tries to decrypt the input data
     */
    private fun decrypt(data: ByteArray, key: ByteArray, ivs: ByteArray): ByteArray {
        val cipher: Cipher = getCipher()
        val secretKeySpec = SecretKeySpec(key, KeyProperties.KEY_ALGORITHM_AES)
        val finalIvs = ByteArray(IV_SIZE)
        ivs.copyInto(finalIvs)
        val iv = IvParameterSpec(finalIvs)
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv)
        return cipher.doFinal(data)
    }


    companion object {
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "secret_key"
        private const val TAG = "BioAuthManager"
        private const val IV_SIZE = 16
        private const val KEY_LENGTH = 16
    }
}