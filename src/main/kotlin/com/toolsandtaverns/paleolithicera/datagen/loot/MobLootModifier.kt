package com.toolsandtaverns.paleolithicera.datagen.loot

import com.toolsandtaverns.paleolithicera.registry.ModEntityTags.HUNTABLE_TAG
import com.toolsandtaverns.paleolithicera.registry.ModItems.RAWHIDE
import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.fabricmc.fabric.api.loot.v3.LootTableSource
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.LootPool
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.util.Identifier
import net.minecraft.item.Items
import net.minecraft.loot.LootTable
import net.minecraft.registry.Registries

object MobLootModifier {

    fun initialize() {
        LootTableEvents.MODIFY.register { id, builder: LootTable.Builder, source: LootTableSource, _ ->
            if (!source.isBuiltin) return@register
            if (!id.value.path.startsWith("entities/")) return@register

            val entityId = Identifier.ofVanilla(id.value.path)
            val entityType = Registries.ENTITY_TYPE.get(entityId)

            if (entityType.isIn(HUNTABLE_TAG)) {
                // Add Raw Hide instead
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
