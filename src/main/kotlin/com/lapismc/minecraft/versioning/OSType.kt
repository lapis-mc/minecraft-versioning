package com.lapismc.minecraft.versioning

/**
 * Distinct operating system types the game is aware of.
 */
enum class OSType {
    /**
     * All Microsoft Windows operating systems.
     */
    WINDOWS,

    /**
     * All Apple OSX operating systems.
     */
    OSX,

    /**
     * Anything not Windows or OSX.
     */
    LINUX
}