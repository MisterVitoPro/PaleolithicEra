# Food Dryer Item Model

**File:** `food_dryer.json`
**Type:** Item model definition  

## Description
The item model for the Food Dryer block when held in inventory or dropped as an item.

## Model Structure
Simple item model that represents the Food Dryer block in 2D inventory form.

## Required Properties
```json
{
  "parent": "item/generated",
  "textures": {
    "layer0": "paleolithic-era:item/food_dryer"
  }
}
```

## Texture Requirements
- **Texture file:** `textures/item/food_dryer.png`
- **Size:** 16x16 pixels
- **Style:** Simple icon representation of the drying frame

## Visual Design
- Top-down or isometric view of the drying frame
- Clear indication of the 4-slot layout
- Wooden frame appearance
- Simple, recognizable icon suitable for inventory slots

## Icon Elements
- Outer wooden frame border
- 4 distinct areas representing the drying slots
- Optional: small food items or hanging indicators
- Earth tone color palette matching the block

## Notes
- Should be instantly recognizable as a food preservation device
- Must be clear and readable at 16x16 pixel size
- Style should match other tool/station icons in the mod
- Consider showing simplified frame structure from above