package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class GroundPathNavigation extends PathNavigation {
    private boolean avoidSun;

    public GroundPathNavigation(Mob pMob, Level pLevel) {
        super(pMob, pLevel);
    }

    @Override
    protected PathFinder createPathFinder(int pMaxVisitedNodes) {
        this.nodeEvaluator = new WalkNodeEvaluator();
        this.nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(this.nodeEvaluator, pMaxVisitedNodes);
    }

    @Override
    protected boolean canUpdatePath() {
        return this.mob.onGround() || this.mob.isInLiquid() || this.mob.isPassenger();
    }

    @Override
    protected Vec3 getTempMobPos() {
        return new Vec3(this.mob.getX(), (double)this.getSurfaceY(), this.mob.getZ());
    }

    @Override
    public Path createPath(BlockPos pPos, int pAccuracy) {
        LevelChunk levelchunk = this.level.getChunkSource().getChunkNow(SectionPos.blockToSectionCoord(pPos.getX()), SectionPos.blockToSectionCoord(pPos.getZ()));
        if (levelchunk == null) {
            return null;
        } else {
            if (levelchunk.getBlockState(pPos).isAir()) {
                BlockPos blockpos = pPos.below();

                while (blockpos.getY() > this.level.getMinBuildHeight() && levelchunk.getBlockState(blockpos).isAir()) {
                    blockpos = blockpos.below();
                }

                if (blockpos.getY() > this.level.getMinBuildHeight()) {
                    return super.createPath(blockpos.above(), pAccuracy);
                }

                while (blockpos.getY() < this.level.getMaxBuildHeight() && levelchunk.getBlockState(blockpos).isAir()) {
                    blockpos = blockpos.above();
                }

                pPos = blockpos;
            }

            if (!levelchunk.getBlockState(pPos).isSolid()) {
                return super.createPath(pPos, pAccuracy);
            } else {
                BlockPos blockpos1 = pPos.above();

                while (blockpos1.getY() < this.level.getMaxBuildHeight() && levelchunk.getBlockState(blockpos1).isSolid()) {
                    blockpos1 = blockpos1.above();
                }

                return super.createPath(blockpos1, pAccuracy);
            }
        }
    }

    @Override
    public Path createPath(Entity pEntity, int pAccuracy) {
        return this.createPath(pEntity.blockPosition(), pAccuracy);
    }

    private int getSurfaceY() {
        if (this.mob.isInWater() && this.canFloat()) {
            int i = this.mob.getBlockY();
            BlockState blockstate = this.level.getBlockState(BlockPos.containing(this.mob.getX(), (double)i, this.mob.getZ()));
            int j = 0;

            while (blockstate.is(Blocks.WATER)) {
                blockstate = this.level.getBlockState(BlockPos.containing(this.mob.getX(), (double)(++i), this.mob.getZ()));
                if (++j > 16) {
                    return this.mob.getBlockY();
                }
            }

            return i;
        } else {
            return Mth.floor(this.mob.getY() + 0.5);
        }
    }

    @Override
    protected void trimPath() {
        super.trimPath();
        if (this.avoidSun) {
            if (this.level.canSeeSky(BlockPos.containing(this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ()))) {
                return;
            }

            for (int i = 0; i < this.path.getNodeCount(); i++) {
                Node node = this.path.getNode(i);
                if (this.level.canSeeSky(new BlockPos(node.x, node.y, node.z))) {
                    this.path.truncateNodes(i);
                    return;
                }
            }
        }
    }

    protected boolean hasValidPathType(PathType pPathType) {
        if (pPathType == PathType.WATER) {
            return false;
        } else {
            return pPathType == PathType.LAVA ? false : pPathType != PathType.OPEN;
        }
    }

    public void setCanOpenDoors(boolean pCanOpenDoors) {
        this.nodeEvaluator.setCanOpenDoors(pCanOpenDoors);
    }

    public boolean canPassDoors() {
        return this.nodeEvaluator.canPassDoors();
    }

    public void setCanPassDoors(boolean pCanPassDoors) {
        this.nodeEvaluator.setCanPassDoors(pCanPassDoors);
    }

    public boolean canOpenDoors() {
        return this.nodeEvaluator.canPassDoors();
    }

    public void setAvoidSun(boolean pAvoidSun) {
        this.avoidSun = pAvoidSun;
    }

    public void setCanWalkOverFences(boolean pCanWalkOverFences) {
        this.nodeEvaluator.setCanWalkOverFences(pCanWalkOverFences);
    }
}