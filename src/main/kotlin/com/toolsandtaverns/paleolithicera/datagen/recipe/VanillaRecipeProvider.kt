package com.toolsandtaverns.paleolithicera.datagen.recipe

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

/**
 * Generates standard crafting and cooking recipes for the mod.
 *
 * This provider creates JSON recipe files for vanilla-style crafting table,
 * furnace, and campfire recipes during the data generation phase of the build.
 */
class VanillaRecipeProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricRecipeProvider(output, registriesFuture) {

    /**
     * Creates a recipe generator that produces all vanilla-style recipes.
     *
     * @param registryLookup Registry lookup for resolving registry references
     * @param exporter Recipe exporter to write recipe files
     * @return A recipe generator with the generate method implemented
     */
    override fun getRecipeGenerator(
        registryLookup: RegistryWrapper.WrapperLookup,
        exporter: RecipeExporter
    ): RecipeGenerator {
        return object : RecipeGenerator(registryLookup, exporter) {
            /**
             * Generates all vanilla-style crafting recipes for the mod.
             *
             * This includes shaped crafting recipes, furnace smelting recipes,
             * and campfire cooking recipes.
             */
            override fun generate() {
                // Bone Knife
                createShaped(RecipeCategory.TOOLS, ModItems.BONE_KNIFE, 1)
                    .pattern(" B")
                    .pattern("S ")
                    .input('B', ModItems.BONE_SHARD)
                    .input('S', Items.STICK)
                    .criterion(hasItem(Items.BONE), conditionsFromItem(Items.BONE))
                    .offerTo(exporter)

                // Bone Spear
                createShaped(RecipeCategory.COMBAT, ModItems.BONE_SPEAR, 1)
                    .pattern(" B")
                    .pattern("SP")
                    .input('B', ModItems.BONE_SHARD)
                    .input('P', ModItems.PLANT_CORDAGE)
                    .input('S', ModItems.WOODEN_SPEAR)
                    .criterion(hasItem(ModItems.BONE_SHARD), conditionsFromItem(ModItems.BONE_SHARD))
                    .offerTo(exporter)

                // Wooden Spear
                createShaped(RecipeCategory.COMBAT, ModItems.WOODEN_SPEAR, 1)
                    .pattern(" S")
                    .pattern("S ")
                    .input('S', Items.STICK)
                    .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                    .offerTo(exporter)

                // Wooden Harpoon
                createShaped(RecipeCategory.COMBAT, ModItems.WOODEN_HARPOON, 1)
                    .pattern("SS")
                    .pattern("WS")
                    .input('S', Items.STICK)
                    .input('W', ModItems.WOODEN_SPEAR)
                    .criterion(hasItem(ModItems.WOODEN_SPEAR), conditionsFromItem(ModItems.WOODEN_SPEAR))
                    .offerTo(exporter)

                // Fire Drill
                createShaped(RecipeCategory.TOOLS, ModItems.FIRE_DRILL, 1)
                    .pattern("SF")
                    .pattern("SS")
                    .input('S', Items.STICK)
                    .input('F', ModItems.PLANT_FIBER)
                    .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem(ModItems.PLANT_FIBER))
                    .offerTo(exporter)

                // Crude Campfire
                createShaped(RecipeCategory.MISC, ModBlocks.CRUDE_CAMPFIRE, 1)
                    .pattern("SF")
                    .pattern("BB")
                    .input('S', Items.STICK)
                    .input('B', ModItems.BARK)
                    .input('F', ModItems.PLANT_FIBER)
                    .criterion(hasItem(ModItems.BARK), conditionsFromItem(ModItems.BARK))
                    .criterion(hasItem(ModItems.PLANT_FIBER), conditionsFromItem(ModItems.PLANT_FIBER))
                    .offerTo(exporter)

                createShaped(RecipeCategory.MISC, ModBlocks.HIDE_DRYER, 1)
                    .pattern("SD")
                    .pattern("BB")
                    .input('S', Items.STICK)
                    .input('D', ModItems.RAWHIDE)
                    .input('B', ModItems.BARK)
                    .criterion(hasItem(ModItems.RAWHIDE), conditionsFromItem { ModItems.RAWHIDE })
                    .offerTo(exporter)

                // Knapping Station - crafting block for stone tools
                createShaped(RecipeCategory.MISC, ModBlocks.KNAPPING_STATION, 1)
                    .pattern("SS")
                    .pattern("RR")
                    .input('S', Items.STICK)
                    .input('R', ModItems.ROCK_CHUNK)
                    .criterion(hasItem(ModItems.ROCK_CHUNK), conditionsFromItem { ModItems.ROCK_CHUNK })
                    .offerTo(exporter)

                createShaped(RecipeCategory.MISC, ModItems.PATCHED_HIDE, 1)
                    .pattern("DF")
                    .pattern("FD")
                    .input('D', ModItems.DRY_HIDE)
                    .input('F', ModItems.PLANT_FIBER)
                    .criterion(hasItem(ModItems.DRY_HIDE), conditionsFromItem(ModItems.DRY_HIDE))
                    .offerTo(exporter)

                createShaped(RecipeCategory.MISC, ModItems.PLANT_CORDAGE, 1)
                    .pattern("FF")
                    .pattern("FF")
                    .input('F', ModItems.PLANT_FIBER)
                    .criterion(hasItem(ModItems.PLANT_FIBER), conditionsFromItem(ModItems.PLANT_FIBER))
                    .offerTo(exporter)

                createShaped(RecipeCategory.COMBAT, ModItems.HIDE_TUNIC, 1)
                    .pattern("PC")
                    .pattern("PP")
                    .input('C', ModItems.PLANT_CORDAGE)
                    .input('P', ModItems.PATCHED_HIDE)
                    .criterion(hasItem(ModItems.DRY_HIDE), conditionsFromItem(ModItems.DRY_HIDE))
                    .offerTo(exporter)

                createShaped(RecipeCategory.COMBAT, ModItems.HIDE_LEGGINGS, 1)
                    .pattern("PP")
                    .pattern("FF")
                    .input('P', ModItems.PATCHED_HIDE)
                    .input('F', ModItems.PLANT_FIBER)
                    .criterion(hasItem(ModItems.DRY_HIDE), conditionsFromItem(ModItems.DRY_HIDE))
                    .offerTo(exporter)

                createShaped(RecipeCategory.COMBAT, ModItems.HIDE_SHOES, 1)
                    .pattern("F ")
                    .pattern("PP")
                    .input('P', ModItems.PATCHED_HIDE)
                    .input('F', ModItems.PLANT_FIBER)
                    .criterion(hasItem(ModItems.DRY_HIDE), conditionsFromItem(ModItems.DRY_HIDE))
                    .offerTo(exporter)

                createShaped(RecipeCategory.COMBAT, ModItems.HIDE_CAP, 1)
                    .pattern("PP")
                    .pattern("F ")
                    .input('P', ModItems.PATCHED_HIDE)
                    .input('F', ModItems.PLANT_FIBER)
                    .criterion(hasItem(ModItems.DRY_HIDE), conditionsFromItem(ModItems.DRY_HIDE))
                    .offerTo(exporter)

                createShaped(RecipeCategory.COMBAT, ModItems.FLINT_KNIFE, 1)
                    .pattern("PF")
                    .pattern("S ")
                    .input('F', ModItems.FLINT_BIFACE)
                    .input('P', ModItems.PLANT_CORDAGE)
                    .input('S', Items.STICK)
                    .criterion(hasItem(ModItems.BONE_KNIFE), conditionsFromItem(ModItems.BONE_KNIFE))
                    .offerTo(exporter)

                createShaped(RecipeCategory.COMBAT, ModItems.FLINT_AXE, 1)
                    .pattern("PF")
                    .pattern("SF")
                    .input('F', ModItems.FLINT_BIFACE)
                    .input('P', ModItems.PLANT_CORDAGE)
                    .input('S', Items.STICK)
                    .criterion(hasItem(ModItems.BONE_KNIFE), conditionsFromItem(ModItems.BONE_KNIFE))
                    .offerTo(exporter)

                createShapeless(RecipeCategory.MISC, Items.BONE_MEAL, 1)
                    .input(ModItems.BONE_SHARD)
                    .criterion(hasItem(ModItems.BONE_SHARD), conditionsFromItem(ModItems.BONE_SHARD))
                    .offerTo(exporter)


//                createShaped(RecipeCategory.COMBAT, ModBlocks.CRUDE_BED, 1)
//                    .pattern("RP")
//                    .pattern("FF")
//                    .input('P', ModItems.PATCHED_HIDE)
//                    .input('F', ModItems.PLANT_FIBER)
//                    .input('R', ModItems.ROCK_CHUNK)
//                    .criterion(hasItem(ModItems.BONE_KNIFE), conditionsFromItem(ModItems.BONE_KNIFE))
//                    .offerTo(exporter)

            }
        }
    }

    /**
     * Gets the name of this data provider for logging purposes.
     *
     * @return The name of this recipe provider
     */
    override fun getName(): String {
        return "Vanilla Recipe Provider"
    }
}