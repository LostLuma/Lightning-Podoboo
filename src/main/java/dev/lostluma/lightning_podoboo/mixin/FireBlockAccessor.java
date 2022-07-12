package dev.lostluma.lightning_podoboo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.Block;
import net.minecraft.block.FireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@Mixin(FireBlock.class)
public interface FireBlockAccessor {
    @Accessor
    Object2IntMap<Block> getBurnChances();

    @Invoker("isRainingAround")
    boolean invokeIsRainingAround(World world, BlockPos pos);

    @Invoker("areBlocksAroundFlammable")
    boolean invokeAreBlocksAroundFlammable(BlockView world, BlockPos pos);

    @Invoker("getFireTickDelay")
    static int invokeGetFireTickDelay(Random random) {
        throw new  AssertionError();
    }
}
