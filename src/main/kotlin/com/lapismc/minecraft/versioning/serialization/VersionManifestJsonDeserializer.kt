package com.lapismc.minecraft.versioning.serialization

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.salomonbrys.kotson.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.stream.JsonReader
import com.lapismc.minecraft.versioning.VersionManifest
import com.lapismc.minecraft.versioning.VersionStub
import com.lapismc.minecraft.versioning.VersionType
import java.io.Reader
import java.util.*

/**
 * Created by michael on 7/19/17.
 */
class VersionManifestJsonDeserializer : ResponseDeserializable<VersionManifest> {
    override fun deserialize(reader: Reader): VersionManifest? {
        val gson = GsonBuilder()
                .registerTypeAdapter<VersionManifest> {
                    deserialize { readManifestDocument(it) }
                    serialize { jsonNull } // Empty, unused serializer to make Kotson happy.
                }
                .registerTypeAdapter<VersionStub> {
                    deserialize { readVersionStub(it) }
                    serialize { jsonNull } // Empty, unused serializer.
                }
                .create()
        return gson.fromJson<VersionManifest>(reader)
    }

    private fun readManifestDocument(deserializer: DeserializerArg): VersionManifest {
        /**
         * Version manifest documents look like this:
         * {
         *   "latest": { }
         *   "versions": [ ]
         * }
         */
        val builder = VersionManifest.Builder()
        val root    = deserializer.json.asJsonObject
        if(root.has("latest"))
            readLatestBlock(root["latest"], builder)
        if(root.has("versions"))
            readVersionsBlock(deserializer, builder)
        return builder.build()
    }

    private fun readVersionStub(deserializer: DeserializerArg): VersionStub {
        /**
         * Version stub block looks like this:
         * {
         *   "id": "1.12-pre7",
         *   "type": "snapshot",
         *   "time": "2017-06-13T06:57:00+00:00",
         *   "releaseTime": "2017-05-31T10:56:41+00:00",
         *   "url": "https://launchermeta.mojang.com/mc/game/870bdfd6ea61ee9cccedab53b28650c68c9cb410/1.12-pre7.json"
         * }
         */
        val element = deserializer.json.asJsonObject

        val id             = element["id"].string
        val typeStr        = element["type"].string
        val updateTimeStr  = element["time"].string
        val releaseTimeStr = element["releaseTime"].string
        val url            = element["url"].string

        val type = VersionType.fromString(typeStr)
        val updateTimeStr = LocalDateTime
        return VersionStub("foo", VersionType.RELEASE, Date(), Date(), "foo")
    }

    private fun readVersionsBlock(deserializer: DeserializerArg, builder: VersionManifest.Builder) {
        val element = deserializer.json
        val stubs   = deserializer.context.deserialize<List<VersionStub>>(element)
        stubs.forEach { builder.addVersion(it) }
    }
}