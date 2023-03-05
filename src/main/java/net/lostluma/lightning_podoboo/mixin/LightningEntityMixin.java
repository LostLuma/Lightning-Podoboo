package net.lostluma.lightning_podoboo.mixin;

import net.lostluma.lightning_podoboo.CosmeticFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LightningEntity.class)
public class LightningEntityMixin {
    @Shadow
    @Nullable
    private ServerPlayerEntity channeler;

    @ModifyVariable(method = "spawnFire(I)V", at = @At("STORE"))
    private BlockState lightning_podoboo$spawnFireGetState(BlockState state) {
        if (this.channeler != null) {
            return state;
        }

        return CosmeticFireBlock.copyBlockStateAttributes(state, CosmeticFireBlock.getInstance().getDefaultState());
    }
}
