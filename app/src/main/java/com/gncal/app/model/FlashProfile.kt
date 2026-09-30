package com.gncal.app.model

/** A named flash profile whose guide number is used as the calculator GN input. */
data class FlashProfile(
    val id: String,
    val name: String,
    val guideNumber: Double
) {
    companion object {
        /** Useful starting profiles; users can rename, edit, or remove them in Settings. */
        val defaults: List<FlashProfile> = listOf(
            FlashProfile("default-gn-24", "GN 24", 24.0),
            FlashProfile("default-gn-36", "GN 36", 36.0),
            FlashProfile("default-gn-60", "GN 60", 60.0)
        )
    }
}
