package com.lapismc.minecraft.versioning

/**
 * Complete information about a version of the game and its dependencies.
 * @param stub Basic information about the version.
 * @param assetIndex Summary of assets needed for the game and where to find them.
 * @param downloads Logical downloads for items such as the client and server.
 * @param libraries Packages the game depends on to run.
 * @param launcher Information needed for the launcher to start the game.
 */
class Version(stub: VersionStub, val assetIndex: AssetIndex, val downloads: List<Resource>,
              val libraries: List<Library>, val launcher: LauncherInfo) {
    /**
     * Unique name of the version.
     */
    val id = stub.id

    /**
     * Type of version (release, snapshot, etc.).
     */
    val type = stub.type

    /**
     * Last date and time the version was updated.
     * May be newer than the release time.
     */
    val updateTime = stub.updateTime

    /**
     * Date and time the version was first released.
     */
    val releaseTime = stub.releaseTime

    /**
     * Location of where to get additional information for the version.
     */
    val url = stub.url

    /**
     * Retrieves a list of libraries needed to run the game on the current system.
     * @return List of required libraries.
     */
    fun getApplicableLibraries() = libraries.filter { it.isApplicable() }

    /**
     * Creates a string representation of the version.
     * @return Version and type as a string.
     */
    override fun toString(): String {
        return "Version($id $type)"
    }

    /**
     * Constructs information for a single version of the game.
     * @param assetIndex Summary of assets needed for the game and where to find them.
     * @param launcher Information needed for the launcher to start the game.
     */
    class Builder(private val stub: VersionStub, private val assetIndex: AssetIndex,
                  private val launcher: LauncherInfo) {
        private val downloads = ArrayList<Resource>()
        private val libraries = ArrayList<Library>()

        /**
         * Adds a download for the version.
         * @param download Download to add.
         * @return Builder for chaining methods.
         */
        fun addDownload(download: Resource): Builder {
            downloads.add(download)
            return this
        }

        /**
         * Adds a library that the version depends on.
         * @return Builder for chaining methods.
         */
        fun addLibrary(library: Library): Builder {
            libraries.add(library)
            return this
        }

        /**
         * Constructs the version.
         * @return Constructed version information.
         */
        fun build(): Version {
            return Version(stub, assetIndex, downloads.toList(), libraries.toList(), launcher)
        }
    }
}