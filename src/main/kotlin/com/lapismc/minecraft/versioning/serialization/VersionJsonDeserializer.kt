package com.lapismc.minecraft.versioning.serialization

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.salomonbrys.kotson.*
import com.google.gson.GsonBuilder
import com.lapismc.minecraft.versioning.Version
import com.lapismc.minecraft.versioning.VersionManifest
import com.lapismc.minecraft.versioning.VersionStub
import java.io.Reader

/**
 * Pulls version information from a JSON document.
 */
class VersionJsonDeserializer : ResponseDeserializable<Version> {
    /**
     * Read version information.
     * @param reader JSON reader used to get version data.
     * @return Constructed version.
     */
    override fun deserialize(reader: Reader): Version? {
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
        return gson.fromJson<Version>(reader)
    }
}