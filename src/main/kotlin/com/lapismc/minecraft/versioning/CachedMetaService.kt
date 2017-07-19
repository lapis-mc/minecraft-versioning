package com.lapismc.minecraft.versioning

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
    override fun getVersionManifest(): VersionManifest {
        var manifest = cachedManifest
        if(manifest == null) {
            manifest = service.getVersionManifest()
            cachedManifest = manifest
            return manifest
        } else {
            return manifest
        }
    }

    /**
     * Retrieves additional information for a version.
     * @param stub Version information from the manifest.
     * @return Complete version information referenced by the stub.
     */
    override fun getVersion(stub: VersionStub): Version {
        var version = versionCache.get(stub.id)
        if(version == null) {
            version = service.getVersion(stub)
            versionCache.put(stub.id, version)
            return version
        } else {
            return version
        }
    }

    /**
     * Retrieves a list of assets needed for the game to run.
     * @param index Reference to the list of assets to retrieve.
     * @return List of assets corresponding to the specified index.
     */
    override fun getAssetList(index: AssetIndex): AssetList {
        var assetList = assetListCache.get(index.resource.name)
        if(assetList == null) {
            assetList = service.getAssetList(index)
            assetListCache.put(index.resource.name, assetList)
            return assetList
        } else {
            return assetList
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