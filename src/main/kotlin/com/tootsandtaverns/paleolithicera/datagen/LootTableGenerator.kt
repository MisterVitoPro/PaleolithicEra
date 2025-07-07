package com.tootsandtaverns.paleolithicera.datagen

import com.tootsandtaverns.paleolithicera.registry.ModBlocks
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.block.Block
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class LootTableGenerator(
    output: FabricDataOutput,
    registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricBlockLootTableProvider(output, registryLookup) {

    override fun generate() {
        // Default drop behavior: drop itself
        addDrop(ModBlocks.KNAPPING_STATION)
    }
}
