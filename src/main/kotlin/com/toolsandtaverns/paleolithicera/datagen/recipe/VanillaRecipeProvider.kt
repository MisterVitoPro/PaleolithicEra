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
                // Bone Knife - diagonal crafting pattern with bone and stick
                createShaped(RecipeCategory.TOOLS, ModItems.BONE_KNIFE, 1)
                    .pattern(" B") // B = bone in top-right
                    .pattern("S ") // S = stick in bottom-left
                    .input('B', Items.BONE)
                    .input('S', Items.STICK)
                    .criterion(hasItem(Items.BONE), conditionsFromItem(Items.BONE))
                    .offerTo(exporter)

                // Bone Spear - diagonal design with bone spearhead at the tip
                createShaped(RecipeCategory.COMBAT, ModItems.BONE_SPEAR, 1)
                    .pattern("  B") // B = bone spearhead at top-right
                    .pattern(" S ") // S = stick in middle
                    .pattern("S  ") // S = stick at bottom-left
                    .input('B', ModItems.BONE_SPEARHEAD)
                    .input('S', Items.STICK)
                    .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem(ModItems.BONE_SPEARHEAD))
                    .offerTo(exporter)

                // Wooden Spear - simple diagonal pattern with sticks
                createShaped(RecipeCategory.COMBAT, ModItems.WOODEN_SPEAR, 1)
                    .pattern(" S") // S = stick in top-right
                    .pattern("S ") // S = stick in bottom-left
                    .input('S', Items.STICK)
                    .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                    .offerTo(exporter)

                // Fire Drill - crafted with sticks and plant fiber
                createShaped(RecipeCategory.TOOLS, ModItems.FIRE_DRILL, 1)
                    .pattern("SF") // S = stick, F = plant fiber in top row
                    .pattern("SS") // SS = two sticks in bottom row
                    .input('S', Items.STICK)
                    .input('F', ModItems.PLANT_FIBER)
                    .criterion(hasItem(Items.CRAFTING_TABLE), conditionsFromItem(ModItems.PLANT_FIBER))
                    .offerTo(exporter)

                // Crude Campfire - primitive version of the vanilla campfire
                createShaped(RecipeCategory.MISC, ModBlocks.CRUDE_CAMPFIRE, 1)
                    .pattern("SF") // S = stick, F = plant fiber in top row
                    .pattern("BB") // BB = two bark items in bottom row
                    .input('S', Items.STICK)
                    .input('B', ModItems.BARK)
                    .input('F', ModItems.PLANT_FIBER)
                    // Unlock recipe when player has both bark and plant fiber
                    .criterion(hasItem(ModItems.BARK), conditionsFromItem(ModItems.BARK))
                    .criterion(hasItem(ModItems.PLANT_FIBER), conditionsFromItem(ModItems.PLANT_FIBER))
                    .offerTo(exporter)

                // Knapping Station - crafting block for stone tools
                createShaped(RecipeCategory.MISC, ModBlocks.KNAPPING_STATION, 1)
                    .pattern("SS") // SS = two sticks in top row
                    .pattern("RR") // RR = two rock chunks in bottom row
                    .input('S', Items.STICK)
                    .input('R', ModItems.ROCK_CHUNK)
                    .criterion(hasItem(ModItems.ROCK_CHUNK), conditionsFromItem { ModItems.ROCK_CHUNK })
                    .offerTo(exporter)

                // Smelting recipe for elderberries in a furnace
                CookingRecipeJsonBuilder.createSmelting(
                    Ingredient.ofItems(ModItems.RAW_ELDERBERRIES), // Input item
                    RecipeCategory.FOOD,                          // Recipe category for JEI/REI
                    ModItems.COOKED_ELDERBERRIES,                // Output item
                    0.35f,                                       // Experience given
                    200                                          // Cook time in ticks (10 seconds)
                ).criterion("has_raw_elderberries", InventoryChangedCriterion.Conditions.items(ModItems.RAW_ELDERBERRIES))
                    .offerTo(exporter, RegistryKey.of(RegistryKeys.RECIPE,Identifier.of(MOD_ID, "smelt_elderberries")))

                // Campfire cooking recipe for elderberries (slower than furnace but no fuel needed)
                CookingRecipeJsonBuilder.createCampfireCooking(
                    Ingredient.ofItems(ModItems.RAW_ELDERBERRIES), // Input item
                    RecipeCategory.FOOD,                          // Recipe category for JEI/REI
                    ModItems.COOKED_ELDERBERRIES,                // Output item
                    0.35f,                                       // Experience given
                    600                                          // Cook time in ticks (30 seconds)
                ).criterion("has_raw_elderberries", InventoryChangedCriterion.Conditions.items(ModItems.RAW_ELDERBERRIES))
                    .offerTo(exporter, RegistryKey.of(RegistryKeys.RECIPE,Identifier.of(MOD_ID, "campfire_cook_elderberries")))

                // Rawhide Tunic (chestplate) - crafted with rawhide and plant fiber
                createShaped(RecipeCategory.COMBAT, ModItems.RAWHIDE_TUNIC, 1)
                    .pattern("RF") // R = rawhide, F = plant fiber in top row
                    .pattern("RR") // RR = two rawhide in bottom row
                    .input('R', ModItems.RAWHIDE)
                    .input('F', ModItems.PLANT_FIBER)
                    .criterion(hasItem(ModItems.RAWHIDE), conditionsFromItem(ModItems.RAWHIDE))
                    .offerTo(exporter)

                // Rawhide Leggings - crafted with rawhide and plant fiber
                createShaped(RecipeCategory.COMBAT, ModItems.RAWHIDE_LEGGINGS, 1)
                    .pattern("RR") // RR = two rawhide in top row
                    .pattern("FF") // FF = two plant fiber in bottom row
                    .input('R', ModItems.RAWHIDE)
                    .input('F', ModItems.PLANT_FIBER)
                    .criterion(hasItem(ModItems.RAWHIDE), conditionsFromItem(ModItems.RAWHIDE))
                    .offerTo(exporter)

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