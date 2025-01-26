package ai.visionlabs.examples.camera

import android.app.Application
import ru.visionlabs.sdk.lunacore.LunaConfig
import ru.visionlabs.sdk.lunacore.LunaID
import ru.visionlabs.sdk.lunacore.liveness.LivenessType

class App : Application() {

    companion object {
        val lunaConfig = LunaConfig.create(
            acceptOccludedFaces = true,
            acceptOccludedMouth = false,
            acceptOneEyed = false,
            acceptOneEyeClose = false,
            usePrimaryFaceTracking = true,
            livenessType = LivenessType.Offline,
            detectFrameSize = 100
//            minFaceSideToMinScreenSide = 0.25f
//            foundFaceDelay = 700L,
//            skipFrames = 50,
//            interactionDelayMs = 200L,
//            glassesChecks = setOf(GlassesCheckType.GLASSES_CHECK_SUN),
        )
    }

    override fun onCreate() {
        super.onCreate()
        LunaID.activateLicense(this, lunaConfig)
    }
}