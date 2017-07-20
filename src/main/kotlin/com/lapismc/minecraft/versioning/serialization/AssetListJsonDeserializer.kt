package com.lapismc.minecraft.versioning.serialization

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.salomonbrys.kotson.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.lapismc.minecraft.versioning.Asset
import com.lapismc.minecraft.versioning.AssetList
import java.io.Reader

/**
 * Pulls asset list information from a JSON document.
 */
class AssetListJsonDeserializer : ResponseDeserializable<AssetList> {
    /**
     * Read asset list information.
     * @param reader JSON reader used to get asset list data.
     * @return Constructed asset list.
     */
    override fun deserialize(reader: Reader): AssetList? {
        val gson = GsonBuilder()
                .registerTypeAdapter<AssetList> {
                    deserialize { readAssetList(it) }
                    serialize { jsonNull } // Empty, unused serializer to make Kotson happy.
                }
                .create()
        return gson.fromJson<AssetList>(reader)
    }

    /**
     * Parses an asset list from a JSON document.
     * @param deserializer Deserialization information.
     * @return Constructed asset list.
     */
    private fun readAssetList(deserializer: DeserializerArg): AssetList {
        val root    = deserializer.json.asJsonObject
        val legacy  = root.has("legacy") && root["legacy"].bool
        val builder = AssetList.Builder(legacy)
        if(root.has("objects")) {
            val objects = root["objects"].asJsonObject
            objects.entrySet().forEach {
                val asset = readAssetBlock(it.key, it.value)
                builder.addAsset(asset)
            }
        }
        return builder.build()
    }

    /**
     * Reads asset information from a JSON asset block.
     * @param path Path to where the asset should be stored locally.
     * @param element Reference to the JSON block containing the asset information.
     * @return Asset information read from the block.
     */
    private fun readAssetBlock(path: String, element: JsonElement): Asset {
        /**
         * Asset blocks look like this:
         * {
         *   "hash": "20abaa7d3b0baa105bc6023d5308f1e5d76acc41",
         *   "size": 11577
         * }
         */
        val assetObject = element.asJsonObject
        val hash = assetObject["hash"].string
        val size = assetObject["size"].int
        return Asset(path, hash, size)
    }
}