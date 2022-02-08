package dev.lostluma.lightningpodoboo.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.lostluma.lightningpodoboo.LightningPodoboo;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin {
    @Shadow
    abstract boolean areBlocksAroundFlammable(BlockView world, BlockPos pos);

    @Redirect(
        method = "scheduledTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FireBlock;areBlocksAroundFlammable(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z")
    )
    private boolean scheduledTickAreBlocksAroundFlammable(FireBlock fireBlock, BlockView world, BlockPos pos) {
        return ((FireBlockMixin)(Object)Blocks.FIRE).areBlocksAroundFlammable(world, pos);
    }

    @Inject(
        method = "scheduledTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;hasHighHumidity(Lnet/minecraft/util/math/BlockPos;)Z"), cancellable = true
    )
    private void scheduledTickHasHighHumidity(BlockState state, ServerWorld world, BlockPos position, Random random, CallbackInfo callbackInfo) {
        if (state.isOf(LightningPodoboo.COSMETIC_FIRE_BLOCK)) {
            callbackInfo.cancel(); // Prevent natural fire spread
        }
    }
}
