package com.lapismc.minecraft.versioning

/**
 * Different types of versions available.
 */
enum class VersionType {
    /**
     * Functional release.
     * Should be the most stable and have fewer bugs.
     */
    RELEASE,

    /**
     * In-progress development version.
     * Can be buggy and unstable, but shows off new features.
     */
    SNAPSHOT,

    /**
     * Old beta versions before the game became "1.0".
     */
    BETA,

    /**
     * Really old versions of the game when it was in early development.
     */
    ALPHA;

    companion object {
        /**
         * Converts a string provided by a meta-service
         * to an element in the enum.
         * @param str Version type represented as a string.
         * @return Enumeration element corresponding to the string.
         */
        fun fromString(str: String) = when(str.toLowerCase()) {
            "release"   -> RELEASE
            "snapshot"  -> SNAPSHOT
            "old_beta"  -> BETA
            "old_alpha" -> ALPHA
            else        -> TODO("Don't know how to handle unknown version string")
        }
    }
}