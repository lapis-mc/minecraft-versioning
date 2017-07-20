package com.lapismc.minecraft.versioning.serialization

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.salomonbrys.kotson.*
import com.google.gson.GsonBuilder
import com.lapismc.minecraft.versioning.AssetList
import com.lapismc.minecraft.versioning.VersionManifest
import com.lapismc.minecraft.versioning.VersionStub
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
                .registerTypeAdapter<VersionManifest> {
                    deserialize { TODO() }
                    serialize { jsonNull } // Empty, unused serializer to make Kotson happy.
                }
                .registerTypeAdapter<VersionStub> {
                    deserialize { TODO() }
                    serialize { jsonNull } // Empty, unused serializer.
                }
                .create()
        return gson.fromJson<AssetList>(reader)
    }
}