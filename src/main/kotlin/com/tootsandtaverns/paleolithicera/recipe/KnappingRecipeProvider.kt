package com.tootsandtaverns.paleolithicera.recipe

import com.tootsandtaverns.paleolithicera.Constants.MOD_ID
import com.tootsandtaverns.paleolithicera.registry.ModItems
import com.tootsandtaverns.paleolithicera.registry.ModRecipes
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.DataOutput
import net.minecraft.data.recipe.RecipeExporter
import net.minecraft.data.recipe.RecipeGenerator
import net.minecraft.data.recipe.ShapelessRecipeJsonBuilder
import net.minecraft.item.Items
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture

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
                // Flint → Flint Shard ×2
                val recipeId = Identifier.of(MOD_ID, "knap_flint_to_flint_shard")
                val recipeKey = RegistryKey.of(RegistryKeys.RECIPE, recipeId)

                ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModRecipes. , 9)
                    .input(BlockInit.EXAMPLE_BLOCK)
                    .criterion(hasItem(BlockInit.EXAMPLE_BLOCK), conditionsFromItem(BlockInit.EXAMPLE_BLOCK))
                    .offerTo(exporter);

                exporter.accept()

                createShapeless(RecipeCategory.MISC, ModItems.FLINT_SHARD, 2)
                    .input(Items.FLINT)
                    .criterion(hasItem(Items.FLINT), conditionsFromItem(Items.FLINT))
                    .offerTo(exporter, recipeKey)
            }
        }
    }

    override fun getName(): String? {
        return "Knapping Recipes"
    }

}
//    override fun getRecipeGenerator(
//        registryLookup: RegistryWrapper.WrapperLookup,
//        exporter: RecipeExporter
//    ): RecipeGenerator {
//        return object : RecipeGenerator(registryLookup, exporter) {
//            override fun generate() {
//                // Flint → Flint Shard ×2
//                val recipeId = Identifier.of(MOD_ID, "knap_flint_to_flint_shard")
//                val recipeKey = RegistryKey.of(RegistryKeys.RECIPE, recipeId)
//                createShapeless(RecipeCategory.MISC, ModItems.FLINT_SHARD, 2)
//                    .input(Items.FLINT)
//                    .criterion(hasItem(Items.FLINT), conditionsFromItem(Items.FLINT))
//                    .offerTo(exporter, recipeKey)

                // Stone Chunk → Crude Blade
//                createShapeless(RecipeCategory.MISC, ModItems.CRUDE_BLADE)
//                    .input(ModItems.STONE_CHUNK)
//                    .criterion(hasItem(ModItems.STONE_CHUNK), conditionsFromItem(ModItems.STONE_CHUNK))
//                    .offerTo(exporter, Identifier.of(MOD_ID, "knap_stone_chunk_to_blade"))

                // Flint + Bone → Spearhead
//                createShapeless(RecipeCategory.MISC, ModItems.SPEARHEAD)
//                    .input(Items.FLINT)
//                    .input(Items.BONE)
//                    .criterion(hasItem(Items.FLINT), conditionsFromItem(Items.FLINT))
//                    .offerTo(exporter, Identifier.of(MOD_ID, "knap_flint_bone_to_spearhead"))

                // Flint Shard + Stick → Crude Knife
//                createShapeless(RecipeCategory.MISC, ModItems.CRUDE_KNIFE)
//                    .input(ModItems.FLINT_SHARD)
//                    .input(Items.STICK)
//                    .criterion(hasItem(ModItems.FLINT_SHARD), conditionsFromItem(ModItems.FLINT_SHARD))
//                    .offerTo(exporter, Identifier.of(MOD_ID, "knap_shard_stick_to_knife"))
//            }
//        }
//    }
//
//    override fun getName(): String {
//        return "KnappingRecipeProvider"
//    }
//}
