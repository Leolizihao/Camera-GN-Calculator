package com.gncal.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gncal.app.model.DistanceUnit
import com.gncal.app.model.FlashProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

enum class ThemeMode { SYSTEM, LIGHT, DARK }

/** 语言模式。枚举顺序即 DataStore 存储值，新增项只能追加，不可插入中间 */
enum class LanguageMode { SYSTEM, CHINESE, ENGLISH, TRADITIONAL_CHINESE }

enum class FontSizeMode { SYSTEM, COMPACT, LARGE }

/** 配色方案。新增值必须追加在末尾，避免改变已存储用户的序号。 */
enum class ColorPreset { AMBER, OCEAN, MINT, ROSE, FOREST, VIOLET, CORAL, SLATE }

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "gncal_settings")

/** 用户偏好（外观、语言、文字大小与单位），使用 Jetpack DataStore 持久化 */
class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME_MODE = intPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val DISTANCE_UNIT = intPreferencesKey("distance_unit")
        val LAST_MODE = intPreferencesKey("last_mode")
        val LANGUAGE = intPreferencesKey("language")
        val FONT_SIZE = intPreferencesKey("font_size")
        val COLOR_PRESET = intPreferencesKey("color_preset")
        val HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        val HAPTIC_INTENSITY = intPreferencesKey("haptic_intensity")
        val FLASH_PROFILES = stringPreferencesKey("flash_profiles")
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        ThemeMode.entries.getOrElse(prefs[Keys.THEME_MODE] ?: 0) { ThemeMode.SYSTEM }
    }

    val dynamicColor: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.DYNAMIC_COLOR] ?: true
    }

    val distanceUnit: Flow<DistanceUnit> = context.dataStore.data.map { prefs ->
        if (prefs[Keys.DISTANCE_UNIT] == 1) DistanceUnit.FEET else DistanceUnit.METER
    }

    val lastMode: Flow<Int> = context.dataStore.data.map { prefs -> prefs[Keys.LAST_MODE] ?: 0 }

    val language: Flow<LanguageMode> = context.dataStore.data.map { prefs ->
        LanguageMode.entries.getOrElse(prefs[Keys.LANGUAGE] ?: 0) { LanguageMode.SYSTEM }
    }

    val fontSize: Flow<FontSizeMode> = context.dataStore.data.map { prefs ->
        FontSizeMode.entries.getOrElse(prefs[Keys.FONT_SIZE] ?: 0) { FontSizeMode.SYSTEM }
    }

    val colorPreset: Flow<ColorPreset> = context.dataStore.data.map { prefs ->
        ColorPreset.entries.getOrElse(prefs[Keys.COLOR_PRESET] ?: 0) { ColorPreset.AMBER }
    }

    val hapticEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.HAPTIC_ENABLED] ?: true
    }

    val hapticIntensity: Flow<Int> = context.dataStore.data.map { prefs ->
        (prefs[Keys.HAPTIC_INTENSITY] ?: 55).coerceIn(0, 100)
    }

    val flashProfiles: Flow<List<FlashProfile>> = context.dataStore.data.map { prefs ->
        decodeFlashProfiles(prefs[Keys.FLASH_PROFILES])
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.ordinal }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DYNAMIC_COLOR] = enabled }
    }

    suspend fun setDistanceUnit(unit: DistanceUnit) {
        context.dataStore.edit { it[Keys.DISTANCE_UNIT] = unit.ordinal }
    }

    suspend fun setLastMode(ordinal: Int) {
        context.dataStore.edit { it[Keys.LAST_MODE] = ordinal }
    }

    suspend fun setLanguage(language: LanguageMode) {
        context.dataStore.edit { it[Keys.LANGUAGE] = language.ordinal }
    }

    suspend fun setFontSize(fontSize: FontSizeMode) {
        context.dataStore.edit { it[Keys.FONT_SIZE] = fontSize.ordinal }
    }

    suspend fun setColorPreset(preset: ColorPreset) {
        context.dataStore.edit { it[Keys.COLOR_PRESET] = preset.ordinal }
    }

    suspend fun setHapticEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.HAPTIC_ENABLED] = enabled }
    }

    suspend fun setHapticIntensity(intensity: Int) {
        context.dataStore.edit { it[Keys.HAPTIC_INTENSITY] = intensity.coerceIn(0, 100) }
    }

    suspend fun addFlashProfile(name: String, guideNumber: Double) {
        val cleanName = name.trim()
        if (cleanName.isEmpty() || guideNumber <= 0.0) return
        context.dataStore.edit { prefs ->
            val profiles = decodeFlashProfiles(prefs[Keys.FLASH_PROFILES])
            prefs[Keys.FLASH_PROFILES] = encodeFlashProfiles(
                profiles + FlashProfile(UUID.randomUUID().toString(), cleanName, guideNumber)
            )
        }
    }

    suspend fun updateFlashProfile(profile: FlashProfile) {
        val cleanName = profile.name.trim()
        if (cleanName.isEmpty() || profile.guideNumber <= 0.0) return
        context.dataStore.edit { prefs ->
            val profiles = decodeFlashProfiles(prefs[Keys.FLASH_PROFILES])
            prefs[Keys.FLASH_PROFILES] = encodeFlashProfiles(
                profiles.map { existing ->
                    if (existing.id == profile.id) {
                        profile.copy(name = cleanName)
                    } else {
                        existing
                    }
                }
            )
        }
    }

    suspend fun deleteFlashProfile(profileId: String) {
        context.dataStore.edit { prefs ->
            val profiles = decodeFlashProfiles(prefs[Keys.FLASH_PROFILES])
            prefs[Keys.FLASH_PROFILES] = encodeFlashProfiles(profiles.filterNot { it.id == profileId })
        }
    }
}

private fun decodeFlashProfiles(raw: String?): List<FlashProfile> {
    if (raw == null) return FlashProfile.defaults
    return runCatching {
        val array = JSONArray(raw)
        buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                val id = item.optString("id")
                val name = item.optString("name").trim()
                val guideNumber = item.optDouble("gn", Double.NaN)
                if (id.isNotEmpty() && name.isNotEmpty() && guideNumber > 0.0) {
                    add(FlashProfile(id, name, guideNumber))
                }
            }
        }
    }.getOrElse { FlashProfile.defaults }
}

private fun encodeFlashProfiles(profiles: List<FlashProfile>): String =
    JSONArray().apply {
        profiles.forEach { profile ->
            put(
                JSONObject()
                    .put("id", profile.id)
                    .put("name", profile.name)
                    .put("gn", profile.guideNumber)
            )
        }
    }.toString()
