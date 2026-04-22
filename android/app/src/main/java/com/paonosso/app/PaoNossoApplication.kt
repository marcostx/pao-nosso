package com.paonosso.app

import android.app.Application
import com.paonosso.app.data.AppContainer

class PaoNossoApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        instance = this
    }

    companion object {
        @Volatile private var instance: PaoNossoApplication? = null
        fun container(): AppContainer = instance!!.container
    }
}
