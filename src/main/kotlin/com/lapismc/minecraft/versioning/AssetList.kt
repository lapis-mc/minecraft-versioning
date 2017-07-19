package com.lapismc.minecraft.versioning

/**
 * Collection of asset information needed to run the game.
 * @param objects All of the assets needed to run the game.
 * @param legacy Indicates whether the legacy asset system should be used.
 *  Specify true for legacy asset system, and false for modern system.
 */
data class AssetList(val objects: List<Asset>, val legacy: Boolean = false) {

    /**
     * Constructs a list of assets.
     * @param legacy Indicates whether the legacy asset system should be used.
     */
    class Builder(val legacy: Boolean = false) {
        private val objects = ArrayList<Asset>()

        /**
         * Adds an asset to the list.
         * @param asset New asset to add.
         * @return Builder for chaining methods.
         */
        fun addAsset(asset: Asset): Builder {
            objects.add(asset)
            return this
        }

        /**
         * Creates the asset list with all the previously provided information.
         * @return Constructed asset list.
         */
        fun build(): AssetList {
            return AssetList(objects.toList(), legacy)
        }
    }
}