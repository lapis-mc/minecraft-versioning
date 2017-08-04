package com.lapismc.minecraft.versioning

import com.github.kittinunf.result.Result
import java.time.format.DateTimeFormatter

/**
 * Provides cached access to a meta-service.
 * Repeat requests will be retrieved from cache
 * instead of performing another possibly expensive request.
 * @param service Underlying service that performs the requests.
 */
class CachedMetaService(private val service: MetaService) : MetaService {
    private var cachedManifest: VersionManifest? = null
    private val versionCache   = HashMap<String, Version>()
    private val assetListCache = HashMap<String, AssetCollection>()
    private val downloadCache  = LRUCache<String, ByteArray>()

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
     * Retrieves the raw content of a version document.
     * This is for downloading and storing the document locally.
     * @param stub Version information from the manifest.
     * @return Result of the request.
     *  If the request was successful, then version information document referenced by the stub is returned.
     *  If the request failed, then the exception information is returned.
     */
    override fun getVersionDocument(stub: VersionStub): Result<ByteArray, Exception> {
        val key = stub.id + ":" + stub.updateTime.format(DateTimeFormatter.ISO_DATE_TIME)
        return cachedDownloadAccess(key, {
            service.getVersionDocument(stub)
        })
    }

    /**
     * Retrieves a set of assets needed for the game to run.
     * @param index Reference to the list of assets to retrieve.
     * @return List of assets corresponding to the specified index.
     */
    override fun getAssetList(index: AssetIndex): Result<AssetCollection, Exception> {
        val key = index.resource.name
        return cachedAccess(key, assetListCache, {
            service.getAssetList(index)
        })
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
        val key = index.resource.hash
        return cachedDownloadAccess(key, {
            service.getAssetListDocument(index)
        })
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
        val key = asset.hash
        return cachedDownloadAccess(key, {
            service.getAssetContent(asset)
        })
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
        val key = artifact.resource.hash
        return cachedDownloadAccess(key, {
            service.getArtifactContent(artifact)
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
        downloadCache.clear()
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

    /**
     * Utility method for giving access to the download cache.
     * If the content is already in the cache, this method will return it.
     * If not, then the [missing] block is called to retrieve the content.
     * @param key Unique key used to address the content in the cache (content hash).
     * @param missing Block to call to get the content when there's a cache miss.
     *  The block is expected to return a result.
     *  If the result is successful, then the content is stored in the cache.
     * @return Result of retrieving content from the cache.
     *  If the content is not in the cache and can't be retrieved, an error is returned.
     */
    private fun cachedDownloadAccess(key: String, missing: () -> Result<ByteArray, Exception>): Result<ByteArray, Exception> {
        return cachedAccess(key, downloadCache, missing)
    }

    /**
     * LRU cache for holding byte arrays of content.
     */
    private class LRUCache<K, V>(private val maxSize: Int = 256) : LinkedHashMap<K, V>(maxSize * 4 / 3, 0.75f, true) {
        /**
         * Indicates whether the oldest entry should be removed.
         * @param eldest Oldest entry.
         * @return True if the size limit has been reached on the cache, false otherwise.
         */
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?) = size > maxSize
    }
}