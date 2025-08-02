package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.entity.CrudeCampfireBlockEntity
import com.toolsandtaverns.paleolithicera.entity.HideDryerBlockEntity
import com.toolsandtaverns.paleolithicera.entity.KnappingStationBlockEntity
import com.toolsandtaverns.paleolithicera.entity.WoodenSpearEntity
import com.toolsandtaverns.paleolithicera.util.id
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier


object ModEntities {

    lateinit var KNAPPING_STATION: BlockEntityType<KnappingStationBlockEntity>
        private set
    lateinit var CRUDE_CAMPFIRE: BlockEntityType<CrudeCampfireBlockEntity>
        private set
    lateinit var SPEAR_ENTITY: EntityType<WoodenSpearEntity>
            private set
    lateinit var HIDE_DRYER_BLOCK_ENTITY: BlockEntityType<HideDryerBlockEntity>
        private set


    fun initialize() {
        KNAPPING_STATION = register(
            "knapping_station",
            ::KnappingStationBlockEntity,
            ModBlocks.KNAPPING_STATION
        )

        CRUDE_CAMPFIRE = register(
            name = "crude_campfire",
            entityFactory = ::CrudeCampfireBlockEntity,
            ModBlocks.CRUDE_CAMPFIRE
        )

        SPEAR_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            id("thrown_spear"),
            EntityType.Builder.create(::WoodenSpearEntity, SpawnGroup.MISC)
                .dimensions(0.5f, 0.5f)
                .maxTrackingRange(4)
                .trackingTickInterval(10)
                .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, id("thrown_spear")))
        )

        HIDE_DRYER_BLOCK_ENTITY = register(
            "hide_dryer",
            ::HideDryerBlockEntity,
            ModBlocks.HIDE_DRYER
            )

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

