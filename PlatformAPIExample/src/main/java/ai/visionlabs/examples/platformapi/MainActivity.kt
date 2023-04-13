package ai.visionlabs.examples.platformapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.visionlabs.sdk.lunacore.Consumer
import ru.visionlabs.sdk.lunacore.Result
import ru.visionlabs.sdk.lunaweb.v6.ApiHuman.Companion.BinaryPayload
import ru.visionlabs.sdk.lunaweb.v6.ApiHuman.Companion.EventRequest
import ru.visionlabs.sdk.lunaweb.v6.ApiHuman.Companion.VerifyRequest
import ru.visionlabs.sdk.lunaweb.v6.EventGenerateResponse
import ru.visionlabs.sdk.lunaweb.v6.VerifyResponse

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // or provide it with Hilt or Dagger
    private val api = App.platformApi

    private val eventResponseConsumer = Consumer<Result<EventGenerateResponse>> { res ->
        if (res.isFailure) {
            // failed
        } else {
            // success
            val response: EventGenerateResponse = res.get()
            response.events
            response.images
        }
    }

    private val verifyResponseConsumer =  Consumer<Result<VerifyResponse>> { res ->
        val response: VerifyResponse = res.get()

        response.images
    }

    /**
     * with this call you can create events.
     * for example to
     * = extract descriptor on server side
     * = identify person
     */
    fun createEvent() {
        // provide photo as byte array
        val photo: ByteArray = byteArrayOf()

        val request = EventRequest(
            handlerId = "34-sddf234-sdfq23",
            payload = BinaryPayload.Photo(photo),
            imageType = 1, // warp
        )

        api.apiEvents(
            query = request,
            consumer = eventResponseConsumer
        )
    }


    // =====================

    /**
     * with this call you can
     * verify person by photo or descriptor
     */
    fun verifyWithCustomHeader() {
        val photo: ByteArray = byteArrayOf()

        // one-shot headers
        val dynamicHeaders = mapOf(
            "X-Auth-Token" to "secret_token1"
        )

        val request = VerifyRequest(
            verifierId =  "sdfqw-123asdf-af123",
            payload = BinaryPayload.Photo(photo),
            extraHeaders = dynamicHeaders,
        )

        api.apiVerifiers(
            query = request,
            consumer = verifyResponseConsumer
        )

    }

    /**
     * verify person by photo or descriptor
     */
    fun verifyByDescriptor() {
        // create descriptor with LunaUtils.getDescriptor() locally
        val descriptor: ByteArray = byteArrayOf()

        val request = VerifyRequest(
            verifierId =  "sdfqw-123asdf-absd",
            payload = BinaryPayload.Descriptor(descriptor),
        )

        api.apiVerifiers(
            query = request,
            consumer = verifyResponseConsumer
        )

    }


    override fun onDestroy() {
        api.removeCallback(eventResponseConsumer)
        api.removeCallback(verifyResponseConsumer)

        super.onDestroy()
    }
}