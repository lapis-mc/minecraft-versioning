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

    companion object {
        /**
         * Extracts GAV information from a formatted string.
         * @param gav String containing the GAV information.
         *  This format should be GROUP:ARTIFACT:VERSION
         * @return Extract GAV information.
         */
        fun parse(gav: String): GroupArtifactVersionId {
            val (groupId, artifactId, version) = gav.split(':', limit = 3)
            return GroupArtifactVersionId(groupId, artifactId, version)
        }
    }
}