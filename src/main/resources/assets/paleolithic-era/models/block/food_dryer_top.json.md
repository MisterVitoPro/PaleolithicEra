# Food Dryer Top Block Model  

**File:** `food_dryer_top.json`
**Type:** Block model definition

## Description
The 3D model for the top half of the Food Dryer structure, completing the frame and providing the upper hanging apparatus.

## Model Structure
This model continues the frame structure from the bottom block:

### Upper Frame Elements
- **Corner post extensions:** Continue the 4 corner posts from bottom block
- **Top connecting frame:** Horizontal beams connecting the posts at top
- **Cross hanging beam:** Central beam for additional hanging points

### Dimensions (in pixels, 0-16 coordinate system)
- Corner post extensions:
  - Front-left: (1,0,1) to (3,8,3)
  - Front-right: (13,0,1) to (15,8,3)
  - Back-left: (1,0,13) to (3,8,15) 
  - Back-right: (13,0,13) to (15,8,15)
- Top frame connecting posts:
  - Front: (1,6,1) to (15,8,3)
  - Back: (1,6,13) to (15,8,15)
  - Left: (1,6,3) to (3,8,13)
  - Right: (13,6,3) to (15,8,13)
- Central cross beam: (3,6,7) to (13,8,9)

## Textures
- **Wood texture:** Matching the bottom block texture
- **Weathered appearance:** Consistent with primitive theme
- **Frame joints:** Rope or binding details at connections

## Visual Design
- Completes the 2-block tall drying frame
- Open center area for hanging item visibility
- Top frame provides structural stability appearance
- Central beam adds additional hanging points

## Connection Requirements
- Must align perfectly with food_dryer.json bottom block
- Post positions must match exactly
- Overall height when combined should be 32 pixels (2 blocks)
- Visual continuity in frame construction

## Item Display Support
The combined structure should support item rendering at multiple hanging points throughout the full 2-block height.

## Notes
- This block is decorative/structural only - no block entity
- Should appear as natural extension of bottom frame
- Open design maintains visibility of drying items
- Frame should look capable of supporting hanging food items