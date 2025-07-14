package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.block.CrudeCampFireBlock
import com.toolsandtaverns.paleolithicera.block.KnappingStationBlock
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.MapColor
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.state.property.Properties
import net.minecraft.util.Identifier


object ModBlocks {

    val KNAPPING_STATION: Block = register("knapping_station", ::KnappingStationBlock, AbstractBlock.Settings.create())
    val CRUDE_CAMPFIRE: Block = register(
        "crude_campfire", ::CrudeCampFireBlock,
        AbstractBlock.Settings.create()
            .mapColor(MapColor.TERRACOTTA_BROWN)
            .strength(1.0f)
            .nonOpaque()
            .pistonBehavior(PistonBehavior.DESTROY)
            .sounds(BlockSoundGroup.WOOD)
            .luminance { state -> if (state.get(Properties.LIT)) 15 else 0 })

    fun initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
            .register { entries: FabricItemGroupEntries ->
                entries.add(KNAPPING_STATION)
            }
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
            .register { entries: FabricItemGroupEntries ->
                entries.add(CRUDE_CAMPFIRE)
            }
    }

    private fun register(
        name: String,
        blockFactory: (AbstractBlock.Settings) -> Block,
        settings: AbstractBlock.Settings,
        shouldRegisterItem: Boolean = true
    ): Block {
        // Create a registry key for the block
        val blockKey = keyOfBlock(name)
        // Create the block instance
        val block: Block? = blockFactory(settings.registryKey(blockKey))

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:moving_piston` or `minecraft:end_gateway`
        if (shouldRegisterItem) {
            // Items need to be registered with a different type of registry key, but the ID
            // can be the same.
            val itemKey = keyOfItem(name)

            val blockItem = BlockItem(block, Item.Settings().registryKey(itemKey))
            Registry.register(Registries.ITEM, itemKey, blockItem)
        }

        return Registry.register<Block, Block>(Registries.BLOCK, blockKey, block)
    }

    private fun keyOfBlock(name: String): RegistryKey<Block>? {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, name))
    }

    private fun keyOfItem(name: String): RegistryKey<Item> {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name))
    }

}
