package com.khata.finance

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.khata.finance.security.BiometricHelper
import com.khata.finance.ui.auth.LockScreen
import com.khata.finance.ui.auth.WelcomeScreen
import com.khata.finance.ui.screens.HomePlaceholder
import com.khata.finance.ui.theme.KhataTheme
import com.khata.finance.viewmodel.AuthViewModel

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: AuthViewModel = viewModel()
            val darkTheme by viewModel.darkTheme.collectAsState()
            KhataTheme(darkTheme = darkTheme) {
                AppRoot(viewModel, this)
            }
        }
    }
}

private enum class AppState { WELCOME, LOCKED, HOME }

@Composable
private fun AppRoot(viewModel: AuthViewModel, activity: FragmentActivity) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val darkTheme by viewModel.darkTheme.collectAsState()
    val pinCode by viewModel.pinCode.collectAsState()
    val biometricEnabled by viewModel.biometricEnabled.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    LaunchedEffect(Unit) {
        val perms = mutableListOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionLauncher.launch(perms.toTypedArray())
    }

    // Decide the starting state once logged-in status is known.
    var unlocked by remember { mutableStateOf(false) }
    var pinError by remember { mutableStateOf(false) }

    val state = when {
        !isLoggedIn -> AppState.WELCOME
        pinCode.isNotEmpty() && !unlocked -> AppState.LOCKED
        else -> AppState.HOME
    }

    when (state) {
        AppState.WELCOME -> WelcomeScreen(viewModel) { unlocked = true }
        AppState.LOCKED -> LockScreen(
            title = if (pinError) "Wrong PIN, try again" else "Enter your PIN",
            showBiometric = biometricEnabled && BiometricHelper.isAvailable(activity),
            onBiometricClick = {
                BiometricHelper.authenticate(
                    activity,
                    onSuccess = { unlocked = true },
                    onError = { }
                )
            },
            onPinComplete = { entered ->
                viewModel.checkPin(entered) { ok ->
                    if (ok) { unlocked = true; pinError = false } else pinError = true
                }
            }
        )
        AppState.HOME -> HomePlaceholder(
            userName = userName,
            darkTheme = darkTheme,
            onToggleTheme = { viewModel.toggleTheme(it) },
            onLogout = { unlocked = false; viewModel.logout() }
        )
    }
}
