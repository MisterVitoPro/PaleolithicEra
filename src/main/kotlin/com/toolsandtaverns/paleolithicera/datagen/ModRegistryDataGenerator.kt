package com.toolsandtaverns.paleolithicera.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

/**
 * Generates JSON files for all dynamic registry entries in the Paleolithic Era mod.
 * 
 * This registry data generator serves several critical functions in the mod's worldgen system:
 * 
 * 1. **World Feature Generation**: Ensures that all configured and placed features (like special
 *    stone formations, flint deposits, or primitive structures) are properly written as JSON files
 *    during data generation, allowing them to appear in the game world
 * 
 * 2. **Biome Modifications**: Supports any biome adjustments that create a more Paleolithic
 *    atmosphere, such as altered vegetation distribution or specialized resource spawning
 * 
 * 3. **Resource Distribution**: Controls how primitive resources like flint, specific wood types,
 *    or other Paleolithic materials are distributed in the world, affecting progression pacing
 * 
 * 4. **Feature Balancing**: Enables fine-tuning of feature occurrence rates and conditions,
 *    ensuring that rare resources remain challenging to find while basic materials are accessible
 * 
 * This system helps create a world environment that supports the Paleolithic gameplay experience,
 * where resource gathering and environmental adaptation are key survival elements.
 */
class ModRegistryDataGenerator(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricDynamicRegistryProvider(output, registriesFuture) {

    /**
     * Configures which registry entries should be included in the generated JSON files.
     * 
     * This method adds all configured and placed features to the generation entries,
     * ensuring that all Paleolithic Era worldgen features are properly exported during
     * data generation. These features might include:
     * 
     * - Natural flint deposits that serve as critical early-game resources
     * - Special rock formations suitable for primitive mining techniques
     * - Distinctive tree distributions that provide appropriate wood types
     * - Environmental elements that reflect a Paleolithic landscape
     *
     * @param registries Registry wrapper lookup for accessing registries
     * @param entries Builder for collecting registry entries to be exported
     */
    override fun configure(
        registries: RegistryWrapper.WrapperLookup,
        entries: Entries
    ) {
        entries.addAll(registries.getOrThrow(RegistryKeys.CONFIGURED_FEATURE))
        entries.addAll(registries.getOrThrow(RegistryKeys.PLACED_FEATURE))
    }

    /**
     * Provides a descriptive name for this data generator in logs and reports.
     * 
     * The name clearly identifies that these generated files are related to the
     * Paleolithic Era mod's worldgen features, helping with debugging and organization
     * during the data generation process.
     *
     * @return The name of this data generator
     */
    override fun getName(): String = "Paleolithic Worldgen Features"
}