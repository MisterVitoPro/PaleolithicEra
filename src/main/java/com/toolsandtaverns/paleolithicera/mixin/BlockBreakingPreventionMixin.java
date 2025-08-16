package com.toolsandtaverns.paleolithicera.mixin;

import com.toolsandtaverns.paleolithicera.registry.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Predicate;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class BlockBreakingPreventionMixin {
    @Shadow
    @Final
    protected ServerPlayerEntity player;

    private static final HashMap<UUID, Long> lastWarnTime = new HashMap<>();
    @Unique
    private static final HashMap<UUID, Boolean> beenWarned = new HashMap<>();
    private static final long COOLDOWN_MS = 3000;

    @Inject(method = "processBlockBreakingAction", at = @At("HEAD"), cancellable = true)
    private void onBlockStart(
            BlockPos pos,
            PlayerActionC2SPacket.Action action,
            Direction direction,
            int worldHeight,
            int sequence,
            CallbackInfo ci
    ) {
        if (action != PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) return;

        BlockState state = player.getWorld().getBlockState(pos);
        ServerWorld world = player.getWorld();

        // Check UNBREAKABLE_TAG (axe required)
        checkToolRequirement(
                ModTags.Blocks.INSTANCE.getUNBREAKABLE_TAG(),
                itemStack -> itemStack.getItem() instanceof AxeItem,
                "message.paleolithic-era.need_axe",
                state, pos, world, ci
        );

        // Check REQUIRES_SHOVEL (shovel required)
        checkToolRequirement(
                ModTags.Blocks.INSTANCE.getREQUIRES_SHOVEL(),
                itemStack -> itemStack.getItem() instanceof ShovelItem,
                "message.paleolithic-era.need_shovel",
                state, pos, world, ci
        );
    }

    /**
     * Prevents block breaking and sends a message if the block is tagged and the tool predicate fails.
     */
    @Unique
    private void checkToolRequirement(
            TagKey<Block> tag,
            Predicate<ItemStack> validToolPredicate,
            String message,
            BlockState state,
            BlockPos pos,
            ServerWorld world,
            CallbackInfo ci
    ) {
        if (!state.isIn(tag)) return;

        ItemStack held = player.getMainHandStack();
        if (validToolPredicate.test(held)) return;

        // Cancel animation and reset block visual
        world.setBlockBreakingInfo(player.getId(), pos, -1);
        player.networkHandler.sendPacket(new BlockUpdateS2CPacket(pos, state));

        UUID id = player.getUuid();
        if (!beenWarned.getOrDefault(id, false)) {
            player.sendMessage(Text.translatable(message), true);
            beenWarned.put(id, true);
        }

        ci.cancel();
    }
}
