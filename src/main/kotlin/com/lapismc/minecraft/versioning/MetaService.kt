package com.lapismc.minecraft.versioning

/**
 * Provides methods for retrieving version information from a meta-service.
 */
interface MetaService {
    /**
     * Retrieves summarized information about all available versions.
     * @return Top-level version information.
     */
    fun getVersionManifest(): VersionManifest

    /**
     * Retrieves additional information for a version.
     * @param stub Version information from the manifest.
     * @return Complete version information referenced by the stub.
     */
    fun getVersion(stub: VersionStub): Version

    /**
     * Retrieves a list of assets needed for the game to run.
     * @param index Reference to the list of assets to retrieve.
     * @return List of assets corresponding to the specified index.
     */
    fun getAssetList(index: AssetIndex): AssetList
}