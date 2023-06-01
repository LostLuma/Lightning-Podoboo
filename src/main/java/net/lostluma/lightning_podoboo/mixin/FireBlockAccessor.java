package net.lostluma.lightning_podoboo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;

@Mixin(FireBlock.class)
public interface FireBlockAccessor {
    @Accessor
    Object2IntMap<Block> getIgniteOdds();

    @Invoker("isNearRain")
    boolean invokeIsNearRain(Level world, BlockPos position);

    @Invoker("isValidFireLocation")
    boolean invokeIsValidFireLocation(BlockGetter world, BlockPos position);

    @Invoker("getFireTickDelay")
    static int invokeGetFireTickDelay(RandomSource random) {
        throw new AssertionError();
    }
}
