package com.lapismc.minecraft.versioning

/**
 * Package required for the game to run
 * and additional information regarding extraction and inclusion rules.
 * @param gav Maven information for the downloadable artifacts.
 * @param artifacts Set of artifacts included in the library.
 * @param exclusions Paths to exclude when extracting the library.
 * @param rules Rules to check whether the library should be included.
 */
class Library(val gav: GroupArtifactVersionId, val artifacts: List<Artifact>,
              val exclusions: List<String>, val rules: List<Rule>, val natives: Map<OSType, String>) {
    /**
     * Creates a string representation of the library.
     * @return GAV information.
     */
    override fun toString(): String {
        return "Library($gav)"
    }

    /**
     * Helps construct a library.
     * @param gav Maven information for the downloadable artifacts.
     */
    class Builder(val gav: GroupArtifactVersionId) {
        private val artifacts  = ArrayList<Artifact>()
        private val exclusions = ArrayList<String>()
        private val rules      = ArrayList<Rule>()
        private val natives    = HashMap<OSType, String>()

        /**
         * Adds an artifact that is part of the library.
         * @param artifact Artifact to add.
         * @return Builder for chaining methods.
         */
        fun addArtifact(artifact: Artifact): Builder {
            artifacts.add(artifact)
            return this
        }

        /**
         * Adds a path that should be excluded during extraction of the library.
         * @param path File path within the artifact to ignore.
         * @return Builder for chaining methods.
         */
        fun excludePath(path: String): Builder {
            exclusions.add(path)
            return this
        }

        /**
         * Adds a rule that should be checked to determine whether the library should be included.
         * @param rule Rule to add.
         * @return Builder for chaining methods.
         */
        fun addRule(rule: Rule): Builder {
            rules.add(rule)
            return this
        }

        /**
         * Specifies that an artifact is a native for an OS.
         * @param os Operating system the native is for.
         * @param artifactId Name of the native artifact.
         * @return Builder for chaining methods.
         */
        fun specifyNative(os: OSType, artifactId: String): Builder {
            natives[os] = artifactId
            return this
        }

        /**
         * Creates the library.
         * @return Constructed library information.
         */
        fun build(): Library {
            return Library(gav, artifacts.toList(),
                    exclusions.toList(), rules.toList(), natives.toMap())
        }
    }
}