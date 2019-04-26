package at.willhaben.multiscreenflow

import android.app.Application
import android.content.Context
import com.squareup.leakcanary.LeakCanary
import android.os.StrictMode



class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this);

        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectActivityLeaks()
                .penaltyLog()
                .build()
        )

        CONTEXT = this
    }

    companion object {
        lateinit var CONTEXT : Context
    }
}