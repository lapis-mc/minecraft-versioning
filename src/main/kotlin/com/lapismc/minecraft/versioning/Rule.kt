package com.lapismc.minecraft.versioning

/**
 * Basic library inclusion rule.
 * @param allowed Flag indicating whether the library should be included.
 */
open class Rule(val allowed: Boolean) {
    /**
     * Checks whether this rule applies to the current system.
     * @return True if the rule should be considered, false otherwise.
     */
    open fun isApplicable() = true

    /**
     * Creates a string representation of the rule.
     * @return Rule as a string.
     */
    override fun toString(): String {
        val allowedStr = if(allowed) "Allowed" else "Denied"
        return "Rule($allowedStr)"
    }
}