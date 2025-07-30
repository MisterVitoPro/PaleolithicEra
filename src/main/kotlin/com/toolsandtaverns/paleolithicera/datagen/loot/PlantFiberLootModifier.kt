package com.toolsandtaverns.paleolithicera.datagen.loot

import com.toolsandtaverns.paleolithicera.Constants
import com.toolsandtaverns.paleolithicera.util.id
import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.minecraft.item.Item
import net.minecraft.loot.LootPool
import net.minecraft.loot.condition.MatchToolLootCondition
import net.minecraft.loot.condition.RandomChanceLootCondition
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.predicate.item.ItemPredicate
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryEntryLookup
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

object PlantFiberLootModifier {

    val listOfBlocksForDrops: List<Identifier> = listOf(
        "blocks/tall_grass",
        "blocks/large_fern",
        "blocks/short_grass",
        "blocks/short_dry_grass",
        "blocks/tall_dry_grass",
        )
        .map { Identifier.ofVanilla( it) }
        .toList()

    fun initialize() {
        LootTableEvents.MODIFY.register { id, tableBuilder, _, registryLookup ->
            if (listOfBlocksForDrops.contains(id.value)) {
                val itemLookup: RegistryEntryLookup<Item> = registryLookup.getOrThrow(RegistryKeys.ITEM)
                val plantFiber = Registries.ITEM.get(id( "plant_fiber"))
                val knifeTag = TagKey.of(RegistryKeys.ITEM, id("knives"))
                val fiberDrop = ItemEntry.builder(plantFiber)
                    .conditionally(
                        MatchToolLootCondition.builder(
                            ItemPredicate.Builder.create().tag(itemLookup, knifeTag)
                        )
                    )
                    .conditionally(RandomChanceLootCondition.builder(0.4f))

                val fiberPool = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1f))
                    .bonusRolls(ConstantLootNumberProvider.create(0f))
                    .with(fiberDrop)

                tableBuilder.pool(fiberPool)
            }
        }
    }
}