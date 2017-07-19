package com.lapismc.minecraft.versioning

/**
 * Maven GAV information.
 * @param groupId Artifact group ID.
 * @param artifactId Artifact name.
 * @param version Artifact version.
 */
data class GroupArtifactVersionId(val groupId: String, val artifactId: String, val version: String) {
    /**
     * Constructs a string representing the GAV.
     * @return Group, artifact, and version separated by colons.
     */
    override fun toString(): String = "$groupId:$artifactId:$version"
}