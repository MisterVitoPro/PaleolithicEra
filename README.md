# Paleolithic Era Mod
Welcome to the **Paleolithic Era Mod**, a Minecraft modification that transports you back to the ancient Stone Age! This mod integrates primitive tools, blocks, items, crafting mechanics, and survival elements designed for an immersive early civilization gameplay experience.

---

## Table of Contents
1. [Overview](#overview)
2. [Blocks](#blocks)
3. [Tools and Weapons](#tools-and-weapons)
4. [Special Items and Features](#special-items-and-features)
5. [Knapping Mechanics](#knapping-mechanics)
6. [Fishing Minigame (Harpoon)](#fishing-minigame-harpoon)
7. [Recipes](#recipes)
8. [Client-Side Features](#client-side-features)
9. [Advanced Configuration](#advanced-configuration)
10. [Additional Notes](#additional-notes)

---

## Overview
The Paleolithic Era Mod introduces early survival elements that let you explore crafting techniques, primitive tools, and hunting using primitive weaponry. The key goals of this mod are:
- Add realism and depth to early-game mechanics.
- Provide tools and blocks that emphasize *prehistoric survival*.
- Offer challenging and creative ways to collect resources, craft items, and manage their environmental impact.

---

## Blocks
This mod introduces new blocks to enrich your gameplay experience:

### 1. **Knapping Station**
- **Description**: The Knapping Station is a crafting block that allows players to create primitive tools and refine raw materials into usable forms.
- **Usage**: Right-click to open the crafting GUI, or sneak + right-click to manually knap.
- **Strength**: 2.0f (slightly less durable than stone blocks).
- **Features**: Uses recipes to convert input items (e.g., Flint, Bone) into useful outputs (e.g., spearheads, bifaces).
- **Crafting Example**:
  - Input: 1 Flint → Output: 1 Flint Biface.

---

### 2. **Crude Campfire**
- **Description**: A primitive cooking station essential for surviving the harsh early days.
- **Features**:
  - Cooks multiple items simultaneously.
  - Emits light with an intensity of `15` when lit.
  - Made of wooden materials for compatibility with the early game.
  - **Maximum Cooking Items**: Supports multiple items held in its internal inventory.

---

### 3. **Elderberry Bush**
- **Description**: A wild-growing bush that provides a new food source: **Elderberries**.
- **Behavior**:
  - Grows naturally in forests or plains.
  - Can be harvested to collect **Raw Elderberries**, which restore hunger and have other recipe uses.

---

## Tools and Weapons
### Primitive Toolsets
The mod features two new tool tiers designed for early survival:

#### 1. **Bone Tools** (Tier 0)
- **Durability**: 25
- **Mining Speed**: 0.5x (slower than wooden tools).
- **Attack Damage Bonus**: 0.2
- **Enchantability**: 3 (low enchantment potential).
- **Ideal For**: Cutting weak materials or soft blocks.

#### 2. **Flint Tools** (Tier 1)
- **Durability**: 40
- **Mining Speed**: 1.0x (same as wooden tools).
- **Attack Damage Bonus**: 0.5
- **Enchantability**: 5 (slightly better than Bone).
- **Best Use**: Ideal for upgrading basic damage and mining capabilities.

Both material types form the basis of Paleolithic weaponry and can be crafted from their respective recipes.

---

### Wooden Spear
- **Description**: A primitive javelin-style weapon usable for ranged combat.
- **Durability**: 45 (weaker than iron but sufficient for early-game usage).
- **Features**:
  - Throwable, much like a **Trident**, with customized velocity and attack mechanics.
  - Usable as either a ranged weapon or a melee item.
  - **Crafted From**: Flint + Stick + String.

---

### Wooden Harpoon
- **Description**: A tool for fishing through a fun minigame mechanic that simulates ancient hunting techniques.
- **Use**: Target a body of water (3x3 area of still water), and engage in a timing-focused fishing minigame.
- **Durability**: Limited; breaks after repeated use.
- **Advanced Features**:
  - Displays advanced durability information when F3+H is enabled.
  - Compatible only with proper water patches (flowing water or single blocks are invalid).

---

## Special Items and Features
- **Flint Biface**: Crafted from Flint, this tool functions as a primitive knife or cutting edge.
- **Bone Spearhead**: Crafted from Bones, essential for building throwable spears or adding sharp edges to primitive constructs.
- **Raw Elderberries**: A new food item that acts as an early-game nourishment source.

---

## Knapping Mechanics
Knapping is a **unique crafting process** where players use a **Knapping Station** to transform base materials into tools or useful items.

### How It Works:
1. Place a material (e.g., Flint or Bone) into the **Knapping Station** input slot.
2. **Right-click** or **Sneak + Right-click** to begin the crafting process.
3. Observe the crafting delay (20 ticks or 1 second).
4. Output items, such as **Flint Bifaces** or **Bone Spearheads**, will appear in the result slot.

#### Fine Details:
- Input stacks will **decrement by 1** per crafting cycle.
- The station will **halt** crafting if the output slot is full.

---

## Fishing Minigame (Harpoon)
The **Wooden Harpoon** introduces a fishing mechanic requiring timing and precision. When aiming at valid still water areas:
1. Right-click to trigger the fishing mechanism.
2. Engage in a reaction-based minigame to "catch" fish through precise inputs.
3. **Rewards**: Fish or other aquatic loot items.

#### Valid Water:
- A proper **3x3 patch of still, non-flowing water** is required.
- Flowing water, or water that lacks adjacent blocks, will fail validation.

---

## Recipes
### New Recipe Types
1. **Knapping Recipes**:
   - Recipes that refine materials into more sophisticated tools.
   - Example:
     - **Input**: Flint → **Output**: Flint Biface.
2. **Primitive Crafting**:
   - Craft Bone Tools, Flint Tools, and Wooden Weapons using sticks, string, and bones.

---

## Client-Side Features
### GUI Enhancements
- The Knapping Station features a **custom GUI** for enhanced crafting functionality.
- Fishing effects include visual and audio feedback when using the **Wooden Harpoon**.

### Block Render Layers
- **Crude Campfire**:
  - Emits light and features realistic transparency for embers and smoke.
- **Elderberry Bush**:
  - Designed with semi-transparent rendering for better visual integration with the environment.

---

## Advanced Configuration
For developers:
- Use the **KnappingRecipeProvider** for generating new crafting recipes procedurally.
- Expand primitive material tiers via the **ToolMaterialsMod** system, allowing for new metals or stones.

---

## Additional Notes
- The mod integrates seamlessly with **Fabric API**, ensuring compatibility with most modpack configurations.
- Extensible design encourages modders to add new blocks, tools, and mechanics within the same "prehistoric" theme.

Feel free to share suggestions or report bugs to improve the **Paleolithic Era Mod**!