package com.toolsandtaverns.paleolithicera.entity

import com.toolsandtaverns.paleolithicera.registry.ModEntityType
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.item.Items
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.ItemScatterer
// Storage view APIs removed in these mappings; use NBT read/write
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.math.cos
import kotlin.math.sin

/**
 * Holds fuel (bones) and emits a protective aura when active.
 * Each bone grants n seconds of protection. While active, mobs within radius
 * cannot target players in the radius; particle ring visualizes the border.
 */
class EffigyOfProtectionEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(ModEntityType.EFFIGY_OF_PROTECTION_BLOCK_ENTITY, pos, state) {

    private val inventory = SimpleInventory(1)
    private var activeTicks: Int = 0

    // 10-block radius
    private val radius = 10.0
    private val secondsPerFuel = 15

    // Server-side HUD: action bar updates for nearby players while aura is active

    fun serverTick(world: ServerWorld) {
        // Consume next bone if inactive and fuel available
        if (activeTicks <= 0 && hasFuel()) {
            consumeOneFuel()
            activeTicks = secondsPerFuel * 20
        }

        if (activeTicks > 0) {
            activeTicks--
            applyProtection(world)
            if (world.time % 10L == 0L) {
                spawnRingParticles(world)
            }
            // Notify nearby players periodically with remaining total time on the aura
            if (world.time % 20L == 0L) {
                notifyPlayers(world)
            }
        } else {
            // Inactive: no notifications
        }
    }

    private fun hasFuel(): Boolean = !inventory.getStack(0).isEmpty && inventory.getStack(0).isOf(Items.BONE)

    private fun consumeOneFuel() {
        val stack = inventory.getStack(0)
        if (!stack.isEmpty && stack.isOf(Items.BONE)) {
            stack.decrement(1)
            markDirty()
        }
    }

    fun tryInsertFuel(offer: ItemStack): Int {
        if (!offer.isOf(Items.BONE) || offer.isEmpty) return 0
        val slot = inventory.getStack(0)
        val max = offer.maxCount
        return if (slot.isEmpty) {
            val toMove = offer.count.coerceAtMost(max)
            inventory.setStack(0, ItemStack(Items.BONE, toMove))
            markDirty()
            toMove
        } else if (slot.isOf(Items.BONE)) {
            val canInsert = (max - slot.count).coerceAtLeast(0)
            val moved = offer.count.coerceAtMost(canInsert)
            if (moved > 0) {
                slot.increment(moved)
                markDirty()
            }
            moved
        } else 0
    }

    fun extractAll(): ItemStack {
        val stack = inventory.removeStack(0)
        if (!stack.isEmpty) markDirty()
        return stack
    }

    private fun applyProtection(world: ServerWorld) {
        val center = Vec3d.ofCenter(pos)
        val players = world.getEntitiesByClass(
            PlayerEntity::class.java,
            Box.from(center).expand(radius),
        ) { true }

        if (players.isEmpty()) return

        // For every hostile mob in the expanded area, repel or stop them at the boundary
        val mobs = world.getEntitiesByClass(
            MobEntity::class.java,
            Box.from(center).expand(radius + 4.0),
        ) { it.isAlive }

        val r = radius
        val rSq = r * r
        val boundary = r + 0.75
        val boundarySq = boundary * boundary

        for (mob in mobs) {
            val toCenter = center.subtract(mob.pos)
            val distSq = toCenter.lengthSquared()

            val isHostile = mob is HostileEntity
            val targetingPlayerInside = (mob.target as? PlayerEntity)?.pos?.isInRadius(center, radius) == true

            // Clear targeting of players inside the aura
            if (targetingPlayerInside) {
                mob.target = null
                mob.setAttacking(false)
            }

            // Only repel hostile mobs or anything currently targeting a player
            if (!isHostile && !targetingPlayerInside) continue

            // Inside the ring: force them to leave
            if (distSq < rSq) {
                val away = mob.pos.subtract(center).normalize()
                val targetPos = center.add(away.multiply(r + 2.0))
                mob.navigation.startMovingTo(targetPos.x, mob.y, targetPos.z, 1.25)
                // nudge outward
                mob.addVelocity(away.x * 0.2, 0.0, away.z * 0.2)
                continue
            }

            // Near the boundary (trying to enter): stop paths and nudge back
            if (distSq <= boundarySq) {
                mob.navigation.stop()
                val away = mob.pos.subtract(center).normalize()
                mob.addVelocity(away.x * 0.15, 0.0, away.z * 0.15)
            }
        }
    }

    private fun spawnRingParticles(world: ServerWorld) {
        val center = Vec3d.ofCenter(pos)
        val step = Math.PI / 16 // 32 points around the circle
        val r = radius + 0.5
        var angle = 0.0
        while (angle < Math.PI * 2) {
            val x = center.x + r * cos(angle)
            val z = center.z + r * sin(angle)
            world.spawnParticles(
                ParticleTypes.SOUL_FIRE_FLAME,
                x,
                center.y + 0.1,
                z,
                1,
                0.0,
                0.0,
                0.0,
                0.0
            )
            angle += step
        }
    }

    private fun notifyPlayers(world: ServerWorld) {
        val msg = Text.literal("Effigy of Protection Aura: ${formatTicks(getFuelTimeInTicks())}")
        val center = Vec3d.ofCenter(pos)
        val aabb = Box.from(center).expand(radius)
        val players = world.getEntitiesByClass(PlayerEntity::class.java, aabb) { true }
        for (player in players) {
            // Only show when the player is highlighting this effigy (crosshair on it)
            val hit = player.raycast(6.0, 0.0f, false)
            if (hit is BlockHitResult) {
                val hp = hit.blockPos
                if (hp == this.pos || hp == this.pos.up()) {
                    player.sendMessage(msg, true)
                }
            }
        }
    }

    private fun formatTicks(ticks: Int): String {
        val seconds = ticks / 20
        val m = seconds / 60
        val s = seconds % 60
        return String.format("%02d:%02d", m, s)
    }

    override fun readNbt(nbt: NbtCompound, registries: net.minecraft.registry.RegistryWrapper.WrapperLookup) {
        super.readNbt(nbt, registries)
        Inventories.readNbt(nbt, inventory.heldStacks, registries)
        activeTicks = nbt.getInt("ActiveTicks").orElse(0)
    }

    override fun writeNbt(nbt: NbtCompound, registries: net.minecraft.registry.RegistryWrapper.WrapperLookup) {
        super.writeNbt(nbt, registries)
        Inventories.writeNbt(nbt, inventory.heldStacks, registries)
        nbt.putInt("ActiveTicks", activeTicks)
    }

    override fun onBlockReplaced(pos: BlockPos, oldState: BlockState) {
        ItemScatterer.spawn(world as World, pos, inventory)
        super.onBlockReplaced(pos, oldState)
    }

    // Helper extension
    private fun Vec3d.isInRadius(center: Vec3d, r: Double): Boolean = this.squaredDistanceTo(center) <= r * r

    private fun getFuelTimeInTicks(): Int {
        return activeTicks + inventory.getStack(0).count * secondsPerFuel * 20
    }
}
