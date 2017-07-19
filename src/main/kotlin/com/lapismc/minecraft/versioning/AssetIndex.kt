package com.lapismc.minecraft.versioning

/**
 * Summary of assets needed for the game and where to find them.
 * @param totalSize Combined size of all assets in bytes.
 * @param resource Information for retrieving and using the asset list.
 */
data class AssetIndex(val totalSize: Int, val resource: Resource)