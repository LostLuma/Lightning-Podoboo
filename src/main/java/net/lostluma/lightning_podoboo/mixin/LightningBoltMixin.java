package net.lostluma.lightning_podoboo.mixin;

import net.lostluma.lightning_podoboo.CosmeticFireBlock;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LightningBolt.class)
public class LightningBoltMixin {
    @ModifyVariable(method = "spawnFire(I)V", at = @At("STORE"))
    private BlockState lightning_podoboo$spawnFireGetState(BlockState state) {
        if (((LightningBolt)(Object)this).getCause() != null) {
            return state;
        }

        return CosmeticFireBlock.copyBlockStateAttributes(state, CosmeticFireBlock.getInstance().defaultBlockState());
    }
}
