package com.toolsandtaverns.paleolithicera.datagen.recipe

import com.toolsandtaverns.paleolithicera.Constants
import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModItems
import com.toolsandtaverns.paleolithicera.util.id
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.data.recipe.CookingRecipeJsonBuilder
import net.minecraft.data.recipe.RecipeExporter
import net.minecraft.data.recipe.RecipeGenerator
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture

class VanillaRecipeProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricRecipeProvider(output, registriesFuture) {

    override fun getRecipeGenerator(
        registryLookup: RegistryWrapper.WrapperLookup,
        exporter: RecipeExporter
    ): RecipeGenerator {
        return object : RecipeGenerator(registryLookup, exporter) {
            override fun generate() {
                createShaped(RecipeCategory.TOOLS, ModItems.BONE_KNIFE, 1)
                    .pattern(" B")
                    .pattern("S ")
                    .input('B', Items.BONE)
                    .input('S', Items.STICK)
                    .criterion(hasItem(Items.BONE), conditionsFromItem(Items.BONE))
                    .offerTo(exporter)

                createShaped(RecipeCategory.COMBAT, ModItems.BONE_SPEAR, 1)
                    .pattern("  B")
                    .pattern(" S ")
                    .pattern("S  ")
                    .input('B', ModItems.BONE_SPEARHEAD)
                    .input('S', Items.STICK)
                    .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem(ModItems.BONE_SPEARHEAD))
                    .offerTo(exporter)

                createShaped(RecipeCategory.COMBAT, ModItems.WOODEN_SPEAR, 1)
                    .pattern(" S")
                    .pattern("S ")
                    .input('S', Items.STICK)
                    .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                    .offerTo(exporter)

                createShaped(RecipeCategory.TOOLS, ModItems.FIRE_DRILL, 1)
                    .pattern("SF")
                    .pattern("SS")
                    .input('S', Items.STICK)
                    .input('F', ModItems.PLANT_FIBER)
                    .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem(ModItems.PLANT_FIBER))
                    .offerTo(exporter)

                createShaped(RecipeCategory.MISC, ModBlocks.CRUDE_CAMPFIRE, 1)
                    .pattern("SF")
                    .pattern("BB")
                    .input('S', Items.STICK)
                    .input('B', ModItems.BARK)
                    .input('F', ModItems.PLANT_FIBER)
                    .criterion(hasItem(ModItems.BARK), conditionsFromItem(ModItems.BARK))
                    .criterion(hasItem(ModItems.PLANT_FIBER), conditionsFromItem(ModItems.PLANT_FIBER))
                    .offerTo(exporter)

                createShaped(RecipeCategory.MISC, ModBlocks.KNAPPING_STATION, 1)
                    .pattern("SS")
                    .pattern("RR")
                    .input('S', Items.STICK)
                    .input('R', ModItems.ROCK_CHUNK)
                    .criterion(hasItem(ModItems.ROCK_CHUNK), conditionsFromItem { ModItems.ROCK_CHUNK })
                    .offerTo(exporter)

                // Smelting (Furnace)
                CookingRecipeJsonBuilder.createSmelting(
                    Ingredient.ofItems(ModItems.RAW_ELDERBERRIES),
                    RecipeCategory.FOOD,
                    ModItems.COOKED_ELDERBERRIES,
                    0.35f,
                    200
                ).criterion("has_raw_elderberries", InventoryChangedCriterion.Conditions.items(ModItems.RAW_ELDERBERRIES))
                    .offerTo(exporter, RegistryKey.of(RegistryKeys.RECIPE,Identifier.of(MOD_ID, "smelt_elderberries")))

                // Campfire Cooking
                CookingRecipeJsonBuilder.createCampfireCooking(
                    Ingredient.ofItems(ModItems.RAW_ELDERBERRIES),
                    RecipeCategory.FOOD,
                    ModItems.COOKED_ELDERBERRIES,
                    0.35f,
                    600
                ).criterion("has_raw_elderberries", InventoryChangedCriterion.Conditions.items(ModItems.RAW_ELDERBERRIES))
                    .offerTo(exporter, RegistryKey.of(RegistryKeys.RECIPE,Identifier.of(MOD_ID, "campfire_cook_elderberries")))

                createShaped(RecipeCategory.COMBAT, ModItems.RAWHIDE_TUNIC, 1)
                    .pattern("RF")
                    .pattern("RR")
                    .input('R', ModItems.RAWHIDE)
                    .input('F', ModItems.PLANT_FIBER)
                    .criterion(hasItem(ModItems.RAWHIDE), conditionsFromItem(ModItems.RAWHIDE))
                    .offerTo(exporter)

                createShaped(RecipeCategory.COMBAT, ModItems.RAWHIDE_LEGGINGS, 1)
                    .pattern("RR")
                    .pattern("FF")
                    .input('R', ModItems.RAWHIDE)
                    .input('F', ModItems.PLANT_FIBER)
                    .criterion(hasItem(ModItems.RAWHIDE), conditionsFromItem(ModItems.RAWHIDE))
                    .offerTo(exporter)

            }
        }
    }

    override fun getName(): String {
        return "Vanilla Recipe Provider"
    }
}