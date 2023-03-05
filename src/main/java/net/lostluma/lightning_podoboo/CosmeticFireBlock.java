package net.lostluma.lightning_podoboo;

import java.util.Map;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.lostluma.lightning_podoboo.mixin.FireBlockAccessor;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class CosmeticFireBlock extends AbstractFireBlock implements PolymerBlock {
    private static final IntProperty AGE = Properties.AGE_15;
    private static final Map<Direction, BooleanProperty> DIRECTION_PROPERTIES = ConnectingBlock.FACING_PROPERTIES.entrySet().stream().filter(entry -> entry.getKey() != Direction.DOWN).collect(Util.toMap());

    // Block Settings were copied from vanilla FireBlock instantiation
    private static final CosmeticFireBlock COSMETIC_FIRE_BLOCK = new CosmeticFireBlock(AbstractBlock.Settings.of(Material.FIRE, MapColor.BRIGHT_RED).noCollision().breakInstantly().luminance(state -> 15).sounds(BlockSoundGroup.WOOL));

    private CosmeticFireBlock(Settings settings) {
        super(settings, 1.0f);

        BlockState defaultState = stateManager.getDefaultState();

        for (BooleanProperty direction : DIRECTION_PROPERTIES.values()) {
            defaultState = defaultState.with(direction, false);
        }

        setDefaultState(defaultState.with(AGE, 0));
    }

    public static CosmeticFireBlock getInstance() {
        return COSMETIC_FIRE_BLOCK;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.AGE_15);

        for (BooleanProperty direction : DIRECTION_PROPERTIES.values()) {
            builder.add(direction);
        }
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return Blocks.FIRE;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return copyBlockStateAttributes(state, Blocks.FIRE.getDefaultState());
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return Blocks.FIRE.canPlaceAt(state, world, pos);
    }

    @Override
    protected boolean isFlammable(BlockState state) {
        // Used by randomDisplayTick
        // To create random smoke particles on flammable surfaces
        return ((FireBlockAccessor)Blocks.FIRE).getBurnChances().getInt(state) > 0;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!entity.isFireImmune()) {
            entity.setFireTicks(entity.getFireTicks() + 1);
            if (entity.getFireTicks() <= 0) {
                entity.setOnFireFor(8);
            }
            entity.damage(world.getDamageSources().inFire(), 1.0f);
        }

        // super.onEntityCollision(state, world, pos, entity);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos position, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, position, oldState, notify);
        world.scheduleBlockTick(position, this, FireBlockAccessor.invokeGetFireTickDelay(world.random));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return ensureCosmeticFire(Blocks.FIRE.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos));
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos position, Random random) {
        // This is an adapted version of FireBlock's scheduledTick method
        // Removing the infinyburn check as well as the placement of new fire
        world.scheduleBlockTick(position, this, FireBlockAccessor.invokeGetFireTickDelay(world.random));

        if (!state.canPlaceAt(world, position)) {
            world.removeBlock(position, false);
        }

        if (!world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            return;
        }

        int age = state.get(AGE);

        if (world.isRaining() && ((FireBlockAccessor)Blocks.FIRE).invokeIsRainingAround(world, position) && random.nextFloat() < 0.2f + (float)age * 0.03f) {
            world.removeBlock(position, false);
            return;
        }

        int newAge = Math.min(15, age + random.nextInt(3) / 2);

        if (age != newAge) {
            state = (BlockState)state.with(AGE, newAge);
            world.setBlockState(position, state, Block.NO_REDRAW);
        }

        if (!((FireBlockAccessor)Blocks.FIRE).invokeAreBlocksAroundFlammable(world, position)) {
            BlockPos blockPos = position.down();
            if (!world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, Direction.UP) || age > 3) {
                world.removeBlock(position, false);
            }
            return;
        }

        if (age == 15 && random.nextInt(4) == 0 && !this.isFlammable(world.getBlockState(position.down()))) {
            world.removeBlock(position, false);
        }
    }

    private BlockState ensureCosmeticFire(BlockState state) {
        return state.isAir() ? state : copyBlockStateAttributes(state, getDefaultState());
    }

    public static BlockState copyBlockStateAttributes(BlockState source, BlockState target) {
        for (BooleanProperty direction : DIRECTION_PROPERTIES.values()) {
            target = target.with(direction, source.get(direction));
        }

        return target.with(AGE, source.get(AGE));
    }
}
