package com.toolsandtaverns.paleolithicera.datagen

import com.toolsandtaverns.paleolithicera.datagen.advancement.PaleolithicEraAdvancementTab
import net.minecraft.data.DataOutput
import net.minecraft.data.advancement.AdvancementProvider
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

/**
 * Registers advancement tabs for the Paleolithic Era mod.
 */
class ModAdvancementProvider(
    output: DataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : AdvancementProvider(
    output, registriesFuture, listOf(
        PaleolithicEraAdvancementTab
    )
)
