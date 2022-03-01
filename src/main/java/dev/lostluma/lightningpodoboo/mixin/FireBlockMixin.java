package dev.lostluma.lightningpodoboo.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.lostluma.lightningpodoboo.CosmeticFireBlock;
import dev.lostluma.lightningpodoboo.LightningPodoboo;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

@Mixin(FireBlock.class)
public abstract class FireBlockMixin {
    @Shadow @Final
    private Object2IntMap<Block> burnChances;

    @Shadow
    abstract protected boolean isFlammable(BlockState state);

    @Redirect(
        method = "getBurnChance(Lnet/minecraft/block/BlockState;)I",
        at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2IntMap;getInt(Ljava/lang/Object;)I")
    )
    private int useFireBlocksFlammableMaps(Object2IntMap<Block> burnChances, Object object) {
        if ((Object)this instanceof CosmeticFireBlock) {
            return ((FireBlockMixin)(Object)Blocks.FIRE).burnChances.getInt(object);
        }

        return burnChances.getInt(object);
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

    @Redirect(
        method = "scheduledTick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FireBlock;isFlammable(Lnet/minecraft/block/BlockState;)Z")
    )
    private boolean alwaysExtingishCosmeticFire(FireBlock fireBlock, BlockState state) {
        return ((Object)this instanceof CosmeticFireBlock) ? false : this.isFlammable(state);
    }
}
