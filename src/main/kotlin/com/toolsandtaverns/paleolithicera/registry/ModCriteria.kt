package com.toolsandtaverns.paleolithicera.registry

import com.toolsandtaverns.paleolithicera.datagen.advancement.LitCrudeCampfireCriterion
import com.toolsandtaverns.paleolithicera.datagen.advancement.EffigyPlacedCriterion
import com.toolsandtaverns.paleolithicera.datagen.advancement.LitCrudeCampfireCriterion.Companion.LIT_FIRE_CRITERION_ID
import net.minecraft.advancement.criterion.Criteria

object ModCriteria {
    lateinit var LIT_CRUDE_CAMPFIRE: LitCrudeCampfireCriterion
    lateinit var EFFIGY_PLACED: EffigyPlacedCriterion

    fun initialize() {
        LIT_CRUDE_CAMPFIRE = Criteria.register(LIT_FIRE_CRITERION_ID, LitCrudeCampfireCriterion())
        EFFIGY_PLACED = Criteria.register(EffigyPlacedCriterion.ID, EffigyPlacedCriterion())
    }
}
