package com.toolsandtaverns.paleolithicera.world.gen.treedecorator

import com.mojang.serialization.MapCodec
import com.toolsandtaverns.paleolithicera.PaleolithicEra.LOGGER
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.world.gen.treedecorator.TreeDecorator
import net.minecraft.world.gen.treedecorator.TreeDecoratorType

object ModTreeDecoratorType {

    val WILLOW_LEAVES: TreeDecoratorType<WillowLeavesTreeDecorator> =
        register("willow_leaves", WillowLeavesTreeDecorator.CODEC)

    fun initialize(){
        LOGGER.info("Registering Custom Tree Decorator Types")
    }

    private fun <P : TreeDecorator> register(id: String, codec: MapCodec<P>): TreeDecoratorType<P> {
        return Registry.register(
            Registries.TREE_DECORATOR_TYPE,
            id,
            TreeDecoratorType(codec))
    }

}