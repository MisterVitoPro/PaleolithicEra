package com.toolsandtaverns.paleolithicera.mixin;

import com.toolsandtaverns.paleolithicera.PaleolithicEra;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.UUID;

import static com.toolsandtaverns.paleolithicera.util.RegistryHelpersKt.tagKeyOfBlock;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class BlockBreakingPreventionMixin {
    @Shadow
    @Final
    protected ServerPlayerEntity player;

    private static final TagKey<Block> UNBREAKABLE_TAG = tagKeyOfBlock("unbreakable_without_tool");

    private static final HashMap<UUID, Long> lastWarnTime = new HashMap<>();
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
        ItemStack held = player.getMainHandStack();
        Item item = held.getItem();

        if (state.isIn(UNBREAKABLE_TAG) && !(item instanceof AxeItem)) {
            ServerWorld world = (ServerWorld) player.getWorld();

            // Immediately clear damage animation
            world.setBlockBreakingInfo(player.getId(), pos, -1);

            // Reset visual state of the block
            player.networkHandler.sendPacket(new BlockUpdateS2CPacket(pos, state));

            // One-time feedback message
            UUID id = player.getUuid();
            long now = System.currentTimeMillis();
            if (now - lastWarnTime.getOrDefault(id, 0L) > COOLDOWN_MS) {
                player.sendMessage(Text.literal("You need an axe to harvest logs."), true);
                lastWarnTime.put(id, now);
            }

            ci.cancel();
        }
    }
}
