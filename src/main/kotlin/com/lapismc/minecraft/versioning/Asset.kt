package com.lapismc.minecraft.versioning

/**
 * Object or resource the game requires to run.
 * @param path Filepath to where the resource should be stored locally.
 * @param hash SHA-1 hash of the asset's contents.
 * @param size Size of the asset in bytes.
 */
data class Asset(val path: String, val hash: String, val size: Int)