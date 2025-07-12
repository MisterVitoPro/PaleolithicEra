package com.toolsandtaverns.paleolithicera.recipe

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.recipe.RecipeExporter
import net.minecraft.data.recipe.RecipeGenerator
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture

/**
 * Data provider that generates knapping recipes for the Knapping Station.
 */
class KnappingRecipeProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricRecipeProvider(output, registriesFuture) {

    override fun getRecipeGenerator(
        registryLookup: RegistryWrapper.WrapperLookup,
        exporter: RecipeExporter
    ): RecipeGenerator {
        return object : RecipeGenerator(registryLookup, exporter) {
            override fun generate() {
                // Flint -> Flint Shard ×2
                exporter.accept(
                    RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(MOD_ID, "knap_flint_to_flint_shard")),
                    KnapRecipe(ItemStack(ModItems.FLINT_SHARD, 2), Items.FLINT),
                    null
                )
                
                // Flint Shard + Stick -> Crude Knife
                exporter.accept(
                    RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(MOD_ID, "knap_crude_knife")),
                    KnapRecipe(ItemStack(ModItems.CRUDE_KNIFE), Items.STICK, ModItems.FLINT_SHARD),
                    null
                )

                // Flint Shard + Bone -> Bone Spearhead
                exporter.accept(
                    RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(MOD_ID, "knap_bone_spearhead")),
                    KnapRecipe(ItemStack(ModItems.BONE_SPEARHEAD), Items.BONE),
                    null
                )
            }
        }
    }

    override fun getName(): String? {
        return "Knapping Recipe Provider"
    }
}
