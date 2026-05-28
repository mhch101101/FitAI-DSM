package app.dsm.fitai.di

import android.app.Application

class FitAIApp: Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.create()
    }
}