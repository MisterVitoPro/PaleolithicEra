package com.toolsandtaverns.paleolithicera.event

import com.toolsandtaverns.paleolithicera.datagen.ModEntityTypeTagProvider.Companion.huntableAnimals
import com.toolsandtaverns.paleolithicera.registry.ModItems.RAWHIDE
import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.fabricmc.fabric.api.loot.v3.LootTableSource
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

object MobLootModifier {

    fun initialize() {
        LootTableEvents.MODIFY.register { id, builder: LootTable.Builder, source: LootTableSource, _ ->
            if (!source.isBuiltin) return@register
            if (!id.value.path.startsWith("entities/")) return@register

            val entityName = id.value.path.removePrefix("entities/")
            val entityId = Identifier.ofVanilla(entityName)
            val entityType = Registries.ENTITY_TYPE.get(entityId)

            println("Huntable=${huntableAnimals.contains(entityType)}")
            if (huntableAnimals.contains(entityType)) {
                builder.pool(
                    LootPool.builder()
                        .with(ItemEntry.builder(RAWHIDE))
                        .rolls(ConstantLootNumberProvider.create(0.5f)) // 50% chance
                )
                    .pool(
                        LootPool.builder()
                            .with(ItemEntry.builder(Items.BONE))
                            .rolls(ConstantLootNumberProvider.create(0.6f)) // 60% chance
                    )
            }
        }
    }

}
