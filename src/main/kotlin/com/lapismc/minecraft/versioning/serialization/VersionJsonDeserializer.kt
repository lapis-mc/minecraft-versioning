package com.lapismc.minecraft.versioning.serialization

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.salomonbrys.kotson.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.lapismc.minecraft.versioning.*
import java.io.Reader
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * Pulls version information from a JSON document.
 * @param versionUrl URL of the version document.
 */
class VersionJsonDeserializer(private val versionUrl: String) : ResponseDeserializable<Version> {
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
            readDownloadsBlock(root["downloads"], builder)
        if(root.has("libraries"))
            readLibrariesBlock(root["libraries"], deserializer.context, builder)
        return builder.build()
    }

    /**
     * Retrieves stub information from the main document.
     * @param element Root document JSON element.
     * @return Constructed version stub.
     */
    private fun readStubInfo(element: JsonObject): VersionStub {
        // TODO: This code was copied from VersionManifestJsonDeserializer (code duplication).
        val id             = element["id"].string
        val typeStr        = element["type"].string
        val updateTimeStr  = element["time"].string
        val releaseTimeStr = element["releaseTime"].string

        val type        = VersionType.fromString(typeStr)
        val updateTime  = OffsetDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(updateTimeStr))
        val releaseTime = OffsetDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(releaseTimeStr))
        return VersionStub(id, type, updateTime, releaseTime, versionUrl)
    }

    /**
     * Retrieves launcher information from the main document.
     * @param element Root document JSON element.
     * @return Constructed launcher information.
     */
    private fun readLauncherInfo(element: JsonObject): LauncherInfo {
        val mainClass      = element["mainClass"].string
        val minimumVersion = element["minimumLauncherVersion"].int
        val argumentsStr   = element["minecraftArguments"].string
        val arguments      = argumentsStr.split(Regex("\\s+"))
        return LauncherInfo(mainClass, minimumVersion, arguments)
    }

    /**
     * Processes the resource downloads block.
     * @param element JSON element referring to the downloads block.
     * @param builder Version builder to report information to.
     */
    private fun readDownloadsBlock(element: JsonElement, builder: Version.Builder) {
        /**
         * Downloads block looks like this:
         * {
         *  "client": { }
         * }
         */
        val downloadsObject = element.asJsonObject
        val downloads = downloadsObject.entrySet().map { readResourceBlock(it.key, it.value) }
        downloads.forEach { builder.addDownload(it) }
    }

    /**
     * Retrieves resource information from a download block entry.
     * @param name Name of the resource.
     * @param element JSON element referring to the resource block.
     * @return Resource information pulled from the block.
     */
    private fun readResourceBlock(name: String, element: JsonElement): Resource {
        /**
         * Resource block looks like this:
         * {
         *   "sha1": "909823f9c467f9934687f136bc95a667a0d19d7f",
         *   "size": 10177098,
         *   "url": "https://launcher.mojang.com/mc/game/1.12/client/909823f9c467f9934687f136bc95a667a0d19d7f/client.jar"
         * }
         */
        val url  = element["url"].string
        val hash = element["sha1"].string
        val size = element["size"].int
        return Resource(name, url, hash, size)
    }

    /**
     * Retrieves libraries from a version document.
     * @param element JSON element referring to the libraries block.
     * @param context Gson context for the deserializer.
     * @param builder Version manifest builder to report information to.
     */
    private fun readLibrariesBlock(element: JsonElement, context: DeserializerArg.Context, builder: Version.Builder) {
        val libraries = context.deserialize<List<Library>>(element)
        libraries.forEach { builder.addLibrary(it) }
    }

    /**
     * Parses asset index information from JSON.
     * @param deserializer Deserialization information.
     * @return Parsed asset index.
     */
    private fun readAssetIndex(deserializer: DeserializerArg): AssetIndex {
        /**
         * Asset index block looks like this:
         * {
         *   "id": "1.12",
         *   "sha1": "67e29e024e664064c1f04c728604f83c24cbc218",
         *   "size": 169014,
         *   "url": "https://launchermeta.mojang.com/mc/assets/1.12/67e29e024e664064c1f04c728604f83c24cbc218/1.12.json",
         *   "totalSize": 127037169
         * }
         */
        val element   = deserializer.json.asJsonObject
        val name      = element["id"].string
        val totalSize = element["totalSize"].int
        val resource  = readResource(name, element)
        return AssetIndex(totalSize, resource)
    }

    /**
     * Parses resource information from a JSON block.
     * @param name Name of the resource.
     * @param element Reference to the JSON object containing the resource information.
     * @return Constructed resource.
     */
    private fun readResource(name: String, element: JsonObject): Resource {
        val url  = element["url"].string
        val hash = element["sha1"].string
        val size = element["size"].int
        return Resource(name, url, hash, size)
    }

    /**
     * Parses library information from JSON.
     * @param deserializer Deserialization information.
     * @return Constructed library.
     */
    private fun readLibrary(deserializer: DeserializerArg): Library {
        /**
         * Library block looks like this:
         * {
         *   "extract": { },
         *   "name": "org.lwjgl.lwjgl:lwjgl-platform:2.9.0",
         *   "natives": { },
         *   "rules": [ ],
         *   "downloads": { }
         * }
         */
        val element = deserializer.json.asJsonObject
        val name    = element["name"].string
        val gav     = GroupArtifactVersionId.parse(name)
        val builder = Library.Builder(gav)
        if(element.has("extract"))
            readLibraryExtractBlock(element["extract"], builder)
        if(element.has("natives"))
            readLibraryNativesBlock(element["natives"], builder)
        if(element.has("rules"))
            deserializer.context.deserialize<List<Rule>>(element["rules"]).forEach { builder.addRule(it) }
        if(element.has("downloads"))
            readLibraryDownloadsBlock(element["downloads"], builder)
        return builder.build()
    }

    /**
     * Reads extraction information from a library extract block.
     * @param element Reference to the JSON object containing the extract information.
     * @param builder Library builder to report information to.
     */
    private fun readLibraryExtractBlock(element: JsonElement, builder: Library.Builder) {
        /**
         * Extract block looks like this:
         * {
         *   "exclude": [
         *     "META-INF/"
         *   ]
         * }
         */
        val extractObject = element.asJsonObject
        if(extractObject.has("exclude")) {
            val excludeArray = extractObject["exclude"].asJsonArray
            excludeArray.forEach { builder.excludePath(it.string) }
        }
    }

    /**
     * Reads native package information from a library native block.
     * @param element Reference to JSON object containing the natives information.
     * @param builder Library builder to report information to.
     */
    private fun readLibraryNativesBlock(element: JsonElement, builder: Library.Builder) {
        /**
         * Natives block looks like this:
         * {
         *   "linux": "natives-linux",
         *   "osx": "natives-osx",
         *   "windows": "natives-windows"
         * }
         */
        val nativesObject = element.asJsonObject
        nativesObject.entrySet().forEach {
            val os = OSType.valueOf(it.key.toUpperCase())
            builder.specifyNative(os, it.value.string)
        }
    }

    /**
     * Reads the downloads block from a library downloads block.
     * @param element Reference to the JSON object containing the downloads information.
     * @param builder Version manifest builder to report information to.
     */
    private fun readLibraryDownloadsBlock(element: JsonElement, builder: Library.Builder) {
        /**
         * Downloads block looks like this:
         * {
         *   "classifiers": {
         *     "natives-linux": { },
         *     "natives-osx": { },
         *     "natives-windows": { },
         *   },
         *   "artifact": { }
         * }
         */
        val downloadsObject = element.asJsonObject
        if(downloadsObject.has("artifact"))
            builder.addArtifact(readArtifactBlock("artifact", downloadsObject["artifact"]))
        if(downloadsObject.has("classifiers")) {
            val labeledArtifacts = downloadsObject["classifiers"].asJsonObject
            labeledArtifacts.entrySet().forEach {
                val artifact = readArtifactBlock(it.key, it.value)
                builder.addArtifact(artifact)
            }
        }
    }

    /**
     * Reads an artifact from a JSON object.
     * @param name Name of the artifact.
     * @param element JSON element referencing the artifact block.
     * @return Constructed artifact from the information in the blcok.
     */
    private fun readArtifactBlock(name: String, element: JsonElement): Artifact {
        /**
         * Artifact block looks like this:
         * {
         *   "size": 65020,
         *   "sha1": "419c05fe9be71f792b2d76cfc9b67f1ed0fec7f6",
         *   "path": "com/paulscode/soundsystem/20120107/soundsystem-20120107.jar",
         *   "url": "https://libraries.minecraft.net/com/paulscode/soundsystem/20120107/soundsystem-20120107.jar"
         * }
         */
        val artifactObject = element.asJsonObject
        val path     = artifactObject["path"].string
        val resource = readResource(name, artifactObject)
        return Artifact(path, resource)
    }

    /**
     * Reads a rule from JSON.
     * @param deserializer Deserialization information.
     * @return Constructed rule.
     */
    private fun readRule(deserializer: DeserializerArg): Rule {
        /**
         * Rule block looks like this:
         * {
         *   "action": "allow"
         * }
         */
        val element = deserializer.json.asJsonObject
        val allowed = element["action"].string == "allowed"
        if(element.has("os"))
            return readOSRule(element["os"], allowed)
        return Rule(allowed)
    }

    /**
     * Reads an OS rule from JSON.
     * @param element JSON element referencing the OS-specific rule block.
     * @param allowed Flag indicating whether the rule is applicable.
     * @return Constructed OS rule.
     */
    private fun readOSRule(element: JsonElement, allowed: Boolean): OSRule {
        /**
         * OS block looks like this:
         * {
         *   "name": "osx"
         *   "version": "^10\\.5\\.\\d$"
         * }
         */
        val osObj  = element["os"].obj
        val osName = osObj["name"].string
        val osType = OSType.valueOf(osName.toUpperCase())
        // Version field is optional.
        return if(osObj.has("version")) {
            val versionRegex = osObj["version"].string
            OSRule(osType, Regex(versionRegex), allowed)
        } else {
            OSRule(osType, allowed)
        }
    }
}