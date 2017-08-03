package com.lapismc.minecraft.versioning

/**
 * Summarized information about all versions available of the game.
 * @param versions Stubs for each version.
 * @param latestReleaseId Name of the latest release version.
 * @param latestSnapshotId Name of the latest snapshot version.
 */
class VersionManifest(versions: Collection<VersionStub>, val latestReleaseId: String, val latestSnapshotId: String): Collection<VersionStub> {
    private val versionMap = HashMap<String, VersionStub>(versions.size)

    init {
        versions.forEach { versionMap.put(it.id, it) }
    }

    /**
     * Latest release version.
     */
    val latestRelease: VersionStub? = get(latestReleaseId)

    /**
     * Latest snapshot version.
     */
    val latestSnapshot: VersionStub? = get(latestSnapshotId)

    /**
     * Retrieve a stub for a specified version.
     * @param id Unique name of the version.
     */
    fun get(id: String) = versionMap[id]

    /**
     * Returns the size of the collection.
     */
    override val size = versionMap.size

    /**
     * Checks if the specified stub is contained in the manifest.
     * @param element Stub to look for in the manifest.
     * @return True if the stub exists in the manifest, or false if it doesn't.
     */
    override fun contains(element: VersionStub) = versionMap.containsValue(element)

    /**
     * Checks if all stubs in the specified collection are contained in the manifest.
     * @param elements Set of stubs to look for in the manifest.
     * @return True if all of the stubs exist in the manifest, or false if at least one doesn't.
     */
    override fun containsAll(elements: Collection<VersionStub>) = elements.all { versionMap.containsValue(it) }

    /**
     * Checks whether the manifest is empty.
     * @return True if the manifest is empty (contains no versions), false otherwise.
     */
    override fun isEmpty() = versionMap.isEmpty()

    /**
     * Retrieves an iterator that can be used to iterate over all version stubs.
     * @return Immutable iterator of version stubs.
     */
    override fun iterator(): Iterator<VersionStub> = versionMap.values.stream().iterator()

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