package com.lapismc.minecraft.versioning.serialization

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.salomonbrys.kotson.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.lapismc.minecraft.versioning.VersionManifest
import com.lapismc.minecraft.versioning.VersionStub
import com.lapismc.minecraft.versioning.VersionType
import java.io.Reader
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * Pulls version manifest information from a JSON document.
 */
class VersionManifestJsonDeserializer : ResponseDeserializable<VersionManifest> {
    /**
     * Read manifest information.
     * @param reader JSON reader used to get manifest data.
     * @return Constructed version manifest.
     */
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

    /**
     * Processes the top-level elements of the version manifest document.
     * @param deserializer Deserialization information.
     * @return Parsed version manifest.
     */
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
            readVersionsBlock(root["versions"], deserializer.context, builder)
        return builder.build()
    }

    /**
     * Processes the latest versions block in the version manifest document.
     * @param element JSON element referring to the latest block.
     * @param builder Version manifest builder to report information to.
     */
    private fun readLatestBlock(element: JsonElement, builder: VersionManifest.Builder) {
        /**
         * Latest block looks like this:
         * {
         *   "snapshot": "1.12-pre7",
         *   "release": "1.11.2"
         * }
         */
        val latestReleaseId  = element["release"].string
        val latestSnapshotId = element["snapshot"].string
        builder.latestRelease(latestReleaseId)
        builder.latestSnapshot(latestSnapshotId)
    }

    /**
     * Processes the versions block in the version manifest document.
     * @param element JSON element referring to the versions block.
     * @param context Gson context for the deserializer.
     * @param builder Version manifest builder to report information to.
     */
    private fun readVersionsBlock(element: JsonElement, context: DeserializerArg.Context, builder: VersionManifest.Builder) {
        val stubs = context.deserialize<List<VersionStub>>(element)
        stubs.forEach { builder.addVersion(it) }
    }

    /**
     * Processes a version stub block in the version manifest document.
     * @param deserializer Deserialization information.
     * @return Parsed version stub.
     */
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

        val type        = VersionType.fromString(typeStr)
        val updateTime  = OffsetDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(updateTimeStr))
        val releaseTime = OffsetDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(releaseTimeStr))
        return VersionStub(id, type, updateTime, releaseTime, url)
    }
}