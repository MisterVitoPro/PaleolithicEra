# Food Dryer Block Model

**File:** `food_dryer.json`
**Type:** Block model definition

## Description
The 3D model for the bottom half of the Food Dryer structure, featuring a frame-like design for hanging and drying food items.

## Model Structure
The food dryer should be a 2-block tall structure with the bottom block containing:

### Base Elements
- **Platform base:** 12x2x12 block base (slightly inset from edges)
- **Four corner posts:** 2x14x2 blocks extending upward
- **Cross beams:** Horizontal supports for item hanging at height 12-14

### Dimensions (in pixels, 0-16 coordinate system)
- Base platform: (2,0,2) to (14,2,14)
- Corner posts: 
  - Front-left: (1,2,1) to (3,16,3)
  - Front-right: (13,2,1) to (15,16,3) 
  - Back-left: (1,2,13) to (3,16,15)
  - Back-right: (13,2,13) to (15,16,15)
- Cross beams:
  - Front: (1,12,3) to (15,14,5)
  - Back: (1,12,11) to (15,14,13)

## Textures
- **Wood texture:** Rustic wooden planks/logs
- **Frame elements:** Darker wood or rope bindings
- **Weathered appearance:** Suitable for primitive crafting station

## Visual Design
- Sturdy frame construction
- Open design allowing visibility of hanging items
- Connects visually with top block (food_dryer_top.json)
- Should look handcrafted/primitive

## Item Hanging Points
Model should support 4 hanging positions for the block entity renderer:
- Front-left: (0.25, 0.85, 0.35)
- Front-right: (0.75, 0.85, 0.35)
- Back-left: (0.25, 0.85, 0.65) 
- Back-right: (0.75, 0.85, 0.65)

## Notes
- Must align properly with food_dryer_top model
- Leave space for item rendering between cross beams
- Frame design should suggest hanging/drying functionality