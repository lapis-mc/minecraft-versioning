package com.lapismc.minecraft.versioning

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import com.lapismc.minecraft.versioning.serialization.*

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
     * @return Result of the request.
     *  If the request was successful, then the top-level version information (stubs) are returned.
     *  If the request failed, then the exception information is returned.
     */
    override fun getVersionManifest(): Result<VersionManifest, Exception> {
        val (_, _, result) = Fuel.get(manifestUrl).responseObject(VersionManifestJsonDeserializer())
        return result
    }

    /**
     * Retrieves additional information for a version.
     * @param stub Version information from the manifest.
     * @return Result of the request.
     *  If the request was successful, then the complete version information referenced by the stub is returned.
     *  If the request failed, then the exception information is returned.
     */
    override fun getVersion(stub: VersionStub): Result<Version, Exception> {
        val (_, _, result) = Fuel.get(stub.url).responseObject(VersionJsonDeserializer(stub.url))
        return result
    }

    /**
     * Retrieves a list of assets needed for the game to run.
     * @param index Reference to the list of assets to retrieve.
     * @return Result of the request.
     *  If the request was successful, then a list of assets corresponding to the specified index is returned.
     *  If the request failed, then the exception information is returned.
     */
    override fun getAssetList(index: AssetIndex): Result<AssetList, Exception> {
        val (_, _, result) = Fuel.get(index.resource.url).responseObject(AssetListJsonDeserializer())
        return result
    }
}