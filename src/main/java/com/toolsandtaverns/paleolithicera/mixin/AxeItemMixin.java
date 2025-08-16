package com.toolsandtaverns.paleolithicera.mixin;

import com.toolsandtaverns.paleolithicera.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemMixin {

    /**
     * Inject after a block has been successfully stripped to also drop bark.
     */
    @Inject(
            method = "tryStrip",
            at = @At("RETURN")
    )
    private void tryStripping(World world, BlockPos pos, @Nullable PlayerEntity player, BlockState state, CallbackInfoReturnable<Optional<BlockState>> cir) {
        if (!world.isClient && cir.getReturnValue().isPresent()) {
            if (world instanceof ServerWorld) {
                ItemStack bark = new ItemStack(ModItems.INSTANCE.getBARK(), 1);
                BlockPos dropPos = pos.offset(Direction.UP, 0); // Drop at the log's position
                Block.dropStack(world, dropPos, bark);
            }
        }
    }
}
