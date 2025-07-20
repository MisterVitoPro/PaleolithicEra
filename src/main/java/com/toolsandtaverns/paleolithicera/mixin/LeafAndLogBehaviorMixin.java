package com.toolsandtaverns.paleolithicera.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.entity.ItemEntity;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Prevents punching logs with an empty hand and adds a 30% stick drop to leaf breaking.
 */
@Mixin(ServerPlayerInteractionManager.class)
public abstract class LeafAndLogBehaviorMixin {

    @Final
    @Shadow
    protected ServerPlayerEntity player;

    /**
     * Adds a 30% chance of dropping a stick when breaking any leaves block.
     */
    @Inject(method = "tryBreakBlock", at = @At("RETURN"))
    private void addStickDrop(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        ServerWorld world = player.getWorld();
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof LeavesBlock) {
            Random random = world.getRandom();
            if (random.nextFloat() < 0.3f) {
                ItemEntity itemEntity = new ItemEntity(
                        world,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        new ItemStack(Items.STICK)
                );
                world.spawnEntity(itemEntity);
            }
        }
    }
}
