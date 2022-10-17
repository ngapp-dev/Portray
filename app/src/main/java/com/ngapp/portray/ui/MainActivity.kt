package com.ngapp.portray.ui

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngapp.portray.R
import com.ngapp.portray.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val darkModeValues = resources.getStringArray(R.array.theme_values)

        when (PreferenceManager.getDefaultSharedPreferences(this)
            .getString(getString(R.string.theme_mode), getString(R.string.theme_default_value))) {
            darkModeValues[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            darkModeValues[1] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            darkModeValues[2] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_collections, R.id.nav_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val value = TypedValue()
            this.theme.resolveAttribute(android.R.attr.statusBarColor, value, true)

            when (destination.id) {
                R.id.nav_onboarding -> {
                    window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
                    binding.appBarMain.toolbar.visibility = View.GONE
                    binding.navView.visibility = View.GONE
                }
                R.id.nav_auth -> {
                    window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
                    binding.appBarMain.toolbar.visibility = View.GONE
                    binding.navView.visibility = View.GONE
                }
                else -> {
                    window.statusBarColor = value.data
                    binding.appBarMain.toolbar.visibility = View.VISIBLE
                    binding.navView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}