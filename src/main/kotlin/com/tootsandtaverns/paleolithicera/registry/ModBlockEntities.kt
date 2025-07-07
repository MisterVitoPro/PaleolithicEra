package com.tootsandtaverns.paleolithicera.registry

import com.tootsandtaverns.paleolithicera.Constants.MOD_ID
import com.tootsandtaverns.paleolithicera.block.entity.KnappingStationBlockEntity
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier


object ModBlockEntities {

    lateinit var KNAPPING_STATION: BlockEntityType<KnappingStationBlockEntity>
        private set

    fun initialize() {
        KNAPPING_STATION = register(
        "knapping_station",
        ::KnappingStationBlockEntity,
        ModBlocks.KNAPPING_STATION)
    }

    private fun <T : BlockEntity> register(
        name: String,
        entityFactory: FabricBlockEntityTypeBuilder.Factory<out T>,
        vararg blocks: Block
    ): BlockEntityType<T> {
        val id = Identifier.of(MOD_ID, name)
        return Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            id,
            FabricBlockEntityTypeBuilder.create<T>(entityFactory, *blocks).build()
        )
    }
}

