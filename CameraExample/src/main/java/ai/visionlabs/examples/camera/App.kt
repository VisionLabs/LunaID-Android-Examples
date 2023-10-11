package ai.visionlabs.examples.camera

import android.app.Application
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
        )
        LunaID.init(
            app = this@App,
            lunaConfig = lunaConfig
        )
    }
}