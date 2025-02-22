package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TurtleEggBlock extends Block {
    public static final MapCodec<TurtleEggBlock> CODEC = simpleCodec(TurtleEggBlock::new);
    public static final int MAX_HATCH_LEVEL = 2;
    public static final int MIN_EGGS = 1;
    public static final int MAX_EGGS = 4;
    private static final VoxelShape ONE_EGG_AABB = Block.box(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
    private static final VoxelShape MULTIPLE_EGGS_AABB = Block.box(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);
    public static final IntegerProperty HATCH = BlockStateProperties.HATCH;
    public static final IntegerProperty EGGS = BlockStateProperties.EGGS;

    @Override
    public MapCodec<TurtleEggBlock> codec() {
        return CODEC;
    }

    public TurtleEggBlock(BlockBehaviour.Properties p_57759_) {
        super(p_57759_);
        this.registerDefaultState(this.stateDefinition.any().setValue(HATCH, Integer.valueOf(0)).setValue(EGGS, Integer.valueOf(1)));
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        if (!pEntity.isSteppingCarefully()) {
            this.destroyEgg(pLevel, pState, pPos, pEntity, 100);
        }

        super.stepOn(pLevel, pPos, pState, pEntity);
    }

    @Override
    public void fallOn(Level pLevel, BlockState pState, BlockPos pPos, Entity pEntity, float pFallDistance) {
        if (!(pEntity instanceof Zombie)) {
            this.destroyEgg(pLevel, pState, pPos, pEntity, 3);
        }

        super.fallOn(pLevel, pState, pPos, pEntity, pFallDistance);
    }

    private void destroyEgg(Level pLevel, BlockState pState, BlockPos pPos, Entity pEntity, int pChance) {
        if (this.canDestroyEgg(pLevel, pEntity)) {
            if (!pLevel.isClientSide && pLevel.random.nextInt(pChance) == 0 && pState.is(Blocks.TURTLE_EGG)) {
                this.decreaseEggs(pLevel, pPos, pState);
            }
        }
    }

    private void decreaseEggs(Level pLevel, BlockPos pPos, BlockState pState) {
        pLevel.playSound(null, pPos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + pLevel.random.nextFloat() * 0.2F);
        int i = pState.getValue(EGGS);
        if (i <= 1) {
            pLevel.destroyBlock(pPos, false);
        } else {
            pLevel.setBlock(pPos, pState.setValue(EGGS, Integer.valueOf(i - 1)), 2);
            pLevel.gameEvent(GameEvent.BLOCK_DESTROY, pPos, GameEvent.Context.of(pState));
            pLevel.levelEvent(2001, pPos, Block.getId(pState));
        }
    }

    @Override
    protected void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (this.shouldUpdateHatchLevel(pLevel) && onSand(pLevel, pPos)) {
            int i = pState.getValue(HATCH);
            if (i < 2) {
                pLevel.playSound(null, pPos, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7F, 0.9F + pRandom.nextFloat() * 0.2F);
                pLevel.setBlock(pPos, pState.setValue(HATCH, Integer.valueOf(i + 1)), 2);
                pLevel.gameEvent(GameEvent.BLOCK_CHANGE, pPos, GameEvent.Context.of(pState));
            } else {
                pLevel.playSound(null, pPos, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.7F, 0.9F + pRandom.nextFloat() * 0.2F);
                pLevel.removeBlock(pPos, false);
                pLevel.gameEvent(GameEvent.BLOCK_DESTROY, pPos, GameEvent.Context.of(pState));

                for (int j = 0; j < pState.getValue(EGGS); j++) {
                    pLevel.levelEvent(2001, pPos, Block.getId(pState));
                    Turtle turtle = EntityType.TURTLE.create(pLevel);
                    if (turtle != null) {
                        turtle.setAge(-24000);
                        turtle.setHomePos(pPos);
                        turtle.moveTo(
                            (double)pPos.getX() + 0.3 + (double)j * 0.2,
                            (double)pPos.getY(),
                            (double)pPos.getZ() + 0.3,
                            0.0F,
                            0.0F
                        );
                        pLevel.addFreshEntity(turtle);
                    }
                }
            }
        }
    }

    public static boolean onSand(BlockGetter pLevel, BlockPos pPos) {
        return isSand(pLevel, pPos.below());
    }

    public static boolean isSand(BlockGetter pReader, BlockPos pPos) {
        return pReader.getBlockState(pPos).is(BlockTags.SAND);
    }

    @Override
    protected void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (onSand(pLevel, pPos) && !pLevel.isClientSide) {
            pLevel.levelEvent(2012, pPos, 15);
        }
    }

    private boolean shouldUpdateHatchLevel(Level pLevel) {
        float f = pLevel.getTimeOfDay(1.0F);
        return (double)f < 0.69 && (double)f > 0.65 ? true : pLevel.random.nextInt(500) == 0;
    }

    @Override
    public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pTe, ItemStack pStack) {
        super.playerDestroy(pLevel, pPlayer, pPos, pState, pTe, pStack);
        this.decreaseEggs(pLevel, pPos, pState);
    }

    @Override
    protected boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
        return !pUseContext.isSecondaryUseActive() && pUseContext.getItemInHand().is(this.asItem()) && pState.getValue(EGGS) < 4
            ? true
            : super.canBeReplaced(pState, pUseContext);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState blockstate = pContext.getLevel().getBlockState(pContext.getClickedPos());
        return blockstate.is(this)
            ? blockstate.setValue(EGGS, Integer.valueOf(Math.min(4, blockstate.getValue(EGGS) + 1)))
            : super.getStateForPlacement(pContext);
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(EGGS) > 1 ? MULTIPLE_EGGS_AABB : ONE_EGG_AABB;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HATCH, EGGS);
    }

    private boolean canDestroyEgg(Level pLevel, Entity pEntity) {
        if (pEntity instanceof Turtle || pEntity instanceof Bat) {
            return false;
        } else {
            return !(pEntity instanceof LivingEntity) ? false : pEntity instanceof Player || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(pLevel, pEntity);
        }
    }
}
