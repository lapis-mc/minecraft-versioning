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
    LINUX;

    companion object {
        /**
         * Name of the current operating system.
         */
        private val localOSName = System.getProperty("os.name").toLowerCase()

        /**
         * Checks whether the local system appears to be Windows.
         * @return True if the local OS is Windows, false otherwise.
         */
        fun isWindows() = localOSName.indexOf("win") >= 0

        /**
         * Checks whether the local system appears to be Mac OSX.
         * @return True if the local OS is Mac OSX, false otherwise.
         */
        fun isOSX() = localOSName.indexOf("mac") >= 0

        /**
         * Checks whether the local system appears to be Linux.
         * @return True if the local OS is Linux, false otherwise.
         */
        fun isLinux() = localOSName.indexOf("linux") >= 0

        /**
         * Gets the enumeration element representative of the current operating system.
         * @return Element representing the current OS.
         */
        val current = when {
            isWindows() -> WINDOWS
            isOSX()     -> OSX
            isLinux()   -> LINUX
            else        -> TODO()
        }
    }
}