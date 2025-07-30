package com.toolsandtaverns.paleolithicera.datagen

import com.toolsandtaverns.paleolithicera.registry.ModEntityTags.HUNTABLE_TAG
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.data.tag.ProvidedTagBuilder
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

/**
 * Tag provider that generates entity_type tags such as `huntable`.
 */
class ModEntityTypeTagProvider(
    output: FabricDataOutput,
    registries: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricTagProvider<EntityType<*>>(output, RegistryKeys.ENTITY_TYPE, registries) {

    override fun configure(lookup: RegistryWrapper.WrapperLookup) {
        val entityTypeRegistry = lookup.getOrThrow(RegistryKeys.ENTITY_TYPE)
        val builder: ProvidedTagBuilder<RegistryKey<EntityType<*>>, EntityType<*>> = this.builder(HUNTABLE_TAG)

        entityTypeRegistry.streamEntries()
            .filter { huntableAnimals.contains(it.value()) }
            .forEach { entry ->
                val registryKey = entry.key.get()
                builder.add(registryKey)
            }
    }

    companion object {
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
            EntityType.PIG,
            EntityType.POLAR_BEAR,
            EntityType.RABBIT,
            EntityType.SHEEP,
            EntityType.WOLF,
        )
    }

}