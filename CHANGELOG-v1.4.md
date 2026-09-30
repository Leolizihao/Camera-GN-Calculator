# GN Cal v1.4.0

## Removed the glass visual styles

- Deleted the frosted glass / liquid glass engine (`ui/theme/GlassEffects.kt`) and the
  `VisualStyle` preference entirely.
- Appearance settings now contain **color presets only**.
- Result card, section cards, hint cards, formula rows and the top app bar are back to the
  standard Material 3 surfaces (`Card` / `Surface` with `colorScheme` container colors).

## New typography: IBM Plex Sans + Source Han Serif

- **Latin letters and digits**: IBM Plex Sans.
- **Chinese**: Source Han Serif / Noto Serif SC (思源宋体), instanced from the variable font.
- The two typefaces are **merged into one font file per weight** (`.toolchain/merge_font.py`).
  Keeping them as separate `FontFamily` entries is not reliable: Compose resolves missing glyphs
  by walking the family in list order on most devices, so Chinese would always land on a single
  weight and the bold/thin distinction would be lost.
- Character set is subsetted to what the app actually renders (518 Chinese + 304 Latin/symbol
  glyphs) → ~214 KB per weight, ~0.9 MB total.
- Weight mapping: **大标题 / 结果数字 = Bold (700)**, **小标题 = Light (300)**,
  正文 = Regular (400), 按钮与标签 = Medium (500).

## Language

- Added **繁體中文 (Traditional Chinese)** — `values-zh-rTW/strings.xml`, `LanguageMode.TRADITIONAL_CHINESE`.
- The new enum value is appended so existing users' stored language ordinals stay valid.
- Locale is applied both at startup (`attachBaseContext`) and in the theme's localized context.

## Other

- Removed the unused `VIBRATE` permission carried over from v1.3.0 work (manifest has zero permissions).
- Removed the obsolete `visual_*` / `settings_visual_style` strings (zh + en).
- Deleted `docs/glass-design.md`; `docs/index.md` updated.

## Verification

- `./gradlew :app:assembleRelease` — successful (7.90 MB, R8 minified, signing v2 + v3).
- `./gradlew :app:bundleRelease` — successful; `bundletool check-transparency` verifies.
- Font files validated: valid sfnt TTF, both Latin (`A`) and Chinese (`光`) glyphs present in all
  four weights.

```
APK SHA-256: A71C6E2786DA620004653B8C4103AF488D3AD4441B90803BEF3A33E09AAA356E
```
