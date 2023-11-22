package ai.visionlabs.examples.camera

import android.app.Application
import android.util.Log
import ru.visionlabs.sdk.lunacore.CloseCameraCommand
import ru.visionlabs.sdk.lunacore.Commands
import ru.visionlabs.sdk.lunacore.LunaConfig
import ru.visionlabs.sdk.lunacore.LunaCoreConfig
import ru.visionlabs.sdk.lunacore.LunaID
import ru.visionlabs.sdk.lunacore.StartBestShotSearchCommand

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
        )

        if (LunaID.activateLicense(applicationContext)) {
            LunaID.init(
                app = this@App,
                lunaConfig = lunaConfig,
            )
        } else {
            Log.e("@@@@", "activation failed")
        }
    }
}