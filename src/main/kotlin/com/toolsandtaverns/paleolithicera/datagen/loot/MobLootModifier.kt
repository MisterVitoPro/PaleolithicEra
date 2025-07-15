package com.toolsandtaverns.paleolithicera.datagen.loot

import com.toolsandtaverns.paleolithicera.PaleolithicEra.LOGGER
import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.minecraft.entity.EntityType
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.LootPool
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.util.Identifier
import net.minecraft.item.Items
import net.minecraft.loot.LootTable
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey

object MobLootModifier {
    fun initialize() {
        LootTableEvents.MODIFY.register { id: RegistryKey<LootTable>, builder, source, registries ->
            if (!source.isBuiltin) return@register

            // Loot tables for mobs follow this path
            if (!id.value.path.startsWith("entities/")) return@register

            // Extract entity ID
            val entityId = Identifier.ofVanilla(id.value.path)

            // Look up entity type
            val entityType: EntityType<*> = Registries.ENTITY_TYPE.get(entityId)

            LOGGER.info("ENTITY:${entityId.path}")
            LOGGER.info("ENTITY:${entityId}")
            LOGGER.info("TYPE:${entityType.spawnGroup}")
            LOGGER.info("CLASS:${entityType.javaClass}")

            if (entityType.spawnGroup.isPeaceful) {
                LOGGER.info("Adding bone drop to: $id")
                builder.pool(
                    LootPool.builder()
                        .with(ItemEntry.builder(Items.BONE))
                        .rolls(ConstantLootNumberProvider.create(1f))
                        .build()
                )
            }
        }
    }
}
