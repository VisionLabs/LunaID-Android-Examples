package ai.visionlabs.examples.platformapi

import android.app.Application
import ru.visionlabs.sdk.lunaweb.v6.ApiHuman
import ru.visionlabs.sdk.lunaweb.v6.ApiHumanConfig

class App : Application() {

    companion object {
        @JvmStatic
        public lateinit var platformApi: ApiHuman
    }

    override fun onCreate() {
        super.onCreate()

        val baseUrl = "http://luna-platform.com/api/6/"

        // will be added to every request
        val constantHeaders = mapOf(
            "Luna-Account-Id" to "12ed7399-f779-479c-8258-bbc45e6017af"
        )

        val apiConfig = ApiHumanConfig(
            baseUrl = baseUrl,
            headers = constantHeaders,
        )

        platformApi = ApiHuman(apiConfig)
    }
}