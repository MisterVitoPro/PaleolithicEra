package com.toolsandtaverns.paleolithicera.datagen

import com.toolsandtaverns.paleolithicera.datagen.advancement.PaleolithicEraAdvancementTab
import net.minecraft.data.DataOutput
import net.minecraft.data.advancement.AdvancementProvider
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

/**
 * Registers advancement tabs for the Paleolithic Era mod, creating the progression system
 * that guides players through the primitive technology experience.
 * 
 * Advancements in the Paleolithic Era mod serve multiple important purposes:
 * 
 * 1. **Guided Progression**: They introduce players to primitive technologies in a logical sequence,
 *    reflecting how early humans might have discovered and built upon basic survival techniques
 * 
 * 2. **Historical Narrative**: Each advancement tells part of the story of human technological evolution,
 *    educating players about realistic Paleolithic era development paths
 * 
 * 3. **Achievement System**: They reward players for mastering primitive survival skills and
 *    discovering new crafting techniques
 * 
 * 4. **Tutorial Function**: Early advancements help teach players the unique mechanics of the mod,
 *    such as knapping, hide processing, and primitive fire starting
 * 
 * The advancement structure is designed to mirror the technological progression that might have
 * occurred in actual Paleolithic societies, from basic stone tools to more complex crafting systems.
 */
class ModAdvancementProvider(
    output: DataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : AdvancementProvider(
    output, registriesFuture, listOf(
        PaleolithicEraAdvancementTab
    )
)
