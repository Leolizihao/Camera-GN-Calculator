# GN Cal v1.3.0

## Real glass visual styles

- Replaced the previous translucent-gradient fake glass with a **real backdrop blur engine**
  (`ui/theme/GlassEffects.kt`).
- Frosted Glass: 22 dp background blur, frosted gradient tint, hairline border, no highlight —
  tuned for maximum readability.
- Liquid Glass: 30 dp background blur, saturation boost (1.35), specular highlight,
  refraction border, inner shadow and 3 dp elevation — a thick, refractive liquid surface.
- Backdrop sampling redraws the app background inside each panel, offset by the panel's window
  position; only a `panel + 2 × blur radius` region is sampled to keep the cost low.
- API 31+ uses `RenderEffect` (via `Modifier.blur`); older Android versions automatically fall
  back to translucent glass without crashing.
- Applied to: result card, section cards, hint cards, formula rows and the top app bar.
- Removed the obsolete `VisualEffects.kt`; `VisualStyle` now maps to `GlassSpec` parameter sets.

## Install robustness

- Enabled **v1 / v2 / v3 / v4** APK signature schemes (previously v2 only); v4 produces the
  `.idsig` file required by `adb install --incremental`.
- Removed the unused `VIBRATE` permission — the app now requests **zero permissions**.
- Added an Android App Bundle with **code transparency**:
  - dedicated 3072-bit transparency key (separate from the app signing key, as required);
  - `bundletool add-transparency` → re-signed with the app signing key →
    `bundletool check-transparency` verifies successfully;
  - the public transparency certificate (`transparency.cert`) is published for independent audit.
  - Note: per Google's documentation, Android does **not** verify code transparency at install
    time; it exists for developer/end-user integrity auditing.
- Added `docs/install-compatibility.md` documenting the signing matrix, SDK policy,
  risk heuristics avoided and the Android 16 Advanced Protection caveat.

## Docs

- Added `docs/glass-design.md` (design rationale, parameter table, implementation notes,
  fallback and performance strategy).
- Updated `docs/index.md` for v1.3.0: badges, download table, SHA-256, structure, docs links.

## Verification

- `./gradlew :app:assembleRelease` — successful (R8 minified).
- `./gradlew :app:bundleRelease` — successful.
- `./gradlew :app:testDebugUnitTest` — 8/8 passing.
- `apksigner verify` — v2 true, v3 true, v4 `.idsig` generated.
- `bundletool check-transparency` — "Code transparency verified".
