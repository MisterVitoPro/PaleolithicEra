package com.toolsandtaverns.paleolithicera.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.registry.Registries;
import net.minecraft.block.Block;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.UUID;

import static com.toolsandtaverns.paleolithicera.Constants.MOD_ID;

/**
 * Makes certain blocks unharvestable, simulating "punching forever".
 * Blocks are identified via a tag (e.g. paleolithic-era:unbreakable_without_tool).
 */
@Mixin(ServerPlayerInteractionManager.class)
public abstract class LogPunchForeverMixin {

    @Final
    @Shadow
    protected ServerPlayerEntity player;

    private static final TagKey<Block> UNBREAKABLE_TAG = TagKey.of(Registries.BLOCK.getKey(), Identifier.of(MOD_ID, "unbreakable_without_tool"));
    private static final HashMap<UUID, Long> lastWarnTime = new HashMap<>();
    private static final long COOLDOWN_MS = 3000; // 3 seconds

    /**
     * Prevents mining progress from increasing if block is tagged as unbreakable.
     */
    @ModifyExpressionValue(
            method = "continueMining",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;calcBlockBreakingDelta(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F")
    )
    private float preventHarvestingCertainBlocks(float original, BlockState state, BlockPos pos, int failedStartMiningTime) {
        if (state.isIn(UNBREAKABLE_TAG)) {
            long now = System.currentTimeMillis();
            long last = lastWarnTime.getOrDefault(player.getUuid(), 0L);
            if (now - last > COOLDOWN_MS) {
                player.sendMessage(Text.literal("You cannot harvest this block yet."), true);
                lastWarnTime.put(player.getUuid(), now);
            }
            return 0.0F; // No progress at all
        }
        return original;
    }
}
