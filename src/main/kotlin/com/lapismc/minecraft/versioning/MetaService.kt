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
     * Retrieves a list of assets needed for the game to run.
     * @param index Reference to the list of assets to retrieve.
     * @return Result of the request.
     *  If the request was successful, then a list of assets corresponding to the specified index is returned.
     *  If the request failed, then the exception information is returned.
     */
    fun getAssetList(index: AssetIndex): Result<AssetList, Exception>
}