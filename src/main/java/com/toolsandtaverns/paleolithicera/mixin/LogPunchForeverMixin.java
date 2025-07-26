package com.toolsandtaverns.paleolithicera.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import static com.toolsandtaverns.paleolithicera.util.RegistryHelpersKt.tagKeyOfBlock;

/**
 * Makes certain blocks unharvestable, simulating "punching forever".
 * Blocks are identified via a tag (e.g. paleolithic-era:unbreakable_without_tool).
 */
@Mixin(ServerPlayerInteractionManager.class)
public abstract class LogPunchForeverMixin {

    @Final
    @Shadow
    protected ServerPlayerEntity player;

    @Unique
    private static final TagKey<Block> UNBREAKABLE_TAG = tagKeyOfBlock("unbreakable_without_tool");

    /**
     * Prevents mining progress from increasing if block is tagged as unbreakable.
     */
    @ModifyExpressionValue(
            method = "continueMining",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;calcBlockBreakingDelta(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F"
            )
    )
    private float preventBreakingRestrictedBlocks(
            float original,
            BlockState state,
            BlockPos pos,
            int failedStartMiningTime
    ) {
        if (state.isIn(UNBREAKABLE_TAG)) {
            return 0.0f; // Prevent progress
        }
        return original;
    }
}
