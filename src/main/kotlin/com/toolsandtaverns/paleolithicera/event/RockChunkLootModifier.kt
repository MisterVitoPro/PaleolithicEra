package com.toolsandtaverns.paleolithicera.event

import com.toolsandtaverns.paleolithicera.PaleolithicEra.LOGGER
import com.toolsandtaverns.paleolithicera.registry.ModItems.ROCK_CHUNK
import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.minecraft.block.Blocks
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.condition.RandomChanceLootCondition
import net.minecraft.loot.condition.SurvivesExplosionLootCondition
import net.minecraft.loot.condition.InvertedLootCondition
import net.minecraft.loot.condition.TableBonusLootCondition
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.entry.AlternativeEntry
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.Enchantments
import net.minecraft.registry.RegistryEntryLookup
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryKey

object RockChunkLootModifier {

    fun initialize() {
        LOGGER.info("RockChunkLootModifier initializing...")
        // Replace sand/gravel loot so rock chunks act like flint: if rock drops, no block item.
        LootTableEvents.REPLACE.register { id: RegistryKey<LootTable>, _, source, registries ->
            if (!source.isBuiltin) return@register null
            val sandKey = Blocks.SAND.lootTableKey.get().value
            val gravelKey = Blocks.GRAVEL.lootTableKey.get().value

            if (id.value == sandKey) {
                val pool = LootPool.builder()
                    .with(
                        AlternativeEntry.builder(
                            ItemEntry.builder(ROCK_CHUNK)
                                .conditionally(RandomChanceLootCondition.builder(0.20f)),
                            ItemEntry.builder(Blocks.SAND.asItem())
                        )
                    )
                    .conditionally(SurvivesExplosionLootCondition.builder())
                    .rolls(ConstantLootNumberProvider.create(1f))

                return@register LootTable.builder().pool(pool).build()
            }

            if (id.value == gravelKey) {
                // Lookups for enchantments
                val enchLookup: RegistryEntryLookup<Enchantment> = registries.getOrThrow(RegistryKeys.ENCHANTMENT)
                val fortuneEntry = enchLookup.getOrThrow(Enchantments.FORTUNE)
                val silkEntry = enchLookup.getOrThrow(Enchantments.SILK_TOUCH)

                // Pool A: If Silk Touch, always gravel; else rock chunk (20%) or gravel
                val poolA = LootPool.builder()
                    .with(
                        AlternativeEntry.builder(
                            ItemEntry.builder(Blocks.GRAVEL.asItem())
                                .conditionally(TableBonusLootCondition.builder(silkEntry, 0.0f, 1.0f)),
                            AlternativeEntry.builder(
                                ItemEntry.builder(ROCK_CHUNK)
                                    .conditionally(RandomChanceLootCondition.builder(0.20f)),
                                ItemEntry.builder(Blocks.GRAVEL.asItem())
                            )
                        )
                    )
                    .conditionally(SurvivesExplosionLootCondition.builder())
                    .rolls(ConstantLootNumberProvider.create(1f))

                // Pool B: vanilla flint chance (fortune-aware), suppressed by Silk Touch
                val poolB = LootPool.builder().with(
                    ItemEntry.builder(Items.FLINT)
                        .conditionally(InvertedLootCondition.builder(TableBonusLootCondition.builder(silkEntry, 0.0f, 1.0f)))
                        .conditionally(TableBonusLootCondition.builder(fortuneEntry, 0.10f, 0.14f, 0.25f))
                )
                    .conditionally(SurvivesExplosionLootCondition.builder())
                    .rolls(ConstantLootNumberProvider.create(1f))

                return@register LootTable.builder().pool(poolA).pool(poolB).build()
            }

            null
        }
    }
}
