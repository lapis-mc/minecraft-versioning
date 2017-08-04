package com.lapismc.minecraft.versioning

import com.github.kittinunf.result.Result

/**
 * Provides methods for retrieving version information from a meta-service.
 */
interface MetaService {
    /**
     * Retrieves summarized information about all available versions.
     * @return Result of the request.
     *  If the request was successful, then the top-level version information (stubs) are returned.
     *  If the request failed, then the exception information is returned.
     */
    fun getVersionManifest(): Result<VersionManifest, Exception>

    /**
     * Retrieves additional information for a version.
     * @param stub Version information from the manifest.
     * @return Result of the request.
     *  If the request was successful, then the complete version information referenced by the stub is returned.
     *  If the request failed, then the exception information is returned.
     */
    fun getVersion(stub: VersionStub): Result<Version, Exception>

    /**
     * Retrieves the raw content of a version document.
     * This is for downloading and storing the document locally.
     * @param stub Version information from the manifest.
     * @return Result of the request.
     *  If the request was successful, then version information document referenced by the stub is returned.
     *  If the request failed, then the exception information is returned.
     */
    fun getVersionDocument(stub: VersionStub): Result<ByteArray, Exception>

    /**
     * Retrieves a set of assets needed for the game to run.
     * @param index Reference to the list of assets to retrieve.
     * @return Result of the request.
     *  If the request was successful, then a list of assets corresponding to the specified index is returned.
     *  If the request failed, then the exception information is returned.
     */
    fun getAssetList(index: AssetIndex): Result<AssetCollection, Exception>

    /**
     * Retrieves the raw content of an asset list document.
     * This is for downloading and storing the document locally.
     * @param index Reference to the list of assets to retrieve.
     * @return Result of the request.
     *  If the request was successful, then the asset list document corresponding to the specified index is returned.
     *  If the request failed, then the exception information is returned.
     */
    fun getAssetListDocument(index: AssetIndex): Result<ByteArray, Exception>

    /**
     * Retrieves the raw content of an asset.
     * This is for downloading and storing the asset locally.
     * @param asset Reference to the asset to download the contents of.
     * @return Result of the request.
     *  If the request was successful, then the asset content is returned.
     *  If the request failed, then the exception information is returned.
     */
    fun getAssetContent(asset: Asset): Result<ByteArray, Exception>

    /**
     * Retrieves the raw content of an artifact from a library.
     * This is for downloading and storing the artifact locally.
     * @param artifact Reference to the artifact to download the contents of.
     * @return Result of the request.
     *  If the request was successful, then the artifact content is returned.
     *  If the request failed, then the exception information is returned.
     */
    fun getArtifactContent(artifact: Artifact): Result<ByteArray, Exception>
}