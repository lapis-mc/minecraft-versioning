package com.lapismc.minecraft.versioning

/**
 * Set of asset information needed to run the game.
 * @param objects All of the assets needed to run the game.
 * @param legacy Indicates whether the legacy asset system should be used.
 *  Specify true for legacy asset system, and false for modern system.
 */
data class AssetCollection(private val objects: Collection<Asset>, val legacy: Boolean = false) : Collection<Asset> {
    /**
     * Returns the number of assets in the list.
     */
    override val size = objects.size

    /**
     * Checks if the specified asset is contained in this list.
     * @param element Asset to look for.
     * @return True if the asset is in the list, or false if it isn't.
     */
    override fun contains(element: Asset) = objects.contains(element)

    /**
     * Checks if all assets in the specified collection are contained in this list.
     * @param elements Assets to look for.
     * @return True if all of the assets were found in the list, or false if at least one wasn't.
     */
    override fun containsAll(elements: Collection<Asset>) = objects.containsAll(elements)

    /**
     * Checks whether the list is empty.
     * @return True if the list is empty, or false if it has something in it.
     */
    override fun isEmpty() = objects.isEmpty()

    /**
     * Gets an iterator that can be used to iterate over all of the assets.
     * @return Immutable iterator.
     */
    override fun iterator(): Iterator<Asset> = objects.stream().iterator()

    /**
     * Constructs a list of assets.
     * @param legacy Indicates whether the legacy asset system should be used.
     */
    class Builder(private val legacy: Boolean = false) {
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
        fun build(): AssetCollection {
            return AssetCollection(objects.toList(), legacy)
        }
    }
}