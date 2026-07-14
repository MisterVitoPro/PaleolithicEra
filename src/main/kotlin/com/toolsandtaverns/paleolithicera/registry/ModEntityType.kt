package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.entity.BoarEntity
import com.toolsandtaverns.paleolithicera.entity.BoneSpearEntity
import com.toolsandtaverns.paleolithicera.entity.CrudeCampfireBlockEntity
import com.toolsandtaverns.paleolithicera.entity.EffigyOfProtectionEntity
import com.toolsandtaverns.paleolithicera.entity.FoodDryerBlockEntity
import com.toolsandtaverns.paleolithicera.entity.HideDryerBlockEntity
import com.toolsandtaverns.paleolithicera.entity.IbexEntity
import com.toolsandtaverns.paleolithicera.entity.KnappingStationBlockEntity
import com.toolsandtaverns.paleolithicera.entity.PebbleEntity
import com.toolsandtaverns.paleolithicera.entity.GroundStorageBlockEntity
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


object ModEntityType {

    lateinit var KNAPPING_STATION: BlockEntityType<KnappingStationBlockEntity>
        private set
    lateinit var CRUDE_CAMPFIRE: BlockEntityType<CrudeCampfireBlockEntity>
        private set
    lateinit var WOODEN_SPEAR_ENTITY: EntityType<WoodenSpearEntity>
        private set
    lateinit var BONE_SPEAR_ENTITY: EntityType<BoneSpearEntity>
        private set
    lateinit var PEBBLE_ENTITY: EntityType<PebbleEntity>
        private set
    lateinit var HIDE_DRYER_BLOCK_ENTITY: BlockEntityType<HideDryerBlockEntity>
        private set
    lateinit var EFFIGY_OF_PROTECTION_BLOCK_ENTITY: BlockEntityType<EffigyOfProtectionEntity>
        private set
    lateinit var FOOD_DRYER_BLOCK_ENTITY: BlockEntityType<FoodDryerBlockEntity>
        private set
    lateinit var GROUND_STORAGE_BLOCK_ENTITY: BlockEntityType<com.toolsandtaverns.paleolithicera.entity.GroundStorageBlockEntity>
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


    val IBEX_ENTITY: EntityType<IbexEntity> by lazy {
        Registry.register(
            Registries.ENTITY_TYPE,
            id("ibex"),
            EntityType.Builder.create(::IbexEntity, SpawnGroup.CREATURE)
                .dimensions(0.9f, 1.1f)
                .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, id("ibex")))
        )
    }

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

        BONE_SPEAR_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            id("bone_spear"),
            EntityType.Builder.create(::BoneSpearEntity, SpawnGroup.MISC)
                .dimensions(0.5f, 0.5f)
                .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, id("bone_spear")))
        )

        PEBBLE_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            id("pebble"),
            EntityType.Builder.create(::PebbleEntity, SpawnGroup.MISC)
                .dimensions(0.25f, 0.25f)
                .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, id("pebble")))
        )

        HIDE_DRYER_BLOCK_ENTITY = register(
            "hide_dryer",
            ::HideDryerBlockEntity,
            ModBlocks.HIDE_DRYER
        )

        EFFIGY_OF_PROTECTION_BLOCK_ENTITY = register(
            "effigy_of_protection",
            ::EffigyOfProtectionEntity,
            ModBlocks.EFFIGY_OF_PROTECTION
        )

        FOOD_DRYER_BLOCK_ENTITY = register(
            "food_dryer",
            ::FoodDryerBlockEntity,
            ModBlocks.FOOD_DRYER
        )

        GROUND_STORAGE_BLOCK_ENTITY = register(
            "ground_storage",
            ::GroundStorageBlockEntity,
            ModBlocks.GROUND_STORAGE
        )

    }

    private fun <T : BlockEntity> register(
        name: String,
        entityFactory: FabricBlockEntityTypeBuilder.Factory<out T>,
        vararg blocks: Block
    ): BlockEntityType<T> {
        val id = id(name)
        return Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            id,
            FabricBlockEntityTypeBuilder.create<T>(entityFactory, *blocks).build()
        )
    }
}

