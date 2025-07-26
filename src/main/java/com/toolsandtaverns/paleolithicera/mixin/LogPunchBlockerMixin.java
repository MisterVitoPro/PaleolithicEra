package com.toolsandtaverns.paleolithicera.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.UUID;

/**
 * Prevents players from breaking logs unless they are using an axe.
 * Displays a one-time message with cooldown to avoid spam.
 */
@Mixin(ServerPlayerInteractionManager.class)
public abstract class LogPunchBlockerMixin {

    @Final
    @Shadow
    protected ServerPlayerEntity player;

    @Unique
    private static final HashMap<UUID, Long> lastWarnTime = new HashMap<>();
    @Unique
    private static final long COOLDOWN_MS = 3000; // 3 seconds

    @Inject(method = "tryBreakBlock", at = @At("HEAD"), cancellable = true)
    private void preventLogBreakingWithoutAxe(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = player.getWorld().getBlockState(pos);
        Block block = state.getBlock();
        ItemStack heldItem = player.getMainHandStack();

        if (block.getDefaultState().isIn(BlockTags.LOGS)) {
            if (!(heldItem.getItem() instanceof AxeItem)) {
                long now = System.currentTimeMillis();
                long last = lastWarnTime.getOrDefault(player.getUuid(), 0L);

                if (now - last > COOLDOWN_MS) {
                    player.sendMessage(Text.literal("You need an axe to harvest logs."), true);
                    lastWarnTime.put(player.getUuid(), now);
                }

                cir.setReturnValue(false);
            }
        }
    }
}
