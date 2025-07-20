package com.toolsandtaverns.paleolithicera.datagen.recipe

import com.toolsandtaverns.paleolithicera.Constants
import com.toolsandtaverns.paleolithicera.recipe.KnapRecipe
import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.advancement.AdvancementCriterion
import net.minecraft.advancement.AdvancementRewards
import net.minecraft.advancement.criterion.InventoryChangedCriterion
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

    private fun buildDefaultKnapRecipe(
        exporter: RecipeExporter,
        recipeId: String,
        recipe: KnapRecipe,
        criterionName: String,
        criterion: AdvancementCriterion<InventoryChangedCriterion.Conditions>
    ) {
        val identifier = Identifier.of(Constants.MOD_ID, recipeId)
        val key = RegistryKey.of(RegistryKeys.RECIPE, identifier)
        return exporter.accept(
            key,
            recipe,
            exporter.advancementBuilder
                .criterion(criterionName, criterion)
                .rewards(AdvancementRewards.Builder.recipe(key))
                .build(identifier)
        )
    }

    override fun getRecipeGenerator(
        registryLookup: RegistryWrapper.WrapperLookup,
        exporter: RecipeExporter
    ): RecipeGenerator {

        return object : RecipeGenerator(registryLookup, exporter) {
            override fun generate() {
                // Flint -> Flint Shard ×2
                buildDefaultKnapRecipe(
                    exporter = exporter,
                    recipeId = "knap_flint_to_flint_shard",
                    recipe = KnapRecipe(ItemStack(ModItems.FLINT_SHARD, 2), Items.FLINT),
                    criterionName = "has_flint",
                    criterion = InventoryChangedCriterion.Conditions.items(Items.FLINT)
                )

                // Flint Shard + Stick -> Crude Knife
                buildDefaultKnapRecipe(
                    exporter = exporter,
                    recipeId = "knap_flint_knife",
                    recipe = KnapRecipe(ItemStack(ModItems.FLINT_KNIFE), Items.STICK, ModItems.FLINT_SHARD),
                    criterionName = "has_flint_shard",
                    criterion = InventoryChangedCriterion.Conditions.items(ModItems.FLINT_SHARD)
                )

                // Flint Shard + Bone -> Bone Spearhead
                buildDefaultKnapRecipe(
                    exporter = exporter,
                    recipeId = "knap_bone_spearhead",
                    recipe = KnapRecipe(ItemStack(ModItems.BONE_SPEARHEAD), Items.BONE),
                    criterionName = "has_flint_shard",
                    criterion = InventoryChangedCriterion.Conditions.items(ModItems.FLINT_SHARD)
                )
            }
        }
    }

    override fun getName(): String? {
        return "Knapping Recipe Provider"
    }
}