package com.lapismc.minecraft.versioning

import com.github.kittinunf.result.Result

/**
 * Provides cached access to a meta-service.
 * Repeat requests will be retrieved from cache
 * instead of performing another possibly expensive request.
 * @param service Underlying service that performs the requests.
 */
class CachedMetaService(private val service: MetaService) : MetaService {
    private var cachedManifest: VersionManifest? = null
    private val versionCache   = HashMap<String, Version>()
    private val assetListCache = HashMap<String, AssetList>()

    /**
     * Retrieves summarized information about all available versions.
     * @return Top-level version information.
     */
    override fun getVersionManifest(): Result<VersionManifest, Exception> {
        val manifest = cachedManifest
        return if(manifest != null)
            Result.of(manifest)
        else {
            val result = service.getVersionManifest()
            if(result is Result.Success)
                cachedManifest = result.value
            result
        }
    }

    /**
     * Retrieves additional information for a version.
     * @param stub Version information from the manifest.
     * @return Complete version information referenced by the stub.
     */
    override fun getVersion(stub: VersionStub): Result<Version, Exception> {
        val key = stub.id
        val version = versionCache[key]
        return if(version != null)
            Result.of(version)
        else {
            val result = service.getVersion(stub)
            if(result is Result.Success)
                versionCache[key] = result.value
            result
        }
    }

    /**
     * Retrieves a list of assets needed for the game to run.
     * @param index Reference to the list of assets to retrieve.
     * @return List of assets corresponding to the specified index.
     */
    override fun getAssetList(index: AssetIndex): Result<AssetList, Exception> {
        val key = index.resource.name
        val assetList = assetListCache[key]
        return if(assetList != null)
            Result.of(assetList)
        else {
            val result = service.getAssetList(index)
            if(result is Result.Success)
                assetListCache[key] = result.value
            result
        }
    }

    /**
     * Removes all cached data.
     * Any new requests will request the data again.
     */
    fun clear() {
        cachedManifest = null
        versionCache.clear()
        assetListCache.clear()
    }
}