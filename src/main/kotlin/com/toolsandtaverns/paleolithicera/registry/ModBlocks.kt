package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.block.*
import com.toolsandtaverns.paleolithicera.registry.custom.EdiblePlants
import com.toolsandtaverns.paleolithicera.util.RegistryHelpers
import com.toolsandtaverns.paleolithicera.util.id
import com.toolsandtaverns.paleolithicera.util.regKeyOfItem
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
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.state.property.Properties


object ModBlocks {

    val CRUDE_BED: Block = register(
        "crude_bed",
        ::CrudeBedBlock,
        AbstractBlock.Settings.create().sounds(BlockSoundGroup.WOOD).strength(0.2F).nonOpaque().burnable()
            .pistonBehavior(PistonBehavior.DESTROY)
    )
    val CRUDE_CAMPFIRE: Block = register(
        "crude_campfire", ::CrudeCampFireBlock,
        AbstractBlock.Settings.create()
            .mapColor(MapColor.TERRACOTTA_BROWN)
            .strength(1.0f)
            .nonOpaque()
            .pistonBehavior(PistonBehavior.DESTROY)
            .sounds(BlockSoundGroup.WOOD)
            .luminance { state -> if (state.get(Properties.LIT)) 15 else 0 })

    val ELDERBERRY_BUSH: Block =
        registerHerbPlantBlockWithoutItem("elderberry_bush", ModItems.EDIBLE_PLANTS[EdiblePlants.ELDERBERRY]!!)
    val CHAMOMILE_PLANT: Block =
        registerHerbPlantBlockWithoutItem("chamomile_plant", ModItems.EDIBLE_PLANTS[EdiblePlants.CHAMOMILE]!!)
    val YARROW_PLANT: Block =
        registerHerbPlantBlockWithoutItem("yarrow_plant", ModItems.EDIBLE_PLANTS[EdiblePlants.YARROW]!!)
    val WILD_GARLIC_PLANT: Block =
        registerHerbPlantBlockWithoutItem("wild_garlic_plant", ModItems.EDIBLE_PLANTS[EdiblePlants.WILD_GARLIC]!!)
    val EPHEDRA_PLANT: Block =
        registerHerbPlantBlockWithoutItem("ephedra_plant", ModItems.EDIBLE_PLANTS[EdiblePlants.EPHEDRA]!!)
    val SAGEBRUSH_PLANT: Block =
        registerHerbPlantBlockWithoutItem("sagebrush_plant", ModItems.EDIBLE_PLANTS[EdiblePlants.SAGEBRUSH]!!)
    val WILD_MINT_PLANT: Block =
        registerHerbPlantBlockWithoutItem("wild_mint_plant", ModItems.EDIBLE_PLANTS[EdiblePlants.WILD_MINT]!!)
    val WILD_GINGER_PLANT: Block =
        registerHerbPlantBlockWithoutItem("wild_ginger_plant", ModItems.EDIBLE_PLANTS[EdiblePlants.WILD_GINGER]!!)

    val HIDE_DRYER: Block =
        register("hide_dryer", ::HideDryerBlock, AbstractBlock.Settings.create().strength(1.8f, 2.0f))
    val KNAPPING_STATION: Block =
        register("knapping_station", ::KnappingStationBlock, AbstractBlock.Settings.create().strength(1.8f, 2.0f))


    fun initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
            .register { entries: FabricItemGroupEntries ->
                entries.add(CRUDE_BED)
                entries.add(CRUDE_CAMPFIRE)
                entries.add(HIDE_DRYER)
                entries.add(KNAPPING_STATION)
            }
    }

    private fun register(
        name: String,
        blockFactory: (AbstractBlock.Settings) -> Block,
        settings: AbstractBlock.Settings,
        shouldRegisterItem: Boolean = true
    ): Block {
        // Create a registry key for the block
        val blockKey = RegistryHelpers.regKeyOfBlock(name)
        // Create the block instance
        val block: Block? = blockFactory(settings.registryKey(blockKey))

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:moving_piston` or `minecraft:end_gateway`
        if (shouldRegisterItem) {
            // Items need to be registered with a different type of registry key, but the ID
            // can be the same.
            val itemKey = regKeyOfItem(name)

            val blockItem = BlockItem(block, Item.Settings().registryKey(itemKey))
            Registry.register(Registries.ITEM, itemKey, blockItem)
        }

        return Registry.register<Block, Block>(Registries.BLOCK, blockKey, block)
    }

    private fun registerBlockWithoutBlockItem(
        name: String,
        additionalSettings: (AbstractBlock.Settings) -> Block
    ): Block {
        return Registry.register(
            Registries.BLOCK, id(name),
            additionalSettings(AbstractBlock.Settings.create().registryKey(RegistryHelpers.regKeyOfBlock(name)))
        )
    }

    private fun registerHerbPlantBlockWithoutItem(id: String, edibleItem: Item): Block {
        return registerBlockWithoutBlockItem(id) { settings: AbstractBlock.Settings ->
            EdiblePlantBlock(
                settings
                    .mapColor(MapColor.DARK_GREEN)
                    .ticksRandomly()
                    .noCollision()
                    .sounds(BlockSoundGroup.SWEET_BERRY_BUSH)
                    .pistonBehavior(PistonBehavior.DESTROY),
                edibleItem
            )
        }
    }


}
