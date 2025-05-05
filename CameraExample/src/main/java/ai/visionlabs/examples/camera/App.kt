package ai.visionlabs.examples.camera

import android.app.Application
import android.graphics.Bitmap
import ru.visionlabs.sdk.lunacore.LunaConfig
import ru.visionlabs.sdk.lunacore.LunaID
import ru.visionlabs.sdk.lunacore.LunaVideoQuality
import ru.visionlabs.sdk.lunacore.liveness.GlassesCheckType
import ru.visionlabs.sdk.lunacore.liveness.LivenessType

class App : Application() {

    companion object {
        val lunaConfig = LunaConfig.create(
            acceptOccludedFaces = true,
            acceptOccludedMouth = false,
            acceptOneEyed = false,
            acceptOneEyeClose = false,
            usePrimaryFaceTracking = false,
            livenessType = LivenessType.Offline,
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