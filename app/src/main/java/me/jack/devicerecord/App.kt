package me.jack.devicerecord

import android.app.Application
import me.jack.kotlin.library.extension.DelegatesExtensions

/**
 * Created by Jack on 2017/12/1.
 */
class App : Application() {

    companion object {
        var instance by DelegatesExtensions.notNullSingleValue<App>()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}