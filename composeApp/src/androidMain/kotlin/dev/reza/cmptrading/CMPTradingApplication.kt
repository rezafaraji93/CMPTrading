package dev.reza.cmptrading

import android.app.Application
import dev.reza.cmptrading.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent

class CMPTradingApplication: Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@CMPTradingApplication)
        }
    }

}