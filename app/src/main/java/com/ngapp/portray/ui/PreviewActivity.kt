package com.ngapp.portray.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.ngapp.portray.R
import com.ngapp.portray.databinding.ActivityPreviewBinding
import com.ngapp.portray.utils.haveR

class PreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val darkModeValues = resources.getStringArray(R.array.theme_values)

        when (PreferenceManager.getDefaultSharedPreferences(this)
            .getString(getString(R.string.theme_mode), getString(R.string.theme_default_value))) {
            darkModeValues[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            darkModeValues[1] -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                window.statusBarColor = ContextCompat.getColor(this, R.color.white)
            }
            darkModeValues[2] -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                window.statusBarColor = ContextCompat.getColor(this, R.color.black)
            }
        }

        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                startActivity(Intent(this@PreviewActivity, MainActivity::class.java))
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        val decorView: View = this.window.decorView
        if (haveR()) {
            window.setDecorFitsSystemWindows(false)
        } else {
            val uiOptions = decorView.systemUiVisibility
            var newUiOptions = uiOptions
            newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_LOW_PROFILE
            newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_FULLSCREEN
            newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE
            newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = newUiOptions
        }
    }

    override fun onResume() {
        super.onResume()
        binding.root.startLayoutAnimation()
    }

}