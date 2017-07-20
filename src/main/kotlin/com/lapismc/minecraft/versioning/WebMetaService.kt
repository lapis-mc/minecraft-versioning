package com.lapismc.minecraft.versioning

import com.github.kittinunf.fuel.Fuel
import com.lapismc.minecraft.versioning.serialization.VersionManifestJsonDeserializer

/**
 * Web-based meta-service.
 * Retrieves version information from the web.
 */
class WebMetaService(private val manifestUrl: String) : MetaService {
    companion object {
        private const val officialManifestUrl = "https://launchermeta.mojang.com/mc/game/version_manifest.json"

        /**
         * Gets a meta-service pointing to the official Mojang version manifest.
         * @return Official meta-service.
         */
        fun official() = WebMetaService(officialManifestUrl)
    }

    /**
     * Retrieves summarized information about all available versions.
     * @return Top-level version information.
     */
    override fun getVersionManifest(): VersionManifest {
        val (_, _, result) = Fuel.get(manifestUrl).responseObject(VersionManifestJsonDeserializer())
        result.fold(success = {
            return result.get()
        }, failure = {
            TODO()
        })
    }

    /**
     * Retrieves additional information for a version.
     * @param stub Version information from the manifest.
     * @return Complete version information referenced by the stub.
     */
    override fun getVersion(stub: VersionStub): Version = TODO()

    /**
     * Retrieves a list of assets needed for the game to run.
     * @param index Reference to the list of assets to retrieve.
     * @return List of assets corresponding to the specified index.
     */
    override fun getAssetList(index: AssetIndex): AssetList = TODO()
}