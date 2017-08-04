package com.lapismc.minecraft.versioning

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import com.lapismc.minecraft.versioning.serialization.*

/**
 * Web-based meta-service.
 * Retrieves version information from the web.
 * @param manifestUrl URL of where the version manifest document can be retrieved from.
 * @param resourceUrl Base URL of where the game assets can be downloaded from.
 */
class WebMetaService(private val manifestUrl: String, private val resourceUrl: String) : MetaService {
    companion object {
        private const val officialManifestUrl = "https://launchermeta.mojang.com/mc/game/version_manifest.json"
        private const val officialResourceUrl = "https://resources.download.minecraft.net"

        /**
         * Gets a meta-service pointing to the official Mojang version manifest.
         * @return Official meta-service.
         */
        fun official() = WebMetaService(officialManifestUrl, officialResourceUrl)
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
     * Retrieves the raw content of a version document.
     * This is for downloading and storing the document locally.
     * @param stub Version information from the manifest.
     * @return Result of the request.
     *  If the request was successful, then version information document referenced by the stub is returned.
     *  If the request failed, then the exception information is returned.
     */
    override fun getVersionDocument(stub: VersionStub): Result<ByteArray, Exception> {
        val (_, _, result) = Fuel.get(stub.url).response()
        return result
    }

    /**
     * Retrieves a set of assets needed for the game to run.
     * @param index Reference to the list of assets to retrieve.
     * @return Result of the request.
     *  If the request was successful, then a list of assets corresponding to the specified index is returned.
     *  If the request failed, then the exception information is returned.
     */
    override fun getAssetList(index: AssetIndex): Result<AssetCollection, Exception> {
        val (_, _, result) = Fuel.get(index.resource.url).responseObject(AssetCollectionJsonDeserializer())
        return result
    }

    /**
     * Retrieves the raw content of an asset list document.
     * This is for downloading and storing the document locally.
     * @param index Reference to the list of assets to retrieve.
     * @return Result of the request.
     *  If the request was successful, then the asset list document corresponding to the specified index is returned.
     *  If the request failed, then the exception information is returned.
     */
    override fun getAssetListDocument(index: AssetIndex): Result<ByteArray, Exception> {
        val (_, _, result) = Fuel.get(index.resource.url).response()
        return result
    }

    /**
     * Retrieves the raw content of an asset.
     * This is for downloading and storing the asset locally.
     * @param asset Reference to the asset to download the contents of.
     * @return Result of the request.
     *  If the request was successful, then the asset content is returned.
     *  If the request failed, then the exception information is returned.
     */
    override fun getAssetContent(asset: Asset): Result<ByteArray, Exception> {
        val url = constructAssetUrl(asset)
        val (_, _, result) = Fuel.get(url).response()
        return result
    }

    /**
     * Retrieves the raw content of an artifact from a library.
     * This is for downloading and storing the artifact locally.
     * @param artifact Reference to the artifact to download the contents of.
     * @return Result of the request.
     *  If the request was successful, then the artifact content is returned.
     *  If the request failed, then the exception information is returned.
     */
    override fun getArtifactContent(artifact: Artifact): Result<ByteArray, Exception> {
        val (_, _, result) = Fuel.get(artifact.resource.url).response()
        return result
    }

    /**
     * Creates a URL indicating where an asset can be downloaded from.
     * @param asset Reference to the asset to generate a URL for.
     * @return URL of the asset.
     */
    private fun constructAssetUrl(asset: Asset): String {
        val hash = asset.hash
        val dir  = hash.substring(0..2)
        return "$resourceUrl/$dir/$hash"
    }
}