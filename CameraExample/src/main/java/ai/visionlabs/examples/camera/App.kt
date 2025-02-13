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
            usePrimaryFaceTracking = false,
            livenessType = LivenessType.None,
            minFaceSideToMinScreenSide = 0.3f
//            minFaceSideToMinScreenSide = 0.25f
//            foundFaceDelay = 700L,
//            skipFrames = 50,
//            interactionDelayMs = 200L,
//            glassesChecks = setOf(GlassesCheckType.GLASSES_CHECK_SUN),
        )
    }

    override fun onCreate() {
        super.onCreate()
        LunaID.initEngine(this, lunaConfig)
    }
}