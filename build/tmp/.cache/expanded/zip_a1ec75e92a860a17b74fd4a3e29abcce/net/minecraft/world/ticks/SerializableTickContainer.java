package net.minecraft.world.ticks;

import java.util.function.Function;
import net.minecraft.nbt.Tag;

public interface SerializableTickContainer<T> {
    Tag save(long pGameTime, Function<T, String> pIdGetter);
}