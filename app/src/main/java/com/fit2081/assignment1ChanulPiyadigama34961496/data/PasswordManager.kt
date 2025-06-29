package com.fit2081.assignment1ChanulPiyadigama34961496.data
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class PasswordManager(private val context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    //creates a shared preferences file that is specifically encrypted for storing the passwords
    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "encrypted_passwords",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun savePassword(userId: String, password: String) {
        encryptedPrefs.edit().putString(userId, password).apply()
    }

    fun getPassword(userId: String): String? {
        return encryptedPrefs.getString(userId, null)
    }

    fun verifyPassword(userId: String, inputPassword: String): Boolean {
        return getPassword(userId) == inputPassword
    }

}