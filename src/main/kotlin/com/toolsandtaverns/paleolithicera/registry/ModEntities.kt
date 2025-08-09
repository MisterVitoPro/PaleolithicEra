package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.entity.BoarEntity
import com.toolsandtaverns.paleolithicera.entity.CrudeBedBlockEntity
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
    lateinit var WOODEN_SPEAR_ENTITY: EntityType<WoodenSpearEntity>
            private set
    lateinit var HIDE_DRYER_BLOCK_ENTITY: BlockEntityType<HideDryerBlockEntity>
        private set

    val BOAR_ENTITY: EntityType<BoarEntity> by lazy {
        Registry.register(
            Registries.ENTITY_TYPE,
            id("boar"),
            EntityType.Builder.create(::BoarEntity, SpawnGroup.CREATURE)
                .dimensions(0.9f, 0.9f)
                .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, id("boar")))
        )
    }

    val CRUDE_BED: BlockEntityType<CrudeBedBlockEntity> = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        id("crude_bed"),
        FabricBlockEntityTypeBuilder
            .create(::CrudeBedBlockEntity, ModBlocks.CRUDE_BED)
            .build()  // ✅ Don't pass a type reference here!
    )

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

        WOODEN_SPEAR_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            id("wooden_spear"),
            EntityType.Builder.create(::WoodenSpearEntity, SpawnGroup.MISC)
                .dimensions(0.5f, 0.5f)
                .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, id("wooden_spear")))
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

