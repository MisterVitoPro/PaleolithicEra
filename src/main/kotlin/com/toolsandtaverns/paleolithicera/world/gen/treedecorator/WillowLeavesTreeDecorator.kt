package com.toolsandtaverns.paleolithicera.world.gen.treedecorator

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.toolsandtaverns.paleolithicera.block.WillowLeafVinesBlock
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import net.minecraft.block.Blocks
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.world.gen.treedecorator.TreeDecorator
import net.minecraft.world.gen.treedecorator.TreeDecoratorType
import java.util.function.Consumer
import java.util.function.Function


class WillowLeavesTreeDecorator(private val probability: Float) : TreeDecorator() {
    override fun getType(): TreeDecoratorType<*>? {
        return ModTreeDecoratorType.WILLOW_LEAVES
    }

    override fun generate(generator: Generator) {
        val random = generator.random
        generator.leavesPositions.forEach(Consumer { pos: BlockPos? ->
            var blockPos: BlockPos
            if (random.nextFloat() < this.probability) {
                blockPos = pos!!.west()
                if (generator.isAir(blockPos)) {
                    placeVines(blockPos, WillowLeafVinesBlock.EAST, generator)
                }
            }

            if (random.nextFloat() < this.probability) {
                blockPos = pos!!.east()
                if (generator.isAir(blockPos)) {
                    placeVines(blockPos, WillowLeafVinesBlock.WEST, generator)
                }
            }

            if (random.nextFloat() < this.probability) {
                blockPos = pos!!.north()
                if (generator.isAir(blockPos)) {
                    placeVines(blockPos, WillowLeafVinesBlock.SOUTH, generator)
                }
            }
            if (random.nextFloat() < this.probability) {
                blockPos = pos!!.south()
                if (generator.isAir(blockPos)) {
                    placeVines(blockPos, WillowLeafVinesBlock.NORTH, generator)
                }
            }
        })
    }

    companion object {
        val CODEC: MapCodec<WillowLeavesTreeDecorator> = Codec.floatRange(0.0f, 1.0f).fieldOf("probability")
            .xmap<WillowLeavesTreeDecorator>(
                Function { probability: Float -> WillowLeavesTreeDecorator(probability) },
                Function { treeDecorator: WillowLeavesTreeDecorator -> treeDecorator.probability })

        /**
         * Places a vine at a given position and then up to 4 more vines going downwards.
         */
        private fun placeVines(pos: BlockPos, faceProperty: BooleanProperty?, generator: Generator) {
            var pos = pos
            generator.replace(pos, ModBlocks.WILLOW_LEAF_VINES.defaultState.with(faceProperty, true))
            var i = 5

            pos = pos.down()
            while (generator.isAir(pos) && i > 0) {
                generator.replace(pos, ModBlocks.WILLOW_LEAF_VINES.defaultState.with(faceProperty, true))
                pos = pos.down()
                --i
            }
        }
    }
}