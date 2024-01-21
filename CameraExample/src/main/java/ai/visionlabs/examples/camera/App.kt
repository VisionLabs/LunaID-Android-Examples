package ai.visionlabs.examples.camera

import android.app.Application
import android.util.Log
import ru.visionlabs.sdk.lunacore.LunaConfig
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
            foundFaceDelay = 1_000L,
            detectFrameSize = 100,

            skipFrames = 0,
            detectorStep = 0,

            interactionDelayMs = 2_000L,
            acceptEyesClosed = true,
            acceptGlasses = false,
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