# GN Cal v1.1.0 Release Notes

## 📦 Build Information
- **Version:** 1.1.0 (versionCode: 2)
- **Package:** com.gncal.app
- **Size:** 7.71 MB
- **Target SDK:** Android 16 (API 36)
- **Min SDK:** Android 8.0 (API 26)
- **SHA-256:** `98443F3E756114F34B64D07FA1659FF4D29A1394E81725D4A7FEBB7FC1B948B4`

## ✨ New Features

### 🎯 Quick Mode Switching
- **Bottom Navigation Bar** with four calculation targets (Aperture, Distance, GN, ISO)
- Instant switching between modes without navigating through menus
- Clear visual indicators for the active mode

### ⚙️ Comprehensive Settings Page
- Dedicated settings screen accessible from bottom navigation
- All preferences organized in categorized sections
- Persistent configuration via DataStore

### 🌐 Language Options
- **System:** Follow device language (default)
- **简体中文:** Simplified Chinese
- **English:** English
- Dynamic locale switching without app restart

### 📐 Font Size Control
- **System:** Follow device font scale (default)
- **Compact:** 90% scale for information-dense layouts
- **Large:** 115% scale for improved readability
- Scales entire Material 3 typography ramp uniformly

### 🎨 Visual Style Effects (Lightweight)
- **Standard:** Clean Material 3 design (default)
- **Frosted Glass:** Semi-transparent surfaces with subtle gradients
- **Liquid Glass:** Radial gradients with shimmer borders
- GPU-friendly effects using composited layers, not real-time blur

### 🎨 Color Presets (when Dynamic Color is off)
- **Amber:** Warm studio-light inspired palette (default)
- **Ocean:** Cool blue nautical tones
- **Mint:** Fresh green natural tones
- **Rose:** Soft pink elegant tones
- Full light/dark mode support for each preset

## 🔧 Technical Improvements
- Refactored theme system with `LocalVisualStyle` composition local
- Typography scaling system without additional font assets
- Locale configuration via `attachBaseContext` for immediate language switching
- Visual effects use `Brush` and `BorderStroke` for minimal overhead
- All calculation logic preserved and verified (8/8 unit tests passing)

## 📂 New Files
```
app/src/main/java/com/gncal/app/
├─ ui/settings/SettingsScreen.kt       # Settings UI with categorized options
├─ ui/theme/ColorPresets.kt            # 4 preset color schemes (light/dark)
├─ ui/theme/VisualEffects.kt           # Frosted/liquid glass modifiers
└─ data/UserPreferences.kt             # Extended with 4 new preference flows
```

## 🔄 Modified Components
- `GnCalApp.kt`: Bottom navigation + destination routing
- `MainActivity.kt`: Locale configuration + new preference collection
- `Theme.kt`: Font scaling + preset selection + visual style provider
- `Type.kt`: Added `Typography.scaled()` extension
- `Components.kt`: Visual style integration for `SectionCard`
- Localized strings for settings in Chinese and English

## ✅ Verification
- `:app:assembleDebug` ✓
- `:app:testDebugUnitTest` ✓ (8/8 tests passing)
- `:app:assembleRelease` ✓
- APK signature verification ✓ (v2 scheme)

## 📥 Installation
```powershell
adb install -r dist\GNCal-v1.1.0-release.apk
```

## 🔗 Backward Compatibility
- Upgrades from v1.0.0 preserve distance unit and theme preferences
- New preferences use sensible defaults (System language, System font size, Standard visual style, Amber preset)
- No changes to core calculation formulas or data models
