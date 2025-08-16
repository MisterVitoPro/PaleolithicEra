package com.toolsandtaverns.paleolithicera.entity

import com.toolsandtaverns.paleolithicera.PaleolithicEra.LOGGER
import com.toolsandtaverns.paleolithicera.registry.ModEntities
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
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import kotlin.math.min

/**
 * BlockEntity for the Crude Campfire block, a primitive fire source in the Paleolithic Era mod.
 *
 * The Crude Campfire represents one of the earliest fire technologies available to players,
 * featuring a simpler construction than vanilla campfires but with similar cooking functionality.
 * It has a limited burn time and handles cooking up to 4 items simultaneously, tracking their
 * individual cooking progress.
 *
 * This implementation mirrors vanilla campfire mechanics while representing the more primitive
 * technology of the Paleolithic era. The crude nature of this campfire is reflected in its
 * limited burn duration and simpler visual effects.
 */
class CrudeCampfireBlockEntity(pos: BlockPos, state: BlockState?) :
    BlockEntity(ModEntities.CRUDE_CAMPFIRE, pos, state),
    Clearable {
    /**
     * Stores the items currently being cooked on the campfire (up to 4).
     * Each slot represents a cooking position on the campfire.
     */
    val itemsBeingCooked: DefaultedList<ItemStack?> = DefaultedList.ofSize<ItemStack?>(4, ItemStack.EMPTY)

    /**
     * Tracks the current cooking progress (in ticks) for each item slot.
     * Each index corresponds to the same index in [itemsBeingCooked].
     */
    private val cookingTimes: IntArray = IntArray(4)

    /**
     * Stores the total cooking time required for each item slot.
     * Determined by the recipe of the item being cooked in the corresponding slot.
     */
    private val cookingTotalTimes: IntArray = IntArray(4)

    /**
     * Tracks how many ticks the campfire has left before it burns out.
     * When this reaches zero, the campfire will extinguish.
     */
    private var burnTicksRemaining: Int = 0

    /**
     * The total number of ticks a crude campfire burns when lit (20 ticks = 1 seconds).
     * This represents the primitive nature of the campfire - it doesn't burn as long as
     * advanced fire sources, reflecting Paleolithic technology limitations.
     */
    private val totalBurnTicks: Int = 120 * 20

    /**
     * Reads the entity's data from NBT or component storage.
     *
     * Loads all cooking-related information including:
     * - Items currently being cooked
     * - Current cooking progress for each item
     * - Total cooking time required for each item
     * - Remaining burn time for the campfire
     *
     * This ensures the campfire state persists across world saves/loads, maintaining
     * all cooking progress and burn state, which is critical for the cooking mechanic.
     *
     * @param view The data source to read from
     */
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
        }) { Arrays.fill(this.cookingTimes, 0) }
        view.getOptionalIntArray("CookingTotalTimes").ifPresentOrElse(Consumer { `is`: IntArray? ->
            System.arraycopy(
                `is`,
                0,
                this.cookingTotalTimes,
                0,
                min(this.cookingTotalTimes.size, `is`!!.size)
            )
        }) { Arrays.fill(this.cookingTotalTimes, 0) }
        burnTicksRemaining = view.getInt("BurnTicksRemaining", 0)
    }

    /**
     * Writes the entity's data to NBT or component storage.
     *
     * Saves all cooking-related information including:
     * - Items currently being cooked
     * - Current cooking progress for each item
     * - Total cooking time required for each item
     * - Remaining burn time for the campfire
     *
     * This persistence is essential for maintaining the campfire's state between game sessions,
     * allowing players to return to their cooking progress - an important gameplay element in
     * the survival-focused Paleolithic era.
     *
     * @param view The data destination to write to
     */
    override fun writeData(view: WriteView) {
        super.writeData(view)
        Inventories.writeData(view, this.itemsBeingCooked, true)
        view.putIntArray("CookingTimes", this.cookingTimes)
        view.putIntArray("CookingTotalTimes", this.cookingTotalTimes)
        view.putInt("BurnTicksRemaining", burnTicksRemaining)
    }

    /**
     * Creates a packet to send this block entity's data to clients.
     *
     * This network synchronization is essential for the multiplayer experience,
     * ensuring all players see the same state of the campfire, including what items
     * are being cooked and visual effects like smoke particles.
     *
     * @return A packet containing this entity's data for client synchronization
     */
    override fun toUpdatePacket(): BlockEntityUpdateS2CPacket? {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    /**
     * Creates NBT data to send when a chunk is first loaded by a client.
     *
     * This provides the initial state of the campfire when a player first sees it,
     * including which items are being cooked. For Paleolithic players, seeing what's
     * cooking from a distance is an important visual cue for finding their way back to camp.
     *
     * @param registries Registry wrapper for data serialization
     * @return NBT compound containing initial chunk data
     */
    override fun toInitialChunkDataNbt(registries: WrapperLookup): NbtCompound? {
        ErrorReporter.Logging(this.reporterContext, LOGGER).use { logging ->
            val nbtWriteView = NbtWriteView.create(logging, registries)
            Inventories.writeData(nbtWriteView, this.itemsBeingCooked, true)
            return nbtWriteView.nbt
        }
    }

    /**
     * Attempts to add an item to the campfire for cooking.
     *
     * This core gameplay mechanic allows players to cook food without requiring a full furnace,
     * representing how Paleolithic humans would have prepared meals. The method:
     * 1. Finds the first empty cooking slot
     * 2. Checks if the item has a valid campfire cooking recipe
     * 3. Sets up cooking times based on the recipe
     * 4. Takes one item from the stack and places it in the cooking slot
     * 5. Emits a game event to notify nearby systems of the change
     *
     * @param world The server world containing this campfire
     * @param entity The entity adding the item (typically a player)
     * @param stack The ItemStack to be cooked
     * @return true if the item was successfully added, false otherwise
     */
    fun addItem(world: ServerWorld, entity: LivingEntity?, stack: ItemStack): Boolean {
        for (i in this.itemsBeingCooked.indices) {
            val itemStack = this.itemsBeingCooked[i]
            if (itemStack.isEmpty) {
                val optional = world.recipeManager
                    .getFirstMatch(
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

    /**
     * Updates the blockstate listeners to notify of changes to this block entity.
     *
     * This method ensures that when the campfire's state changes (like adding an item
     * or completing cooking), the client is properly notified and visual updates occur.
     * For Paleolithic players, these visual cues are critical feedback mechanisms to
     * understand the cooking process.
     */
    private fun updateListeners() {
        this.markDirty()
        this.getWorld()!!.updateListeners(this.getPos(), this.cachedState, this.cachedState, 3)
    }

    /**
     * Clears all items from the campfire's cooking slots.
     *
     * This implementation of the Clearable interface allows the campfire to be emptied
     * programmatically, such as when the block is broken or another game mechanic requires
     * removing all cooking items at once.
     */
    override fun clear() {
        this.itemsBeingCooked.clear()
    }

    /**
     * Handles behavior when the campfire block is replaced or destroyed.
     *
     * Drops any items that were being cooked when the campfire is broken,
     * preventing resource loss and maintaining the survival-oriented game design
     * of the Paleolithic Era mod.
     *
     * @param pos The position of the block
     * @param oldState The previous blockstate
     */
    override fun onBlockReplaced(pos: BlockPos, oldState: BlockState?) {
        if (this.world != null) {
            ItemScatterer.spawn(this.world, pos, this.itemsBeingCooked)
        }
    }

    /**
     * Reads component data from the component system.
     *
     * This allows the campfire to integrate with Minecraft's component system,
     * loading container data for the cooking items. The component system provides
     * a more flexible approach to data storage than traditional NBT.
     *
     * @param components The components access object containing data
     */
    override fun readComponents(components: ComponentsAccess) {
        super.readComponents(components)
        (components.getOrDefault(
            DataComponentTypes.CONTAINER,
            ContainerComponent.DEFAULT
        ) as ContainerComponent).copyTo(
            this.itemsBeingCooked
        )
    }

    /**
     * Adds components to the component builder for data serialization.
     *
     * Registers the campfire's inventory as a container component, allowing
     * it to be properly saved and loaded as part of Minecraft's component system.
     * This is critical for data persistence across game sessions.
     *
     * @param builder The component map builder
     */
    override fun addComponents(builder: ComponentMap.Builder) {
        builder.add(
            DataComponentTypes.CONTAINER,
            ContainerComponent.fromStacks(this.itemsBeingCooked)
        )
    }

    /**
     * Removes redundant data from copied stack data.
     *
     * This deprecated method is maintained for backward compatibility with
     * Minecraft's data systems. It prevents duplicate item data when the
     * block entity is copied.
     *
     * @param view The write view to modify
     */
    @Deprecated("Deprecated in Java")
    override fun removeFromCopiedStackData(view: WriteView) {
        view.remove("Items")
    }

    /**
     * Starts the burn timer for the crude campfire.
     *
     * When called, this method sets the campfire to burn for its full duration
     * (totalBurnTicks). This represents lighting the campfire with a primitive
     * fire-starting method, a critical survival skill in the Paleolithic era.
     *
     * The limited burn time (compared to modern furnaces) reflects the primitive
     * technology available during this era, requiring players to regularly maintain
     * their fire sources.
     */
    fun startBurnTimer() {
        this.burnTicksRemaining = totalBurnTicks
        markDirty()
    }

    companion object {
        /**
         * Handles server-side tick logic for a lit crude campfire.
         *
         * This method is responsible for several critical gameplay mechanics:
         * 1. Burn time management - decreases remaining burn time and extinguishes when depleted
         * 2. Cooking progress - advances cooking time for each item being cooked
         * 3. Item completion - finishes cooking items that have reached their cooking time
         * 4. Result spawning - drops the cooked item into the world when complete
         *
         * These mechanics simulate primitive cooking techniques of the Paleolithic era,
         * where food preparation was a time-consuming but essential survival activity.
         * The limited burn time creates gameplay tension, requiring players to manage
         * their cooking time efficiently.
         *
         * @param world The server world containing the campfire
         * @param pos The position of the campfire block
         * @param state The current blockstate of the campfire
         * @param blockEntity The campfire block entity instance
         * @param recipeMatchGetter Recipe matcher for finding cooking recipes
         */
        fun litServerTick(
            world: ServerWorld,
            pos: BlockPos,
            state: BlockState,
            blockEntity: CrudeCampfireBlockEntity,
            recipeMatchGetter: MatchGetter<SingleStackRecipeInput?, CampfireCookingRecipe?>
        ) {
            var bl = false

            if (blockEntity.burnTicksRemaining > 0) {
                blockEntity.burnTicksRemaining--

                if (blockEntity.burnTicksRemaining == 0) {
                    world.setBlockState(pos, state.with(CampfireBlock.LIT, false), 3)
                    return
                }
            }


            for (i in blockEntity.itemsBeingCooked.indices) {
                val itemStack = blockEntity.itemsBeingCooked[i]
                if (!itemStack.isEmpty) {
                    bl = true
                    blockEntity.cookingTimes[i]++
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

        /**
         * Handles server-side tick logic for an unlit crude campfire.
         *
         * Even when not burning, the campfire maintains state for partially cooked items.
         * However, cooking progress degrades over time when the fire is out, simulating
         * how food cools down when not actively heated - a realistic mechanic that adds
         * challenge to Paleolithic cooking.
         *
         * The cooking regression occurs at twice the rate of progression (decreasing by 2 ticks
         * per game tick), creating urgency to relight fires that have gone out during cooking.
         * This represents the struggle of maintaining consistent heat sources in primitive technology.
         *
         * @param world The world containing the campfire
         * @param pos The position of the campfire block
         * @param state The current blockstate of the campfire
         * @param campfire The campfire block entity instance
         */
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

        /**
         * Handles client-side visual effects for the crude campfire.
         *
         * This method is responsible for creating the visual representation of the campfire,
         * generating smoke particles both from the fire itself and from items being cooked.
         * These visual cues serve multiple gameplay purposes:
         *
         * 1. Feedback for cooking - Players can see when items are cooking from the additional smoke
         * 2. Navigation aid - Smoke from campfires can help players locate their base from a distance
         * 3. Atmosphere - Creates the primitive, rustic feel appropriate for Paleolithic technology
         *
         * The smoke particles from cooking items are positioned precisely around the campfire
         * based on which slot the item occupies, creating a realistic cooking simulation.
         *
         * @param world The client world containing the campfire
         * @param pos The position of the campfire block
         * @param state The current blockstate of the campfire
         * @param campfire The campfire block entity instance
         */
        fun clientTick(world: World, pos: BlockPos, state: BlockState, campfire: CrudeCampfireBlockEntity) {
            if (!state.get(CampfireBlock.LIT)) return

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
                    val fixed = 0.3125f
                    val d = pos.x.toDouble() + 0.5 - (direction.offsetX
                        .toFloat() * fixed).toDouble() + (direction.rotateYClockwise().offsetX
                        .toFloat() * fixed).toDouble()
                    val e = pos.y.toDouble() + 0.5
                    val g = pos.z.toDouble() + 0.5 - (direction.offsetZ
                        .toFloat() * fixed).toDouble() + (direction.rotateYClockwise().offsetZ
                        .toFloat() * fixed).toDouble()

                    (0..3).forEach { k ->
                        world.addParticleClient(ParticleTypes.SMOKE, d, e, g, 0.0, 5.0E-4, 0.0)
                    }
                }
            }
        }
    }
}
