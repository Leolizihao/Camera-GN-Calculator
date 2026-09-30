# GN Cal v1.2.0

## Custom flash profiles

- Added a persistent flash profile list with custom names and GN values.
- Added quick profile switching above the GN input on the home calculator screen.
- Added default profiles for GN 24, GN 36, and GN 60.
- Added Settings management for adding, editing, and deleting profiles.
- Profile data is stored locally through the existing Android DataStore setup.
- Selecting a profile only fills the existing GN input; calculation formulas and targets remain unchanged.

## Verification

- Debug build successful.
- Existing calculator unit tests pass.
- Release APK is signed and verified with APK Signature Scheme v2.
