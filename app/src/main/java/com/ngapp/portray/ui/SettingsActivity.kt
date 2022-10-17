package com.ngapp.portray.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngapp.portray.R
import com.ngapp.portray.databinding.ActivitySettingsBinding
import com.ngapp.portray.ui.about.AboutFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener,
    Preference.SummaryProvider<ListPreference> {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences?,
        key: String?
    ) {
        val darkModeString = getString(R.string.theme_mode)
        key?.let {
            if (it == darkModeString) sharedPreferences?.let { pref ->
                val darkModeValues = resources.getStringArray(R.array.theme_values)
                when (pref.getString(darkModeString, darkModeValues[0])) {
                    darkModeValues[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    darkModeValues[1] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    darkModeValues[2] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
    }


    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    override fun provideSummary(preference: ListPreference): CharSequence? {
        return if (preference.key == getString(R.string.theme_mode)) preference.entry
        else "Unknown Preference"
    }
}