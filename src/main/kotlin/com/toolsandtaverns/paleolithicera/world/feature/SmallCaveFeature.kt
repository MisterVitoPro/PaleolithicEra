package com.toolsandtaverns.paleolithicera.world.feature

import com.mojang.serialization.Codec
import net.minecraft.block.Blocks
import net.minecraft.block.BedBlock
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.Heightmap
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.util.FeatureContext
import org.slf4j.LoggerFactory
import kotlin.math.*

class SmallCaveFeature(codec: Codec<DefaultFeatureConfig>) : Feature<DefaultFeatureConfig>(codec) {
    
    companion object {
        private val LOGGER = LoggerFactory.getLogger("SmallCaveFeature")
    }

    override fun generate(context: FeatureContext<DefaultFeatureConfig>): Boolean {
        val world = context.world
        val pos = context.origin
        val random = context.random

        // Check if this is a good location for a cave (near surface)
        val surfaceY = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.x, pos.z) - 1
        if (pos.y < surfaceY - 5 || pos.y > surfaceY + 5) {
            return false
        }

        // Ensure we're inside or adjacent to a sizeable hill/mountain rather than flat surface
        if (!isInsideSizeableHillOrMountain(world, pos)) {
            return false
        }

        // Avoid generating inside or immediately adjacent to villages (heuristic block scan)
        if (isNearVillageBlocks(world, pos)) {
            return false
        }

        // Avoid other surface structures (temples, portals, monuments, etc.)
        if (isNearOtherStructureBlocks(world, pos)) {
            return false
        }

        // Ensure there is solid support under the entrance and initial chamber area
        if (!hasSolidSupport(world, pos)) {
            return false
        }

        // Check if we're in terrain that can support a cave (stone, dirt, etc.)
        val groundBlock = world.getBlockState(pos.down()).block
        if (groundBlock != Blocks.STONE && groundBlock != Blocks.DEEPSLATE && 
            groundBlock != Blocks.DIRT && groundBlock != Blocks.GRASS_BLOCK &&
            groundBlock != Blocks.ANDESITE && groundBlock != Blocks.GRANITE &&
            groundBlock != Blocks.DIORITE) {
            LOGGER.info("❌ Cave rejected - unsuitable ground block: ${groundBlock.translationKey}")
            return false
        }

        LOGGER.info("✅ GENERATING SMALL CAVE at ${pos.x}, ${pos.y}, ${pos.z} (Surface Y: $surfaceY)")
        generateCave(world, pos, random)
        LOGGER.info("🎉 Small cave generation completed successfully!")
        return true
    }

    private fun isNearVillageBlocks(world: StructureWorldAccess, origin: BlockPos): Boolean {
        val radius = 20
        val minY = origin.y - 4
        val maxY = origin.y + 6

        fun isVillageBlock(blockPos: BlockPos): Boolean {
            val state = world.getBlockState(blockPos)
            val block = state.block
            if (block is BedBlock) return true
            return block == Blocks.BELL ||
                block == Blocks.COMPOSTER ||
                block == Blocks.CARTOGRAPHY_TABLE ||
                block == Blocks.BLAST_FURNACE ||
                block == Blocks.SMOKER ||
                block == Blocks.LECTERN ||
                block == Blocks.STONECUTTER ||
                block == Blocks.GRINDSTONE ||
                block == Blocks.BARREL ||
                block == Blocks.CAULDRON ||
                block == Blocks.FLETCHING_TABLE ||
                block == Blocks.HAY_BLOCK ||
                block == Blocks.DIRT_PATH
        }

        for (dx in -radius..radius) {
            for (dz in -radius..radius) {
                // Simple circle check to reduce iterations slightly
                if (dx * dx + dz * dz > radius * radius) continue
                for (y in minY..maxY) {
                    val checkPos = BlockPos(origin.x + dx, y, origin.z + dz)
                    if (isVillageBlock(checkPos)) return true
                }
            }
        }
        return false
    }

    private fun isNearOtherStructureBlocks(world: StructureWorldAccess, origin: BlockPos): Boolean {
        val radius = 20
        val minY = origin.y - 6
        val maxY = origin.y + 10

        fun isStructureMarker(blockPos: BlockPos): Boolean {
            val b = world.getBlockState(blockPos).block
            // Ruined portal markers
            if (b == Blocks.CRYING_OBSIDIAN || b == Blocks.NETHERRACK || b == Blocks.MAGMA_BLOCK || b == Blocks.GOLD_BLOCK) return true
            // Desert/Jungle temple markers
            if (b == Blocks.CHISELED_SANDSTONE || b == Blocks.CUT_SANDSTONE || b == Blocks.ORANGE_TERRACOTTA || b == Blocks.MOSSY_COBBLESTONE) return true
            // Stronghold/stone structure markers
            if (b == Blocks.STONE_BRICKS || b == Blocks.MOSSY_STONE_BRICKS || b == Blocks.CRACKED_STONE_BRICKS || b == Blocks.CHISELED_STONE_BRICKS) return true
            // Ocean structures
            if (b == Blocks.PRISMARINE || b == Blocks.DARK_PRISMARINE || b == Blocks.SEA_LANTERN) return true
            return false
        }

        for (dx in -radius..radius) {
            for (dz in -radius..radius) {
                if (dx * dx + dz * dz > radius * radius) continue
                for (y in minY..maxY) {
                    val checkPos = BlockPos(origin.x + dx, y, origin.z + dz)
                    if (isStructureMarker(checkPos)) return true
                }
            }
        }
        return false
    }

    private fun hasSolidSupport(world: StructureWorldAccess, origin: BlockPos): Boolean {
        // Check that the blocks directly beneath the planned entrance (z = -2..-1)
        // and the initial chamber floor (z = 0..2) are NOT air. This prevents
        // caves generating over sheer cliffs or floating edges.
        for (x in -2..2) {
            // Entrance support
            for (z in -2..-1) {
                val below = origin.add(x, -1, z)
                if (world.getBlockState(below).isAir) return false
            }
            // Initial chamber support
            for (z in 0..2) {
                val below = origin.add(x, -1, z)
                if (world.getBlockState(below).isAir) return false
            }
        }
        return true
    }

    private fun isInsideSizeableHillOrMountain(world: StructureWorldAccess, origin: BlockPos): Boolean {
        val surfaceAtOrigin = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, origin.x, origin.z) - 1

        // Sample surrounding surface heights to measure relief
        val sampleOffsets = listOf(
            // close ring (radius ~6)
            Pair(0, 6), Pair(6, 0), Pair(0, -6), Pair(-6, 0),
            Pair(4, 4), Pair(-4, 4), Pair(4, -4), Pair(-4, -4),
            // far ring (radius ~10)
            Pair(0, 10), Pair(10, 0), Pair(0, -10), Pair(-10, 0),
            Pair(7, 7), Pair(-7, 7), Pair(7, -7), Pair(-7, -7)
        )

        var maxSurface = surfaceAtOrigin
        var minSurface = surfaceAtOrigin
        var higherCount = 0
        for ((dx, dz) in sampleOffsets) {
            val sx = origin.x + dx
            val sz = origin.z + dz
            val sY = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, sx, sz) - 1
            if (sY > maxSurface) maxSurface = sY
            if (sY < minSurface) minSurface = sY
            if (sY >= surfaceAtOrigin + 6) higherCount++
        }

        val localRelief = maxSurface - minSurface

        // Ensure forward (+Z) direction actually climbs so the cave can go into a hill
        var forwardMaxDelta = 0
        for (dz in 3..10) {
            for (dx in -2..2) {
                val sY = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, origin.x + dx, origin.z + dz) - 1
                val delta = sY - surfaceAtOrigin
                if (delta > forwardMaxDelta) forwardMaxDelta = delta
            }
        }

        val sizeableHill = localRelief >= 10 && higherCount >= 4 && forwardMaxDelta >= 6
        return sizeableHill
    }

    private fun generateCave(world: StructureWorldAccess, centerPos: BlockPos, random: Random) {
        val caveWidth = 3 + random.nextInt(2) // 3-4 blocks wide (reduced for chunk bounds)
        val caveDepth = 8 + random.nextInt(5) // 8-12 blocks deep (reduced for chunk bounds)
        val caveHeight = 4 + random.nextInt(2) // 4-5 blocks high
        
        LOGGER.info("Cave dimensions: Width=${caveWidth}, Depth=${caveDepth}, Height=${caveHeight}")

        // Create the main cave chamber - stay within chunk bounds
        for (x in -caveWidth..caveWidth) {
            for (z in 0..caveDepth) {
                for (y in 0..caveHeight) {
                    val currentPos = centerPos.add(x, y, z)
                    
                    // Ensure we stay within reasonable chunk bounds (8 blocks from center)
                    if (abs(currentPos.x - centerPos.x) > 7 || abs(currentPos.z - centerPos.z) > 7) {
                        continue
                    }
                    
                    // Create an oval/elliptical cave shape
                    val distanceX = abs(x).toDouble() / caveWidth
                    val distanceZ = z.toDouble() / caveDepth
                    val distanceY = y.toDouble() / caveHeight
                    
                    // Use elliptical distance formula
                    val distance = sqrt(distanceX * distanceX + distanceZ * distanceZ + distanceY * distanceY * 0.5)
                    
                    if (distance <= 1.0) {
                        // Clear the space for the cave
                        world.setBlockState(currentPos, Blocks.AIR.defaultState, 3)
                        
                        // Add some floor variation
                        if (y == 0 && random.nextFloat() < 0.3f) {
                            world.setBlockState(currentPos.down(), Blocks.COBBLESTONE.defaultState, 3)
                        }
                    }
                }
            }
        }

        // Create entrance area (3-4 blocks tall, short tunnel into the cave)
        val entranceHeight = 3 + random.nextInt(2) // 3-4 blocks tall
        val entranceLength = 2 // Short tunnel length
        for (x in -2..2) {
            for (z in -entranceLength..-1) {
                for (y in 0 until entranceHeight) {
                    val entrancePos = centerPos.add(x, y, z)

                    // Ensure entrance stays within bounds
                    if (abs(entrancePos.x - centerPos.x) <= 7 && abs(entrancePos.z - centerPos.z) <= 7) {
                        world.setBlockState(entrancePos, Blocks.AIR.defaultState, 3)
                    }
                }
            }
        }

        // Add a simple natural roof: stone layer with dirt above it
        addRoofLayer(world, centerPos, caveWidth, caveDepth, caveHeight)

        // Add some natural cave decorations
        addCaveDecorations(world, centerPos, caveWidth, caveDepth, caveHeight, random)
    }

    private fun addRoofLayer(
        world: StructureWorldAccess,
        centerPos: BlockPos,
        caveWidth: Int,
        caveDepth: Int,
        caveHeight: Int
    ) {
        val stoneY = caveHeight + 1
        val dirtY = caveHeight + 2
        for (x in -caveWidth..caveWidth) {
            for (z in 0..caveDepth) {
                val stonePos = centerPos.add(x, stoneY, z)
                val dirtPos = centerPos.add(x, dirtY, z)

                if (abs(stonePos.x - centerPos.x) <= 7 && abs(stonePos.z - centerPos.z) <= 7) {
                    // Place stone if there is air where the roof should be
                    if (world.getBlockState(stonePos).isAir) {
                        world.setBlockState(stonePos, Blocks.STONE.defaultState, 3)
                    }
                }

                if (abs(dirtPos.x - centerPos.x) <= 7 && abs(dirtPos.z - centerPos.z) <= 7) {
                    // Place dirt above stone to mimic natural topsoil if air
                    if (world.getBlockState(dirtPos).isAir) {
                        world.setBlockState(dirtPos, Blocks.DIRT.defaultState, 3)
                    }
                }
            }
        }
    }

    private fun addCaveDecorations(
        world: StructureWorldAccess,
        centerPos: BlockPos,
        caveWidth: Int,
        caveDepth: Int,
        caveHeight: Int,
        random: Random
    ) {
        // Add some cobblestone patches on walls
        for (i in 0..random.nextInt(2) + 1) { // Reduced decoration count
            val x = random.nextInt(caveWidth * 2 + 1) - caveWidth
            val z = random.nextInt(caveDepth + 1)
            val y = random.nextInt(caveHeight + 1)
            val pos = centerPos.add(x, y, z)
            
            // Ensure decoration stays within bounds
            if (abs(pos.x - centerPos.x) > 7 || abs(pos.z - centerPos.z) > 7) {
                continue
            }
            
            // Check if this is near a wall
            if (world.getBlockState(pos).isAir && 
                (!world.getBlockState(pos.up()).isAir || 
                 !world.getBlockState(pos.down()).isAir ||
                 !world.getBlockState(pos.north()).isAir ||
                 !world.getBlockState(pos.south()).isAir ||
                 !world.getBlockState(pos.east()).isAir ||
                 !world.getBlockState(pos.west()).isAir)) {
                
                if (random.nextFloat() < 0.6f) {
                    world.setBlockState(pos, Blocks.COBBLESTONE.defaultState, 3)
                } else if (random.nextFloat() < 0.3f) {
                    world.setBlockState(pos, Blocks.MOSSY_COBBLESTONE.defaultState, 3)
                }
            }
        }

        // Add some water drips (water source blocks near ceiling)
        for (i in 0..random.nextInt(1) + 1) { // Reduced water count
            val x = random.nextInt(caveWidth * 2 + 1) - caveWidth
            val z = random.nextInt(caveDepth + 1)
            val y = caveHeight - 1
            val pos = centerPos.add(x, y, z)
            
            // Ensure water stays within bounds
            if (abs(pos.x - centerPos.x) > 7 || abs(pos.z - centerPos.z) > 7) {
                continue
            }
            
            if (world.getBlockState(pos).isAir && !world.getBlockState(pos.up()).isAir) {
                if (random.nextFloat() < 0.4f) {
                    world.setBlockState(pos, Blocks.WATER.defaultState, 3)
                }
            }
        }
    }
}
