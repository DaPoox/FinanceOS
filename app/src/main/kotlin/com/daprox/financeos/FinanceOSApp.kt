package com.daprox.financeos

import android.app.Application
import com.daprox.financeos.data.dataModule
import com.daprox.financeos.di.appModule
import com.daprox.financeos.presentation.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/** Application entry point. Initializes Koin dependency injection with all feature modules. */
class FinanceOSApp : Application() {
  override fun onCreate() {
    super.onCreate()
    startKoin {
      androidContext(this@FinanceOSApp)
      modules(
        appModule,
        dataModule,
        presentationModule,
      )
    }
  }
}
