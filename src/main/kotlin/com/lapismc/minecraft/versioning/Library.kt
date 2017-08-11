package com.lapismc.minecraft.versioning

import kotlin.coroutines.experimental.buildSequence

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
     * Checks whether the library should be included for the current OS.
     * @return True if the library is needed on the local OS,
     *  or false if it can be skipped.
     */
    fun isApplicable(): Boolean {
        val applicableRules = rules.filter { it.isApplicable() }
        if(applicableRules.isEmpty())
            return true
        return applicableRules.all { it.allowed }
    }

    /**
     * Gets the list of artifacts applicable to the current platform.
     * @return Set of artifacts needed for the current OS.
     */
    fun getApplicableArtifacts() = buildSequence<Artifact> {
        val common = commonArtifact
        if(common != null)
            yield(common)
        val native = nativeArtifact
        if(native != null)
            yield(native)
    }

    /**
     * Artifact used on all platforms needed by the library.
     * @return Non-native artifact.
     *   If null, then there is no non-native artifact for this library.
     */
    val commonArtifact: Artifact?
        get() = artifacts.find { it.resource.name == "artifact" }

    /**
     * Artifact used on specific platforms needed by the library.
     * @return Native artifact.
     *   If null, then there is no native artifact for this library.
     */
    val nativeArtifact: Artifact?
        get() {
            return if(natives.containsKey(OSType.current)) {
                val nativeKey = natives[OSType.current]
                artifacts.find { it.resource.name == nativeKey }
            } else {
                null
            }
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