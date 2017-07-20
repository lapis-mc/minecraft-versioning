package com.lapismc.minecraft.versioning

/**
 * Library inclusion rule for operating system checks.
 * @param os Operating system this rule applies to.
 * @param allowed Flag indicating whether the library should be included.
 */
class OSRule(val os: OSType, allowed: Boolean) : Rule(allowed) {
    /**
     * Checks whether this rule applies to the current system.
     * @return True if the rule should be considered, false otherwise.
     */
    override fun isApplicable(): Boolean = OSType.current == os

    /**
     * Creates a string representation of the rule.
     * @return Rule as a string.
     */
    override fun toString(): String {
        val allowedStr = if(allowed) "Allowed" else "Denied"
        return "Rule($allowedStr on $os)"
    }
}