package net.lostluma.lightning_podoboo;

import java.util.Map;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.lostluma.lightning_podoboo.mixin.FireBlockAccessor;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class CosmeticFireBlock extends BaseFireBlock implements PolymerBlock {
    private static final IntegerProperty AGE = BlockStateProperties.AGE_15;
    private static final Map<Direction, BooleanProperty> DIRECTION_PROPERTIES = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter(entry -> entry.getKey() != Direction.DOWN).collect(Util.toMap());

    // Block Settings were copied from vanilla FireBlock instantiation
    private static final CosmeticFireBlock COSMETIC_FIRE_BLOCK = new CosmeticFireBlock(BlockBehaviour.Properties.of(Material.FIRE, MaterialColor.FIRE).noCollission().instabreak().lightLevel(state -> 15).sound(SoundType.WOOL));

    /*
    private static final CosmeticFireBlock COSMETIC_FIRE_BLOCK = new CosmeticFireBlock(
        AbstractBlock.Settings.create().mapColor(MapColor.BRIGHT_RED).replaceable().noCollision().breakInstantly().luminance(state -> 15).sounds(BlockSoundGroup.WOOL).pistonBehavior(PistonBehavior.DESTROY)
    );
     */

    private CosmeticFireBlock(Properties properties) {
        super(properties, 1.0f);

        BlockState defaultState = this.stateDefinition.any();

        for (BooleanProperty direction : DIRECTION_PROPERTIES.values()) {
            defaultState = defaultState.setValue(direction, false);
        }

        registerDefaultState(defaultState.setValue(AGE, 0));
    }

    public static CosmeticFireBlock getInstance() {
        return COSMETIC_FIRE_BLOCK;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);

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
        return copyBlockStateAttributes(state, Blocks.FIRE.defaultBlockState());
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return Blocks.FIRE.canSurvive(state, world, pos);
    }

    @Override
    protected boolean canBurn(BlockState state) {
        // Used by animateTick
        // To create random smoke particles on flammable surfaces
        return ((FireBlockAccessor)Blocks.FIRE).getIgniteOdds().getInt(state) > 0;
    }

    @Override
	public void entityInside(BlockState state, Level world, BlockPos position, Entity entity) {
        if (!entity.fireImmune()) {
            entity.setRemainingFireTicks(entity.getRemainingFireTicks() + 1);
            if (entity.getRemainingFireTicks() <= 0) {
                entity.setSecondsOnFire(8);
            }
            entity.hurt(world.damageSources().inFire(), 1.0f);
        }

        // super.entityInside(state, world, pos, entity);
    }

    @Override
	public void onPlace(BlockState state, Level world, BlockPos position, BlockState oldState, boolean notify) {
        super.onPlace(state, world, position, oldState, notify);
        world.scheduleTick(position, this, FireBlockAccessor.invokeGetFireTickDelay(world.random));
    }

    @Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos position, BlockPos neighborPos) {
        return ensureCosmeticFire(Blocks.FIRE.updateShape(state, direction, neighborState, level, position, neighborPos));
    }

    @Override
	public void tick(BlockState state, ServerLevel level, BlockPos position, RandomSource random) {
        // This is an adapted version of FireBlock's scheduledTick method
        // Removing the infinyburn check as well as the placement of new fire
		level.scheduleTick(position, this, FireBlockAccessor.invokeGetFireTickDelay(level.random));

        if (!state.canSurvive(level, position)) {
            level.removeBlock(position, false);
        }

        if (!level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            return;
        }

        int age = state.getValue(AGE);

        if (level.isRaining() && ((FireBlockAccessor)Blocks.FIRE).invokeIsNearRain(level, position) && random.nextFloat() < 0.2f + (float)age * 0.03f) {
            level.removeBlock(position, false);
            return;
        }

        int newAge = Math.min(15, age + random.nextInt(3) / 2);

        if (age != newAge) {
            state = (BlockState)state.setValue(AGE, newAge);
            level.setBlock(position, state, Block.UPDATE_INVISIBLE);
        }

        if (!((FireBlockAccessor)Blocks.FIRE).invokeIsValidFireLocation(level, position)) {
            BlockPos blockPos = position.below();
            if (!level.getBlockState(blockPos).isFaceSturdy(level, blockPos, Direction.UP) || age > 3) {
                level.removeBlock(position, false);
            }
            return;
        }

        if (age == 15 && random.nextInt(4) == 0 && !this.canBurn(level.getBlockState(position.below()))) {
            level.removeBlock(position, false);
        }
    }

    private BlockState ensureCosmeticFire(BlockState state) {
        return state.isAir() ? state : copyBlockStateAttributes(state, defaultBlockState());
    }

    public static BlockState copyBlockStateAttributes(BlockState source, BlockState target) {
        for (BooleanProperty direction : DIRECTION_PROPERTIES.values()) {
            target = target.setValue(direction, source.getValue(direction));
        }

        return target.setValue(AGE, source.getValue(AGE));
    }
}
