package com.gncal.app

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gncal.app.data.ColorPreset
import com.gncal.app.data.FontSizeMode
import com.gncal.app.data.LanguageMode
import com.gncal.app.data.ThemeMode
import com.gncal.app.data.UserPreferencesRepository
import com.gncal.app.data.VisualStyle
import com.gncal.app.ui.GnCalApp
import com.gncal.app.ui.theme.GnCalTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var preferences: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        preferences = UserPreferencesRepository(this)

        setContent {
            val themeMode by preferences.themeMode.collectAsStateWithLifecycle(ThemeMode.SYSTEM)
            val dynamicColor by preferences.dynamicColor.collectAsStateWithLifecycle(true)
            val colorPreset by preferences.colorPreset.collectAsStateWithLifecycle(ColorPreset.AMBER)
            val fontSize by preferences.fontSize.collectAsStateWithLifecycle(FontSizeMode.SYSTEM)
            val visualStyle by preferences.visualStyle.collectAsStateWithLifecycle(VisualStyle.STANDARD)
            val language by preferences.language.collectAsStateWithLifecycle(LanguageMode.SYSTEM)

            GnCalTheme(
                themeMode = themeMode,
                dynamicColor = dynamicColor,
                colorPreset = colorPreset,
                fontSize = fontSize,
                visualStyle = visualStyle,
                language = language
            ) {
                GnCalApp(preferences = preferences)
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = UserPreferencesRepository(newBase)
        val language = runBlocking { prefs.language.first() }
        val locale = when (language) {
            LanguageMode.SYSTEM -> Locale.getDefault()
            LanguageMode.CHINESE -> Locale.CHINESE
            LanguageMode.ENGLISH -> Locale.ENGLISH
        }
        
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        
        val context = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            newBase.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            newBase.resources.updateConfiguration(config, newBase.resources.displayMetrics)
            newBase
        }
        
        super.attachBaseContext(context)
    }
}
