package com.lapismc.minecraft.versioning

/**
 * Summarized information about all versions available of the game.
 * @param versions Stubs for each version.
 * @param latestReleaseId Name of the latest release version.
 * @param latestSnapshotId Name of the latest snapshot version.
 */
class VersionManifest(val versions: List<VersionStub>, val latestReleaseId: String, val latestSnapshotId: String) {
    /**
     * Latest release version.
     */
    val latestRelease: VersionStub?
        get() = versions.find { it.id == latestReleaseId }

    /**
     * Latest snapshot version.
     */
    val latestSnapshot: VersionStub?
        get() = versions.find { it.id == latestSnapshotId }

    /**
     * Constructs a version manifest.
     */
    class Builder {
        private val versions = ArrayList<VersionStub>()
        private var latestReleaseId: String?  = null
        private var latestSnapshotId: String? = null

        /**
         * Adds a version of the game to the manifest.
         * @param version Version to add.
         * @return Builder for chaining methods.
         */
        fun addVersion(version: VersionStub): Builder {
            versions.add(version)
            return this
        }

        /**
         * Defines which release version is the latest.
         * @param id Name of the latest release version.
         */
        fun latestRelease(id: String): Builder {
            latestReleaseId = id
            return this
        }

        /**
         * Defines which snapshot version is the latest.
         * @param id Name of the latest snapshot version.
         */
        fun latestSnapshot(id: String): Builder {
            latestSnapshotId = id
            return this
        }

        /**
         * Constructs the version manifest.
         * @return Created version manifest.
         */
        fun build(): VersionManifest {
            // TODO: Find a better way to handle tracking the latest release and snapshot.
            return VersionManifest(versions.toList(), latestReleaseId!!, latestSnapshotId!!)
        }
    }
}