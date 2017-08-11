package com.lapismc.minecraft.versioning

/**
 * Library inclusion rule for operating system checks.
 * @param os Operating system this rule applies to.
 * @param versionRegex Regular expression used to determine whether this rule applies to the OS version.
 * @param allowed Flag indicating whether the library should be included.
 */
class OSRule(val os: OSType, val versionRegex: Regex, allowed: Boolean) : Rule(allowed) {
    /**
     * Creates an OS rule that ignores the version.
     * @param os Operating system this rule applies to.
     * @param allowed Flag indicating whether the library should be included.
     */
    constructor(os: OSType, allowed: Boolean) : this(os, Regex(""), allowed)

    companion object {
        private val currentOSVersion = System.getProperty("os.version")
    }

    /**
     * Checks whether this rule applies to the current system.
     * @return True if the rule should be considered, false otherwise.
     */
    override fun isApplicable(): Boolean = OSType.current == os && versionRegex.matches(currentOSVersion)

    /**
     * Creates a string representation of the rule.
     * @return Rule as a string.
     */
    override fun toString(): String {
        val allowedStr = if(allowed) "Allowed" else "Denied"
        return "Rule($allowedStr on $os)"
    }
}