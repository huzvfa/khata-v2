package com.khata.finance.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.khata.finance.KhataApp
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val settings = (app as KhataApp).settings

    val darkTheme: StateFlow<Boolean> =
        settings.darkTheme.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val isLoggedIn: StateFlow<Boolean> =
        settings.isLoggedIn.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val userName: StateFlow<String> =
        settings.userName.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val userEmail: StateFlow<String> =
        settings.userEmail.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val pinCode: StateFlow<String> =
        settings.pinCode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val biometricEnabled: StateFlow<Boolean> =
        settings.biometricEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun toggleTheme(dark: Boolean) = viewModelScope.launch { settings.setDarkTheme(dark) }

    fun signUp(name: String, email: String, password: String, onDone: () -> Unit) {
        viewModelScope.launch {
            settings.registerUser(name, email, password)
            onDone()
        }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val ok = settings.checkLogin(email, password)
            if (ok) settings.setLoggedIn(true)
            onResult(ok)
        }
    }

    fun logout() = viewModelScope.launch { settings.setLoggedIn(false) }

    fun setPin(pin: String) = viewModelScope.launch { settings.setPin(pin) }

    fun setBiometric(enabled: Boolean) = viewModelScope.launch { settings.setBiometric(enabled) }

    fun checkPin(pin: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch { onResult(settings.checkPin(pin)) }
    }

    fun updateProfile(name: String, email: String) =
        viewModelScope.launch { settings.updateProfile(name, email) }
}
