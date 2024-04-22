package ai.visionlabs.examples.camera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ai.visionlabs.examples.camera.ui.main.MainFragment
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.visionlabs.sdk.lunacore.LunaID

class MainActivity : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

        LunaID.engineInitStatus
            .flowWithLifecycle(this.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                when (it) {
                    is LunaID.EngineInitStatus.InProgress -> {
                        Log.d("EngineInitStatus", "Engine init in progress")
                    }
                    is LunaID.EngineInitStatus.Success -> {
                        Log.d("EngineInitStatus", "Engine initialized successfully")
                    }

                    is LunaID.EngineInitStatus.Failure -> {
                        Log.e("EngineInitStatus", it.cause?.message ?: "Unknown error")
                        Log.e("EngineInitStatus", it.cause?.stackTraceToString() ?: "No stackTrace")
                        finish()
                    }

                    else -> throw IllegalStateException("Unknown engine init status")
                }
            }.flowOn(Dispatchers.Main)
            .launchIn(this.lifecycleScope)
    }
}