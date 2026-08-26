package com.khata.finance

import android.app.Application
import com.khata.finance.data.SettingsStore

class KhataApp : Application() {
    lateinit var settings: SettingsStore
        private set

    override fun onCreate() {
        super.onCreate()
        settings = SettingsStore(this)
    }
}
