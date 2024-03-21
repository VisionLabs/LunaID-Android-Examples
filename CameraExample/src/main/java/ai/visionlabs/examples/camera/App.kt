package ai.visionlabs.examples.camera

import android.app.Application
import android.util.Log
import ru.visionlabs.sdk.lunacore.LunaConfig
import ru.visionlabs.sdk.lunacore.LunaID
import ru.visionlabs.sdk.lunacore.liveness.GlassesCheckType

class App : Application() {

    companion object {
        lateinit var lunaConfig: LunaConfig
    }

    override fun onCreate() {
        super.onCreate()

        lunaConfig = LunaConfig.create(
            acceptOccludedFaces = false,
            acceptOneEyed = false,
            foundFaceDelay = 700L,
            detectFrameSize = 350,

            skipFrames = 50,
            detectorStep = 1,

            interactionDelayMs = 200L,
            acceptEyesClosed = false,

            glassesChecks = setOf(GlassesCheckType.GLASSES_CHECK_SUN)
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