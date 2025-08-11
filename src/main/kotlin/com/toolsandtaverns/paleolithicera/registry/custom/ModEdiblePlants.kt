package com.toolsandtaverns.paleolithicera.registry.custom

import com.toolsandtaverns.paleolithicera.Constants.MOD_ID
import com.toolsandtaverns.paleolithicera.util.id
import com.toolsandtaverns.paleolithicera.util.regKeyOfItem
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.ConsumableComponent
import net.minecraft.component.type.FoodComponent
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Item
import net.minecraft.item.consume.ApplyEffectsConsumeEffect
import net.minecraft.item.consume.UseAction
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier

/**
 * Data definition for an edible plant item (berries/herbs).
 * This lets you add new entries without writing new classes.
 */
data class EdiblePlantDef(
    val idPath: String,                    // e.g., "elderberry", "yarrow"
    val hunger: Int,                       // half-shanks of hunger restored
    val saturationModifier: Float,         // vanilla-like saturation modifier
    val alwaysEdible: Boolean = false,     // if true, can eat at full hunger
    val effect: RegistryEntry<StatusEffect>? = null,      // optional status effect
    val effectDurationTicks: Int = 0,      // in ticks (20 = 1s)
    val effectAmplifier: Int = 0,          // 0 = level I
    val effectChance: Float = 1.0f,        // 0..1 probability
    val cookable: Boolean = false, // whether we should generate a campfire-cooking recipe
    val cookedIdPath: String? = null,      // optional cooked result id if different
    val maxStackSize: Int = 64             // typical for small foods
)

enum class EdiblePlants(val ediblePlantDef: EdiblePlantDef) {
    ELDERBERRY(EdiblePlantDef(
        idPath = "raw_elderberries",
        hunger = 4,
        saturationModifier = 0.3f,
        alwaysEdible = false,
        effect = StatusEffects.POISON,
        effectDurationTicks = 20 * 3,           // 3 seconds
        effectChance = 1.0f,
        cookable = true,
        cookedIdPath = "cooked_elderberries",    // set this to your cooked item id if you have one
        maxStackSize = 64
    )),
    YARROW(EdiblePlantDef(
            idPath = "yarrow_herb",
            hunger = 1,
            saturationModifier = 0.2f,
            alwaysEdible = false,
            effect = StatusEffects.REGENERATION,
            effectAmplifier = 0,
            effectDurationTicks = 20 * 4,           // 3 seconds
            effectChance = 1.0f,
            maxStackSize = 64
        )
    );

    val definitions: EdiblePlantDef = ediblePlantDef
}

/**
 * Central list: define all berries/herbs here.
 */
object ModEdiblePlants {

    /**
     * Create a FoodComponent from a definition.
     */
    private fun generateSettings(def: EdiblePlantDef): Item.Settings {
        val foodBuilder = FoodComponent.Builder()
            .nutrition(def.hunger)
            .saturationModifier(def.saturationModifier)
        if (def.alwaysEdible) foodBuilder.alwaysEdible()

        val consumableComponent = ConsumableComponent.builder()
            .consumeSeconds(1.6f)
            .useAction(UseAction.EAT)
            .sound(SoundEvents.ENTITY_GENERIC_EAT)
            .consumeParticles(true)

        if (def.effect != null && def.effectDurationTicks > 0) {
            consumableComponent
                .consumeEffect(
                    ApplyEffectsConsumeEffect(StatusEffectInstance(def.effect, def.effectDurationTicks, def.effectAmplifier), def.effectChance)
                )
        }

        return Item.Settings()
            .registryKey(regKeyOfItem(def.idPath))
            .component(DataComponentTypes.FOOD,foodBuilder.build())
            .component(DataComponentTypes.CONSUMABLE, consumableComponent.build())
    }

    /**
     * Registers all edible plant items into the item registry.
     * Returns a map from id path -> Item instance for convenience.
     */
    fun registerAll(): Map<EdiblePlants, Item> {
        val out = mutableMapOf<EdiblePlants, Item>()
        for (plant in EdiblePlants.entries) {
            val def = plant.definitions
            val item = Item(generateSettings(def).maxCount(def.maxStackSize))
            val id = id(def.idPath)
            val registered = Registry.register(Registries.ITEM, id, item)
            out[plant] = registered
        }
        return out
    }
}