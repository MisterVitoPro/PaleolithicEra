package com.toolsandtaverns.paleolithicera.recipe

import com.toolsandtaverns.paleolithicera.registry.ModBlocks
import com.toolsandtaverns.paleolithicera.registry.ModItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.recipe.RecipeExporter
import net.minecraft.data.recipe.RecipeGenerator
import net.minecraft.item.Items
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.RegistryWrapper
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
                    .pattern("B ")
                    .pattern(" S")
                    .input('B', Items.BONE)
                    .input('S', Items.STICK)
                    .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem(Items.BONE))
                    .offerTo(exporter)

                createShaped(RecipeCategory.COMBAT, ModItems.BONE_SPEAR, 1)
                    .pattern("  B")
                    .pattern(" S ")
                    .pattern("S  ")
                    .input('B', ModItems.BONE_SPEARHEAD)
                    .input('S', Items.STICK)
                    .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem(ModItems.BONE_SPEARHEAD))
                    .offerTo(exporter)

                createShaped(RecipeCategory.TOOLS, ModItems.FIRE_DRILL, 1)
                    .pattern("S S")
                    .pattern(" D ")
                    .pattern(" F ")
                    .input('D', ModItems.BARK)
                    .input('S', Items.STICK)
                    .input('F', ModItems.PLANT_FIBER)
                    .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem(ModItems.PLANT_FIBER))
                    .offerTo(exporter)

                createShaped(RecipeCategory.MISC, ModBlocks.CRUDE_CAMPFIRE, 1)
                    .pattern("SBS")
                    .pattern("BFB")
                    .pattern("SBS")
                    .input('S', Items.STICK)
                    .input('B', ModItems.BARK)
                    .input('F', ModItems.PLANT_FIBER)
                    .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem(ModItems.BARK))
                    .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem(ModItems.PLANT_FIBER))
                    .offerTo(exporter)

                createShaped(RecipeCategory.MISC, ModBlocks.KNAPPING_STATION, 1)
                    .pattern("SS")
                    .pattern("BF")
                    .input('S', Items.STICK)
                    .input('B', ModItems.BARK)
                    .input('F', ModItems.PLANT_FIBER)
                    .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem { ModItems.PLANT_FIBER })
                    .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem { ModItems.BARK })
                    .offerTo(exporter)
            }
        }
    }

    override fun getName(): String {
        return "Vanilla Recipe Provider"
    }
}