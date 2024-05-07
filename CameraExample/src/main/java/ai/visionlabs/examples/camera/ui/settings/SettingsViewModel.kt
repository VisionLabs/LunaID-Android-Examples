package ai.visionlabs.examples.camera.ui.settings

import ai.visionlabs.examples.camera.ui.Settings
import ai.visionlabs.examples.camera.ui.main.MainViewState
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.visionlabs.sdk.lunacore.LunaID

class SettingsViewModel : ViewModel() {

}