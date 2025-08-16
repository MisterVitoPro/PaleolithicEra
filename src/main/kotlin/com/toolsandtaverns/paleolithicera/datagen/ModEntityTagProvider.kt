package com.toolsandtaverns.paleolithicera.datagen

import com.toolsandtaverns.paleolithicera.registry.ModEntities
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.entity.EntityType
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture

/**
 * Tag provider that generates entity type tags for the Paleolithic Era mod,
 * defining which creatures are part of the primitive hunting ecosystem.
 *
 * The entity tagging system serves several important gameplay functions:
 *
 * 1. **Hunting Mechanics**: The `huntable` tag designates animals that can be hunted using
 *    primitive weapons like spears, providing appropriate targets for Paleolithic hunters
 *
 * 2. **Resource Drops**: Tagged entities may have special drops when killed with primitive
 *    weapons, such as hide, raw meat, or bone, reflecting realistic hunting outcomes
 *
 * 3. **Advancement Triggers**: Entity tags can be used in advancement criteria to track
 *    hunting achievements and progress in survival skills
 *
 * 4. **AI Behaviors**: Tagged entities may exhibit special behaviors when encountering players
 *    with primitive weapons, simulating more realistic predator-prey dynamics
 *
 * These entity classifications help create a more immersive Paleolithic hunting experience
 * that reflects historical realities of early human subsistence strategies.
 */
class ModEntityTypeTagProvider(
    output: FabricDataOutput,
    registries: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricTagProvider.EntityTypeTagProvider(output, registries) {

    /**
     * Configures the entity type tags for the Paleolithic Era mod.
     *
     * This method populates the huntable tag with all animals designated as appropriate
     * targets for primitive hunting techniques. The selection reflects animals that would
     * have been hunted by Paleolithic humans and provides suitable challenges and rewards
     * for players using primitive weapons.
     *
     * @param lookup Registry wrapper lookup for accessing entity type registries
     */
    override fun configure(lookup: RegistryWrapper.WrapperLookup) {
        val builder = builder(HUNTABLE_TAG)

        huntableAnimals.forEach { entityType ->
            val id: Identifier = EntityType.getId(entityType)
            val key: RegistryKey<EntityType<*>> = RegistryKey.of(RegistryKeys.ENTITY_TYPE, id)
            builder.add(key)
        }
    }

    companion object {
        /**
         * List of all animal entity types that can be hunted with primitive weapons.
         *
         * This selection represents animals that would have been targeted by Paleolithic hunters,
         * chosen based on several factors:
         *
         * 1. Historical accuracy - species that existed during the Paleolithic era
         * 2. Varied sizes - from small game (rabbits) to large prey (cows, horses)
         * 3. Different environments - representing hunting across various biomes
         * 4. Risk levels - some animals (wolves, polar bears) present danger to hunters
         * 5. Resource value - animals providing useful materials like hide, meat, and bone
         *
         * The diversity of huntable animals encourages players to develop different hunting
         * strategies and tools for different prey, reflecting the adaptive nature of
         * Paleolithic hunting practices across environments.
         */
        val huntableAnimals = listOf(
            EntityType.ARMADILLO,
            EntityType.CAMEL,
            EntityType.COW,
            EntityType.DONKEY,
            EntityType.FOX,
            EntityType.GOAT,
            EntityType.HORSE,
            EntityType.LLAMA,
            EntityType.MOOSHROOM,
            EntityType.MULE,
            EntityType.PANDA,
            EntityType.POLAR_BEAR,
            EntityType.RABBIT,
            EntityType.SHEEP,
            EntityType.WOLF,
            ModEntities.BOAR_ENTITY
        )
    }

}