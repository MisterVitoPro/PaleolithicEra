package com.toolsandtaverns.paleolithicera.event

import com.toolsandtaverns.paleolithicera.progression.WorldProgress
import com.toolsandtaverns.paleolithicera.registry.ModEntityType
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnReason
import net.minecraft.entity.passive.GoatEntity
import net.minecraft.entity.passive.PigEntity
import net.minecraft.server.world.ServerWorld
import java.util.concurrent.CopyOnWriteArrayList

object SpawnGate {
    private data class Allowed(val type: EntityType<*>, val pos: BlockPos, val expiry: Long)
    private val recentEggAllowed = CopyOnWriteArrayList<Allowed>()

    fun noteSpawnEggUse(world: ServerWorld, pos: BlockPos, type: EntityType<*>) {
        val expiry = world.time + 40 // ~2 seconds grace
        recentEggAllowed.add(Allowed(type, pos, expiry))
    }

    private fun isEggAllowed(world: ServerWorld, entity: Entity): Boolean {
        val now = world.time
        // prune expired
        recentEggAllowed.removeIf { it.expiry < now }
        val match = recentEggAllowed.firstOrNull {
            it.type == entity.type && it.pos.getSquaredDistance(entity.x, entity.y, entity.z) <= 16.0
        }
        if (match != null) {
            recentEggAllowed.remove(match)
            return true
        }
        return false
    }

    fun initialize() {
        // Replace pigs with boars and goats with ibex until progression unlocks.
        ServerEntityEvents.ENTITY_LOAD.register { entity: Entity, world ->
            if (world is ServerWorld && !WorldProgress.isUnlocked(world)) {
                // Allow if this looks like a spawn egg usage nearby
                if (isEggAllowed(world, entity)) return@register
                // Allow if a nearby creative player is present (likely manual spawn/building)
                val anyCreativeNearby = world.players.any { it.isCreative && it.squaredDistanceTo(entity) <= 32.0 }
                if (anyCreativeNearby) return@register

                val replacement = when {
                    entity.type == EntityType.PIG || entity is PigEntity ->
                        ModEntityType.BOAR_ENTITY.create(world, SpawnReason.NATURAL)
                    entity.type == EntityType.GOAT || entity is GoatEntity ->
                        ModEntityType.IBEX_ENTITY.create(world, SpawnReason.NATURAL)
                    else -> null
                }

                if (replacement != null) {
                    // Position and orientation match
                    replacement.refreshPositionAndAngles(entity.x, entity.y, entity.z, entity.yaw, entity.pitch)
                    // Try to spawn the replacement, then remove the original
                    if (world.spawnEntity(replacement)) {
                        entity.remove(Entity.RemovalReason.DISCARDED)
                    }
                }
            }
        }
    }
}
