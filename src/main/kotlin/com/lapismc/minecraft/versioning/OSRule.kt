package com.lapismc.minecraft.versioning

/**
 * Library inclusion rule for operating system checks.
 * @param os Operating system this rule applies to.
 * @param allowed Flag indicating whether the library should be included.
 */
class OSRule(val os: OSType, allowed: Boolean) : Rule(allowed) {
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
    }

    /**
     * Checks whether this rule applies to the current system.
     * @return True if the rule should be considered, false otherwise.
     */
    override fun isApplicable(): Boolean = when(os) {
        OSType.WINDOWS -> isWindows()
        OSType.OSX     -> isOSX()
        OSType.LINUX   -> isLinux()
    }
}