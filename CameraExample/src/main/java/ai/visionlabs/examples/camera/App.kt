package ai.visionlabs.examples.camera

import android.app.Application
import ru.visionlabs.sdk.lunacore.LunaConfig
import ru.visionlabs.sdk.lunacore.LunaCoreConfig
import ru.visionlabs.sdk.lunacore.LunaID

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        LunaID.init(
            app = this@App,
            lunaConfig = LunaConfig.create(),
            areDescriptorsEnabled = true,
        )
    }
}