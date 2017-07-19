package com.lapismc.minecraft.versioning

/**
 * Information needed for the launcher to start the game.
 * @param mainClass Java class used as the entry-point.
 * @param minimumVersion Minimum required version needed to understand the information.
 * @param arguments Command-line arguments to pass into the game.
 *  Strings with the format: ${VAR} should be replaced with the variable's value.
 */
data class LauncherInfo(val mainClass: String, val minimumVersion: Int, val arguments: List<String>)