package com.lapismc.minecraft.versioning

/**
 * Common attributes that downloadable objects have.
 * @param name String used to uniquely reference the resource.
 * @param url URL of the location to retrieve the resource data.
 * @param hash SHA-1 hash of the resource's contents.
 * @param size Size of the resource in bytes.
 */
data class Resource(val name: String, val url: String, val hash: String, val size: Int)