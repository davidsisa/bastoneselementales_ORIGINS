package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class WorldCoordinates implements Coordinates {
    private final WorldCoordinate x;
    private final WorldCoordinate y;
    private final WorldCoordinate z;

    public WorldCoordinates(WorldCoordinate pX, WorldCoordinate pY, WorldCoordinate pZ) {
        this.x = pX;
        this.y = pY;
        this.z = pZ;
    }

    @Override
    public Vec3 getPosition(CommandSourceStack pSource) {
        Vec3 vec3 = pSource.getPosition();
        return new Vec3(this.x.get(vec3.x), this.y.get(vec3.y), this.z.get(vec3.z));
    }

    @Override
    public Vec2 getRotation(CommandSourceStack pSource) {
        Vec2 vec2 = pSource.getRotation();
        return new Vec2((float)this.x.get((double)vec2.x), (float)this.y.get((double)vec2.y));
    }

    @Override
    public boolean isXRelative() {
        return this.x.isRelative();
    }

    @Override
    public boolean isYRelative() {
        return this.y.isRelative();
    }

    @Override
    public boolean isZRelative() {
        return this.z.isRelative();
    }

    @Override
    public boolean equals(Object pOther) {
        if (this == pOther) {
            return true;
        } else if (!(pOther instanceof WorldCoordinates worldcoordinates)) {
            return false;
        } else if (!this.x.equals(worldcoordinates.x)) {
            return false;
        } else {
            return !this.y.equals(worldcoordinates.y) ? false : this.z.equals(worldcoordinates.z);
        }
    }

    public static WorldCoordinates parseInt(StringReader pReader) throws CommandSyntaxException {
        int i = pReader.getCursor();
        WorldCoordinate worldcoordinate = WorldCoordinate.parseInt(pReader);
        if (pReader.canRead() && pReader.peek() == ' ') {
            pReader.skip();
            WorldCoordinate worldcoordinate1 = WorldCoordinate.parseInt(pReader);
            if (pReader.canRead() && pReader.peek() == ' ') {
                pReader.skip();
                WorldCoordinate worldcoordinate2 = WorldCoordinate.parseInt(pReader);
                return new WorldCoordinates(worldcoordinate, worldcoordinate1, worldcoordinate2);
            } else {
                pReader.setCursor(i);
                throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(pReader);
            }
        } else {
            pReader.setCursor(i);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(pReader);
        }
    }

    public static WorldCoordinates parseDouble(StringReader pReader, boolean pCenterCorrect) throws CommandSyntaxException {
        int i = pReader.getCursor();
        WorldCoordinate worldcoordinate = WorldCoordinate.parseDouble(pReader, pCenterCorrect);
        if (pReader.canRead() && pReader.peek() == ' ') {
            pReader.skip();
            WorldCoordinate worldcoordinate1 = WorldCoordinate.parseDouble(pReader, false);
            if (pReader.canRead() && pReader.peek() == ' ') {
                pReader.skip();
                WorldCoordinate worldcoordinate2 = WorldCoordinate.parseDouble(pReader, pCenterCorrect);
                return new WorldCoordinates(worldcoordinate, worldcoordinate1, worldcoordinate2);
            } else {
                pReader.setCursor(i);
                throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(pReader);
            }
        } else {
            pReader.setCursor(i);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(pReader);
        }
    }

    public static WorldCoordinates absolute(double pX, double pY, double pZ) {
        return new WorldCoordinates(new WorldCoordinate(false, pX), new WorldCoordinate(false, pY), new WorldCoordinate(false, pZ));
    }

    public static WorldCoordinates absolute(Vec2 pVector) {
        return new WorldCoordinates(
            new WorldCoordinate(false, (double)pVector.x), new WorldCoordinate(false, (double)pVector.y), new WorldCoordinate(true, 0.0)
        );
    }

    public static WorldCoordinates current() {
        return new WorldCoordinates(new WorldCoordinate(true, 0.0), new WorldCoordinate(true, 0.0), new WorldCoordinate(true, 0.0));
    }

    @Override
    public int hashCode() {
        int i = this.x.hashCode();
        i = 31 * i + this.y.hashCode();
        return 31 * i + this.z.hashCode();
    }
}