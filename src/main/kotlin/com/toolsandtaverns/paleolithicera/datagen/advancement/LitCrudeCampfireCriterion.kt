package com.toolsandtaverns.paleolithicera.datagen.advancement

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancement.AdvancementCriterion
import net.minecraft.advancement.criterion.AbstractCriterion
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

/**
 * Custom criterion that triggers when a player lights a Crude Campfire.
 */
class LitCrudeCampfireCriterion : AbstractCriterion<LitCrudeCampfireCriterion.Conditions>() {

    companion object {
        const val LIT_FIRE_CRITERION_ID: String = "lit_crude_campfire"
    }

    fun create(): AdvancementCriterion<Conditions> {
        return create(Conditions(Optional.empty()))
    }

    fun trigger(player: ServerPlayerEntity) {
        this.trigger(player) { true }
    }

    override fun getConditionsCodec(): Codec<Conditions> = Conditions.CODEC

    class Conditions(
        private val player: Optional<LootContextPredicate>
    ) : AbstractCriterion.Conditions {
        override fun player(): Optional<LootContextPredicate> = player

        companion object {
            val CODEC: Codec<Conditions> = RecordCodecBuilder.create { instance ->
                instance.group(
                    LootContextPredicate.CODEC.optionalFieldOf("player").forGetter { it.player }
                ).apply(instance, ::Conditions)
            }
        }
    }
}
