package com.toolsandtaverns.paleolithicera.mixin;

import com.toolsandtaverns.paleolithicera.registry.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientBlockBreakBlockerMixin {

    @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"), cancellable = true)
    private void preventBreakingProgress(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        BlockState state = player.getWorld().getBlockState(pos);
        ItemStack heldItem = player.getMainHandStack();

        if (state.isIn(ModTags.Blocks.INSTANCE.getUNBREAKABLE_TAG()) && !(heldItem.getItem() instanceof AxeItem)) {
            cir.setReturnValue(false); // Stop animation and breaking from progressing
        }

        if (state.isIn(ModTags.Blocks.INSTANCE.getREQUIRES_SHOVEL()) && !(heldItem.getItem() instanceof ShovelItem)) {
            cir.setReturnValue(false);
        }
    }

}
