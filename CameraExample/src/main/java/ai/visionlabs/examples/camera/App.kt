package ai.visionlabs.examples.camera

import android.app.Application
import android.util.Log
import ru.visionlabs.sdk.lunacore.LunaConfig
import ru.visionlabs.sdk.lunacore.LunaCoreConfig
import ru.visionlabs.sdk.lunacore.LunaID

class App : Application() {

    companion object {
        lateinit var lunaConfig: LunaConfig
    }

    override fun onCreate() {
        super.onCreate()

        lunaConfig = LunaConfig.create(
            borderDistanceLeft = 100,
            borderDistanceTop = 40,
            borderDistanceRight = 20,
            borderDistanceBottom = 400,
            acceptOccludedFaces = false,
            acceptOneEyed = false,
            startDelayMs = 1_000,
            finishDelayMs = 1_000,
        )

        if (LunaID.activateLicense(applicationContext)) {
            LunaID.init(
                app = this@App,
                lunaConfig = lunaConfig
            )
        } else {
            Log.e("@@@@", "activation failed")
        }
    }
}