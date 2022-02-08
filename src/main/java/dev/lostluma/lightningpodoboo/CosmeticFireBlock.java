package dev.lostluma.lightningpodoboo;

import eu.pb4.polymer.api.block.PolymerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class CosmeticFireBlock extends FireBlock implements PolymerBlock {
    public CosmeticFireBlock(Settings settings) {
        super(settings);

        BlockState defaultState = stateManager.getDefaultState();

        for (BooleanProperty direction : ConnectingBlock.FACING_PROPERTIES.values()) {
            if (direction != ConnectingBlock.DOWN) {
                defaultState = defaultState.with(direction, false);
            }
        }

        setDefaultState(defaultState.with(AGE, 0));
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        return Blocks.FIRE;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return copyBlockStateDirections(state, getPolymerBlock(state).getDefaultState());
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return Blocks.FIRE.canPlaceAt(state, world, pos);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        BlockState updated = super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        return !updated.isOf(Blocks.FIRE) ? updated : copyBlockStateDirections(updated, getDefaultState());
    }

    public static BlockState copyBlockStateDirections(BlockState source, BlockState target) {
        for (BooleanProperty direction : ConnectingBlock.FACING_PROPERTIES.values()) {
            if (direction != ConnectingBlock.DOWN) {
                target = target.with(direction, source.get(direction));
            }
        }

        return target.with(FireBlock.AGE, source.get(FireBlock.AGE)); // Age is necessary for neightbor update
    }
}
