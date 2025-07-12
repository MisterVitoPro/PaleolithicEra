package com.toolsandtaverns.paleolithicera.block.entity

import com.mojang.logging.LogUtils
import com.toolsandtaverns.paleolithicera.registry.ModBlockEntities
import net.minecraft.block.BlockState
import net.minecraft.block.CampfireBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.component.ComponentMap
import net.minecraft.component.ComponentsAccess
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.ContainerComponent
import net.minecraft.entity.LivingEntity
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.particle.ParticleTypes
import net.minecraft.recipe.CampfireCookingRecipe
import net.minecraft.recipe.RecipeEntry
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.ServerRecipeManager.MatchGetter
import net.minecraft.recipe.input.SingleStackRecipeInput
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.server.world.ServerWorld
import net.minecraft.storage.NbtWriteView
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.util.Clearable
import net.minecraft.util.ErrorReporter
import net.minecraft.util.ItemScatterer
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import org.slf4j.Logger
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import kotlin.math.min

class CrudeCampfireBlockEntity(pos: BlockPos, state: BlockState?) : BlockEntity(ModBlockEntities.CRUDE_CAMPFIRE, pos, state),
    Clearable {
    val itemsBeingCooked: DefaultedList<ItemStack?> = DefaultedList.ofSize<ItemStack?>(4, ItemStack.EMPTY)
    private val cookingTimes: IntArray = IntArray(4)
    private val cookingTotalTimes: IntArray = IntArray(4)

    override fun readData(view: ReadView) {
        super.readData(view)
        this.itemsBeingCooked.clear()
        Inventories.readData(view, this.itemsBeingCooked)
        view.getOptionalIntArray("CookingTimes").ifPresentOrElse(Consumer { `is`: IntArray? ->
            System.arraycopy(
                `is`,
                0,
                this.cookingTimes,
                0,
                min(this.cookingTotalTimes.size, `is`!!.size)
            )
        }, Runnable { Arrays.fill(this.cookingTimes, 0) })
        view.getOptionalIntArray("CookingTotalTimes").ifPresentOrElse(Consumer { `is`: IntArray? ->
            System.arraycopy(
                `is`,
                0,
                this.cookingTotalTimes,
                0,
                min(this.cookingTotalTimes.size, `is`!!.size)
            )
        }, Runnable { Arrays.fill(this.cookingTotalTimes, 0) })
    }

    override fun writeData(view: WriteView) {
        super.writeData(view)
        Inventories.writeData(view, this.itemsBeingCooked, true)
        view.putIntArray("CookingTimes", this.cookingTimes)
        view.putIntArray("CookingTotalTimes", this.cookingTotalTimes)
    }

    override fun toUpdatePacket(): BlockEntityUpdateS2CPacket? {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(registries: WrapperLookup): NbtCompound? {
        ErrorReporter.Logging(this.reporterContext, LOGGER).use { logging ->
            val nbtWriteView = NbtWriteView.create(logging, registries)
            Inventories.writeData(nbtWriteView, this.itemsBeingCooked, true)
            return nbtWriteView.nbt
        }
    }

    fun addItem(world: ServerWorld, entity: LivingEntity?, stack: ItemStack): Boolean {
        for (i in this.itemsBeingCooked.indices) {
            val itemStack = this.itemsBeingCooked[i]
            if (itemStack.isEmpty) {
                val optional = world.recipeManager
                    .getFirstMatch<SingleStackRecipeInput?, CampfireCookingRecipe?>(
                        RecipeType.CAMPFIRE_COOKING,
                        SingleStackRecipeInput(stack),
                        world
                    )
                if (optional.isEmpty) {
                    return false
                }

                this.cookingTotalTimes[i] =
                    ((optional.get() as RecipeEntry<*>).value() as CampfireCookingRecipe).cookingTime
                this.cookingTimes[i] = 0
                this.itemsBeingCooked[i] = stack.splitUnlessCreative(1, entity)
                world.emitGameEvent(
                    GameEvent.BLOCK_CHANGE,
                    this.getPos(),
                    GameEvent.Emitter.of(entity, this.cachedState)
                )
                this.updateListeners()
                return true
            }
        }

        return false
    }

    private fun updateListeners() {
        this.markDirty()
        this.getWorld()!!.updateListeners(this.getPos(), this.cachedState, this.cachedState, 3)
    }

    override fun clear() {
        this.itemsBeingCooked.clear()
    }

    override fun onBlockReplaced(pos: BlockPos, oldState: BlockState?) {
        if (this.world != null) {
            ItemScatterer.spawn(this.world, pos, this.itemsBeingCooked)
        }
    }

    override fun readComponents(components: ComponentsAccess) {
        super.readComponents(components)
        (components.getOrDefault<ContainerComponent?>(
            DataComponentTypes.CONTAINER,
            ContainerComponent.DEFAULT
        ) as ContainerComponent).copyTo(
            this.itemsBeingCooked
        )
    }

    override fun addComponents(builder: ComponentMap.Builder) {
        super.addComponents(builder)
        builder.add<ContainerComponent?>(
            DataComponentTypes.CONTAINER,
            ContainerComponent.fromStacks(this.itemsBeingCooked)
        )
    }

    @Deprecated("Deprecated in Java")
    override fun removeFromCopiedStackData(view: WriteView) {
        view.remove("Items")
    }

    companion object {
        private val LOGGER: Logger = LogUtils.getLogger()
        private const val field_31330 = 2
        private const val field_31331 = 4
        fun litServerTick(
            world: ServerWorld,
            pos: BlockPos,
            state: BlockState,
            blockEntity: CrudeCampfireBlockEntity,
            recipeMatchGetter: MatchGetter<SingleStackRecipeInput?, CampfireCookingRecipe?>
        ) {
            var bl = false

            for (i in blockEntity.itemsBeingCooked.indices) {
                val itemStack = blockEntity.itemsBeingCooked.get(i)
                if (!itemStack.isEmpty) {
                    bl = true
                    val var10002: Int = blockEntity.cookingTimes[i]++
                    if (blockEntity.cookingTimes[i] >= blockEntity.cookingTotalTimes[i]) {
                        val singleStackRecipeInput = SingleStackRecipeInput(itemStack)
                        val itemStack2 =
                            recipeMatchGetter.getFirstMatch(singleStackRecipeInput, world).map(
                                Function { recipe: RecipeEntry<CampfireCookingRecipe?>? ->
                                    (recipe!!.value() as CampfireCookingRecipe).craft(
                                        singleStackRecipeInput,
                                        world.registryManager
                                    )
                                }).orElse(itemStack) as ItemStack
                        if (itemStack2.isItemEnabled(world.enabledFeatures)) {
                            ItemScatterer.spawn(
                                world,
                                pos.x.toDouble(),
                                pos.y.toDouble(),
                                pos.z.toDouble(),
                                itemStack2
                            )
                            blockEntity.itemsBeingCooked[i] = ItemStack.EMPTY
                            world.updateListeners(pos, state, state, 3)
                            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state))
                        }
                    }
                }
            }

            if (bl) {
                markDirty(world, pos, state)
            }
        }

        fun unlitServerTick(world: World, pos: BlockPos?, state: BlockState, campfire: CrudeCampfireBlockEntity) {
            var bl = false

            for (i in campfire.itemsBeingCooked.indices) {
                if (campfire.cookingTimes[i] > 0) {
                    bl = true
                    campfire.cookingTimes[i] =
                        MathHelper.clamp(campfire.cookingTimes[i] - 2, 0, campfire.cookingTotalTimes[i])
                }
            }

            if (bl) {
                markDirty(world, pos, state)
            }
        }

        fun clientTick(world: World, pos: BlockPos, state: BlockState, campfire: CrudeCampfireBlockEntity) {
            if (!state.get(CampfireBlock.LIT)) return  // 🔥 Don't render smoke if not lit

            val random = world.random
            if (random.nextFloat() < 0.11f) {
                (0..<random.nextInt(2) + 2).forEach { i ->
                    CampfireBlock.spawnSmokeParticle(
                        world,
                        pos,
                        state.get(CampfireBlock.SIGNAL_FIRE),
                        false
                    )
                }
            }

            val i = (state.get(CampfireBlock.FACING) as Direction).horizontalQuarterTurns

            for (j in campfire.itemsBeingCooked.indices) {
                if (!campfire.itemsBeingCooked[j].isEmpty && random.nextFloat() < 0.2f) {
                    val direction = Direction.fromHorizontalQuarterTurns(Math.floorMod(j + i, 4))
                    val f = 0.3125f
                    val d = pos.x.toDouble() + 0.5 - (direction.offsetX
                        .toFloat() * 0.3125f).toDouble() + (direction.rotateYClockwise().offsetX
                        .toFloat() * 0.3125f).toDouble()
                    val e = pos.y.toDouble() + 0.5
                    val g = pos.z.toDouble() + 0.5 - (direction.offsetZ
                        .toFloat() * 0.3125f).toDouble() + (direction.rotateYClockwise().offsetZ
                        .toFloat() * 0.3125f).toDouble()

                    (0..3).forEach { k ->
                        world.addParticleClient(ParticleTypes.SMOKE, d, e, g, 0.0, 5.0E-4, 0.0)
                    }
                }
            }
        }
    }
}
