package com.lapismc.minecraft.versioning

import java.time.LocalDateTime

/**
 * Brief information about a version of the game.
 * Can be used to get additional information about the version and its dependencies.
 * @param id Unique name of the version.
 * @param type Type of version (release, snapshot, etc.).
 * @param updateTime Last date and time the version was updated.
 *  May be newer than the release time.
 * @param releaseTime Date and time the version was first released.
 * @param url Location of where to get additional information for the version.
 */
data class VersionStub(val id: String, val type: VersionType,
                       val updateTime: LocalDateTime, val releaseTime: LocalDateTime, val url: String)