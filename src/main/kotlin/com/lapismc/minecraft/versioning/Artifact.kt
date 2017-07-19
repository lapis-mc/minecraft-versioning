package com.lapismc.minecraft.versioning

/**
 * File associated with a library needed for runtime.
 * @param path Path to where the artifact should be stored locally.
 * @param resource Information for retrieving and using the artifact.
 */
data class Artifact(val path: String, val resource: Resource)