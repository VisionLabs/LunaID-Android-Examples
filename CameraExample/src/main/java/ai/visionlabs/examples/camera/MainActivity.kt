package ai.visionlabs.examples.camera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ai.visionlabs.examples.camera.ui.main.MainFragment
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo

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
    }
}