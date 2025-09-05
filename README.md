# Paleolithic Era Mod

Welcome to **Paleolithic Era** — a Fabric mod that drops Minecraft’s opening hours into the Stone Age. You’ll craft by knapping, cook on crude fires, forage wild plants, and hunt with primitive weapons while working your way toward early settlement.

> **Mod ID:** `paleolithic-era`

---

## Contents
- [Overview](#overview)
- [World & Survival Content](#world--survival-content)
  - [Blocks & Stations](#blocks--stations)
  - [Wild Plants & Food](#wild-plants--food)
  - [Animals](#animals)
- [Tools & Weapons](#tools--weapons)
- [Core Systems](#core-systems)
  - [Knapping](#knapping)
  - [Harpoon Fishing Minigame](#harpoon-fishing-minigame)
- [Recipes & Data](#recipes--data)
- [World Generation](#world-generation)
- [Client/Visuals](#clientvisuals)
- [Developer Notes](#developer-notes)

---

## Overview
**Paleolithic Era** expands the early game with believable, low-tech progression:
- Gather sticks, fiber, berries, flint, bone and rock chunks.
- Shape parts at a **Knapping Station**.
- Light a **Crude Campfire** and cook basic food.
- Hunt with **wooden spears/harpoons** and upgrade into bone/flint toolsets.
- Forage **elderberries** and **yarrow** to survive and craft.
- Encounter early fauna like **Boar** roaming the wilds.

The design aim is to create meaningful choices and a grounded start that still plays smoothly in modpacks.

---

## World & Survival Content

### Blocks & Stations
- **Knapping Station** – Compact work surface for shaping flint/bone into tool parts (bifaces, spearheads). Has a short action delay and basic input/output slots. Designed for early-game recipes.
- **Crude Campfire** – Primitive heat/cooking source with light emission; supports multiple items and early survival cooking.
- **Crude Bed** – Rough, early bedding with custom texturing and recipe variants (intended as a first shelter milestone before proper beds).

### Wild Plants & Food
- **Elderberry Bush** – Naturally generates and yields **Raw Elderberries** (edible, used in recipes). Elderberry harvesting uses proper loot tables.
- **Yarrow (Herb)** – A wild plant with growth stages, drops/seeds, and early-game food/herbal use. Integrated into recipes and data generation.

### Animals
- **Boar** – A sturdy early-game animal that roams the overworld. Interacts with elderberries (can be fed/bred) and fits into a foraging/hunting loop.

---

## Tools & Weapons
- **Primitive Toolsets**
  - **Bone Tools (Tier 0)** – Extremely low durability, slower mining; useful stepping stone.
  - **Flint Tools (Tier 1)** – Better durability/speed than bone; establishes a proper early tier.
- **Wooden Spear** – Throwable javelin/melee hybrid with tuned handling for early combat.
- **Wooden Harpoon** – Enables fishing via a timing-based minigame on valid 3×3 still water.
- **Parts & Components** – **Flint Biface**, **Bone Spearhead**, etc., crafted via knapping and used across recipes.

---

## Core Systems

### Knapping
Perform quick shaping actions on the **Knapping Station**:
1. Insert a base material (flint, bone, etc.).
2. Activate to knap (short delay per action).
3. Receive parts like **Flint Bifaces** or **Bone Spearheads**.

Notes:
- Consumes one input per operation.
- Halts if the output is full.

### Harpoon Fishing Minigame
- Requires a valid **3×3 still-water** patch (no flowing/singles).
- Right-click to start; succeed with timing/precision to catch fish and aquatic loot.
- Harpoons show advanced durability info with F3+H.

---

## Recipes & Data
- **Knapping** – Source → shaped parts (e.g., flint → **Flint Biface**).
- **Primitive Crafting** – Sticks/strings/bones/flint combine into early tools and weapons.
- **Food** – Early cooking paths on the **Crude Campfire**; berries and herbs support basic survival.
- **Data Generation** – Providers cover recipes/loot for new plants (e.g., yarrow), and elderberry harvesting is driven by loot tables.

---

## World Generation
- **Wild Crops** – **Elderberry Bushes** and **Yarrow** appear in suitable biomes as foraging targets.
- **Fauna** – **Boar** added to biome spawn lists with sensible weights for early exploration.

> Worldgen targets and spawn weights are tuned for a survival-forward early game without cluttering vanilla terrain.

---

## Client/Visuals
- Custom GUI for **Knapping Station**.
- Render layers for foliage (bush transparency) and **Crude Campfire** visuals.
- New textures for **Crude Bed** and plant growth stages (e.g., yarrow).

---

## Testing

This project includes a comprehensive test suite to ensure reliability and prevent regressions. Tests are organized by priority levels and can be run individually or in groups.

### Test Priority Levels

- **P0 (Critical)**: Essential functionality that must always work (mod initialization, core mechanics)
- **P1 (Important)**: Important functionality that should work reliably (UI, entities, world generation)  
- **P2 (Nice-to-have)**: Additional functionality and edge cases (particle effects, advanced interactions)

### Running Tests

#### Run All Tests
```bash
./gradlew test
```

#### Run Tests by Priority Level
```bash
# Critical tests only - fastest feedback
./gradlew testP0

# Important tests only
./gradlew testP1

# Nice-to-have tests only  
./gradlew testP2

# Quick tests (P0 + unit tests) - good for development
./gradlew testQuick
```

#### Run Tests by Type
```bash
# Unit tests only
./gradlew testUnit

# Integration tests only
./gradlew testIntegration

# Performance tests only
./gradlew testPerformance
```

#### Run Tests with Detailed Output
```bash
./gradlew test --info
./gradlew testP0 --info
```

### Test Coverage

The test suite covers:

#### P0 (Critical) Tests
- Mod initialization and registration
- Item registry functionality  
- Core knapping station mechanics
- World progression system
- Basic inventory operations

#### P1 (Important) Tests
- Spear item functionality and combat mechanics
- Loot modifier systems (plant fiber, rock chunks, mob drops)
- Recipe and crafting workflows
- Entity behaviors (boar, ibex)
- Screen handlers and UI components

#### P2 (Nice-to-have) Tests
- Advanced entity interactions
- Particle effects and rendering components
- Data generation validation
- Performance characteristics
- Edge case handling

#### Integration Tests
- Complete knapping workflow from materials to tools
- Progression system integration with gameplay mechanics
- Multi-system interactions and error handling
- Resource management and scarcity scenarios

#### Performance Tests  
- Inventory operation performance
- Knapping action timing
- Memory usage stability
- Concurrent access handling
- Scalability verification

### Test Development Guidelines

When adding new features:
1. Write P0 tests for critical functionality first
2. Add P1 tests for important user-facing features  
3. Include P2 tests for edge cases and nice-to-have scenarios
4. Create integration tests for multi-system features
5. Add performance tests for resource-intensive operations

### Continuous Integration

The test suite is designed to work in CI environments:
- `testP0` for quick feedback on pull requests
- `test` for full validation before merging
- `testPerformance` for performance regression detection

## Developer Notes
- **Mod ID:** `paleolithic-era`
- **Language/Stack:** Fabric + Kotlin
- **Entrypoints:** main, client, and datagen are registered.
- **Data:** Loot tables and recipe providers are in place for plants (elderberries/yarrow) and early items.
- **Worldgen hooks:** Biome modifications register plant features and entity spawns.
- **Testing:** Comprehensive test suite with tagged priorities (P0/P1/P2) for reliable development and CI/CD

---

## Roadmap Hints
This project aims to:
- flesh out "Stage 0: The Awakening"
- introduce more Paleolithic structures (tanning racks, World POI huts)
- more Herbs with status effects that spawn in the world 
- effigies/totems that give radial effects to players
- Another animal to hunt
- Improve Boars to attack when scared

---

### Credits & Links
- MIT Licensed.
- BlockBench used for models