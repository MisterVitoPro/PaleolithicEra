package com.toolsandtaverns.paleolithicera.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
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
        if (state.getBlock() instanceof LeavesBlock) { // TODO this is air because its after the block broke
            Random random = world.getRandom();
            if (random.nextFloat() < 0.3f) {
                BlockPos dropPos = pos.offset(Direction.UP, 0);
                ItemStack dropStack = new ItemStack(Items.STICK,1);
                Block.dropStack(world, dropPos, dropStack);
            }
        }
    }
}
