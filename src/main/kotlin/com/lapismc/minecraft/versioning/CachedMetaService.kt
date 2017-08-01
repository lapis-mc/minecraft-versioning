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
        return cachedAccess(key, versionCache, {
            service.getVersion(stub)
        })
    }

    /**
     * Retrieves a list of assets needed for the game to run.
     * @param index Reference to the list of assets to retrieve.
     * @return List of assets corresponding to the specified index.
     */
    override fun getAssetList(index: AssetIndex): Result<AssetList, Exception> {
        val key = index.resource.name
        return cachedAccess(key, assetListCache, {
            service.getAssetList(index)
        })
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

    /**
     * Generic method for accessing caches.
     * If a value is already in the cache, this method will return it.
     * If not, then the [missing] block is called to retrieve the value.
     * @param key Unique key used to address the value in the cache.
     * @param map Hash map representing the cache.
     * @param missing Block to call to get the value when there's a cache miss.
     *  The block is expected to return a result.
     *  If the result is successful, then the value is stored in the cache.
     * @return Result of retrieving a value from the cache.
     *  If the item is not in the cache and can't be retrieved, an error is returned.
     */
    private fun <T : Any> cachedAccess(key: String, map: HashMap<String, T>, missing: () -> Result<T, Exception>): Result<T, Exception> {
        val cachedValue = map[key]
        return if(cachedValue != null)
            Result.of(cachedValue)
        else {
            val result = missing.invoke()
            if(result is Result.Success)
                map[key] = result.value
            result
        }
    }
}