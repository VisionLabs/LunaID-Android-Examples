package ai.visionlabs.examples.camera

import android.app.Application
import ru.visionlabs.sdk.lunacore.LunaConfig
import ru.visionlabs.sdk.lunacore.LunaID
import ru.visionlabs.sdk.lunacore.V60
import ru.visionlabs.sdk.lunacore.V65
import ru.visionlabs.sdk.lunacore.liveness.GlassesCheckType
import ru.visionlabs.sdk.lunacore.liveness.LivenessType

class App : Application() {

    companion object {
        val lunaConfig = LunaConfig.create(
            ags = 0.5F,
            minFaceSize = 50,
            headPitch = 25F,
            headYaw = 25F,
            headRoll = 25F,
            strictlyMinSize = true,
            acceptOneEyed = false,
            bestShotsCount = 1,
            foundFaceDelay = 500L,
            acceptEyesClosed = false,
            acceptMask = false,
            acceptOccludedFaces = false,
            usePrimaryFaceTracking = true,
            eyesAggregationEnabled = true,
            glassesAggregationEnabled = true,
            livenessType = LivenessType.None,
            useDescriptors = true,
            faceOcclusionAggregationEnabled = true,
            glassesChecks = setOf(GlassesCheckType.GLASSES_CHECK_SUN)
        )
    }

    override fun onCreate() {
        super.onCreate()
        LunaID.initEngine(this, lunaConfig)
    }
}