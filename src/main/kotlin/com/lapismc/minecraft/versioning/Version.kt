package com.lapismc.minecraft.versioning

/**
 * Complete information about a version of the game and its dependencies.
 * @param assetIndex Summary of assets needed for the game and where to find them.
 * @param downloads Logical downloads for items such as the client and server.
 * @param libraries Packages the game depends on to run.
 * @param launcher Information needed for the launcher to start the game.
 */
class Version(val assetIndex: AssetIndex, val downloads: List<Resource>,
              val libraries: List<Library>, val launcher: LauncherInfo) {
    /**
     * Constructs information for a single version of the game.
     * @param assetIndex Summary of assets needed for the game and where to find them.
     * @param launcher Information needed for the launcher to start the game.
     */
    class Builder(val assetIndex: AssetIndex, val launcher: LauncherInfo) {
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
            return Version(assetIndex, downloads, libraries, launcher)
        }
    }
}