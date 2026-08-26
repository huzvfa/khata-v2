package com.khata.finance.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.khata.finance.util.Hash
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "khata_settings")

class SettingsStore(private val context: Context) {

    companion object {
        private val DARK_THEME = booleanPreferencesKey("dark_theme")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_PASSWORD = stringPreferencesKey("user_password")
        private val PIN_CODE = stringPreferencesKey("pin_code")
        private val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    val darkTheme: Flow<Boolean> = context.dataStore.data.map { it[DARK_THEME] ?: true }
    val userEmail: Flow<String> = context.dataStore.data.map { it[USER_EMAIL] ?: "" }
    val userName: Flow<String> = context.dataStore.data.map { it[USER_NAME] ?: "" }
    val pinCode: Flow<String> = context.dataStore.data.map { it[PIN_CODE] ?: "" }
    val biometricEnabled: Flow<Boolean> = context.dataStore.data.map { it[BIOMETRIC_ENABLED] ?: false }
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[IS_LOGGED_IN] ?: false }

    suspend fun setDarkTheme(value: Boolean) {
        context.dataStore.edit { it[DARK_THEME] = value }
    }

    suspend fun registerUser(name: String, email: String, password: String) {
        context.dataStore.edit {
            it[USER_NAME] = name
            it[USER_EMAIL] = email
            it[USER_PASSWORD] = Hash.sha256(password)
            it[IS_LOGGED_IN] = true
        }
    }

    suspend fun checkLogin(email: String, password: String): Boolean {
        val p = context.dataStore.data.first()
        return p[USER_EMAIL] == email && p[USER_PASSWORD] == Hash.sha256(password)
    }

    suspend fun hasAccount(): Boolean {
        val p = context.dataStore.data.first()
        return !(p[USER_EMAIL].isNullOrEmpty())
    }

    suspend fun setLoggedIn(value: Boolean) {
        context.dataStore.edit { it[IS_LOGGED_IN] = value }
    }

    suspend fun setPin(pin: String) {
        context.dataStore.edit { it[PIN_CODE] = Hash.sha256(pin) }
    }

    suspend fun checkPin(pin: String): Boolean {
        val p = context.dataStore.data.first()
        return p[PIN_CODE] == Hash.sha256(pin)
    }

    suspend fun setBiometric(value: Boolean) {
        context.dataStore.edit { it[BIOMETRIC_ENABLED] = value }
    }

    suspend fun updateProfile(name: String, email: String) {
        context.dataStore.edit {
            it[USER_NAME] = name
            it[USER_EMAIL] = email
        }
    }
}
