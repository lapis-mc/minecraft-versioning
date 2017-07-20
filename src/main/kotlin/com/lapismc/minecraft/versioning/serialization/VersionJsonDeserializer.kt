package com.lapismc.minecraft.versioning.serialization

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.salomonbrys.kotson.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.lapismc.minecraft.versioning.*
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
                .registerTypeAdapter<Version> {
                    deserialize { readVersionDocument(it) }
                    serialize { jsonNull } // Empty, unused serializer to make Kotson happy.
                }
                .registerTypeAdapter<AssetIndex> {
                    deserialize { readAssetIndex(it) }
                    serialize { jsonNull } // Empty, unused serializer.
                }
                .registerTypeAdapter<Resource> {
                    deserialize { readResource(it) }
                    serialize { jsonNull } // Empty, unused serializer.
                }
                .registerTypeAdapter<Library> {
                    deserialize { readLibrary(it) }
                    serialize { jsonNull } // Empty, unused serializer.
                }
                .registerTypeAdapter<Rule> {
                    deserialize { readRule(it) }
                    serialize { jsonNull } // Empty, unused serializer.
                }
                .create()
        return gson.fromJson<Version>(reader)
    }

    /**
     * Processes the top-level elements of the version document.
     * @param deserializer Deserialization information.
     * @return Parsed version.
     */
    private fun readVersionDocument(deserializer: DeserializerArg): Version {
        /**
         * Version documents look like this:
         * {
         *   "assetIndex": { },
         *   "assets": "legacy",
         *   "downloads": { },
         *   "id": "1.4.5",
         *   "libraries": [ ],
         *   "mainClass": "net.minecraft.launchwrapper.Launch",
         *   "minecraftArguments": "${auth_player_name} ${auth_session} --gameDir ${game_directory} --assetsDir ${game_assets}",
         *   "minimumLauncherVersion": 4,
         *   "releaseTime": "2012-12-19T22:00:00+00:00",
         *   "time": "2016-02-02T15:37:47+00:00",
         *   "type": "release"
         * }
         */
        val root       = deserializer.json.asJsonObject
        val stub       = readStubInfo(root)
        val launcher   = readLauncherInfo(root)
        val assetIndex = deserializer.context.deserialize<AssetIndex>(root["assetIndex"])
        val builder    = Version.Builder(stub, assetIndex, launcher)
        if(root.has("downloads"))
            readDownloadsBlock(root["downloads"], deserializer.context, builder)
        if(root.has("libraries"))
            readLibrariesBlock(root["libraries"], deserializer.context, builder)
        return builder.build()
    }

    private fun readStubInfo(element: JsonObject): VersionStub {
        TODO()
    }

    private fun readLauncherInfo(element: JsonObject): LauncherInfo {
        TODO()
    }

    private fun readDownloadsBlock(element: JsonElement, context: DeserializerArg.Context, builder: Version.Builder) {
        TODO()
    }

    private fun readLibrariesBlock(element: JsonElement, context: DeserializerArg.Context, builder: Version.Builder) {
        TODO()
    }

    private fun readLibraryDownloadsBlock(element: JsonElement, context: DeserializerArg.Context, builder: Version.Builder) {
        TODO()
    }

    private fun readArtifactBlock(name: String, element: JsonElement, context: DeserializerArg.Context): Artifact {
        TODO()
    }

    private fun readAssetIndex(deserializer: DeserializerArg): AssetIndex {
        TODO()
    }

    private fun readResource(deserializer: DeserializerArg): Resource {
        TODO()
    }

    private fun readLibrary(deserializer: DeserializerArg): Library {
        TODO()
    }

    private fun readRule(deserializer: DeserializerArg): Rule {
        TODO()
    }
}