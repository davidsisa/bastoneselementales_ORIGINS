package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.AngleArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class SetWorldSpawnCommand {
    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
        pDispatcher.register(
            Commands.literal("setworldspawn")
                .requires(p_138665_ -> p_138665_.hasPermission(2))
                .executes(p_274830_ -> setSpawn(p_274830_.getSource(), BlockPos.containing(p_274830_.getSource().getPosition()), 0.0F))
                .then(
                    Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(p_138671_ -> setSpawn(p_138671_.getSource(), BlockPosArgument.getSpawnablePos(p_138671_, "pos"), 0.0F))
                        .then(
                            Commands.argument("angle", AngleArgument.angle())
                                .executes(
                                    p_138663_ -> setSpawn(
                                            p_138663_.getSource(), BlockPosArgument.getSpawnablePos(p_138663_, "pos"), AngleArgument.getAngle(p_138663_, "angle")
                                        )
                                )
                        )
                )
        );
    }

    private static int setSpawn(CommandSourceStack pSource, BlockPos pPos, float pAngle) {
        ServerLevel serverlevel = pSource.getLevel();
        if (serverlevel.dimension() != Level.OVERWORLD) {
            pSource.sendFailure(Component.translatable("commands.setworldspawn.failure.not_overworld"));
            return 0;
        } else {
            serverlevel.setDefaultSpawnPos(pPos, pAngle);
            pSource.sendSuccess(
                () -> Component.translatable("commands.setworldspawn.success", pPos.getX(), pPos.getY(), pPos.getZ(), pAngle),
                true
            );
            return 1;
        }
    }
}