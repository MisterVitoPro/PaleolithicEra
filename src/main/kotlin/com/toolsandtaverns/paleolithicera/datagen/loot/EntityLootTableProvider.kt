package com.toolsandtaverns.paleolithicera.datagen.loot

import com.toolsandtaverns.paleolithicera.registry.ModEntityType
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricEntityLootTableProvider
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.condition.EntityPropertiesLootCondition
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class EntityLootTableProvider(
    output: FabricDataOutput,
    registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricEntityLootTableProvider(output, registryLookup) {

    override fun generate() {
        register(
            ModEntityType.BOAR_ENTITY, LootTable.builder()
                .pool(
                    LootPool.builder()
                        .rolls(UniformLootNumberProvider.create(1f, 2f))
                        .with(ItemEntry.builder(Items.PORKCHOP))
                        .conditionally(EntityPropertiesLootCondition.create(LootContext.EntityTarget.THIS))
                )
        )
    }


}
