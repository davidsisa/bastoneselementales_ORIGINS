package net.minecraft.world.level.levelgen.feature.featuresize;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class FeatureSizeType<P extends FeatureSize> {
    public static final FeatureSizeType<TwoLayersFeatureSize> TWO_LAYERS_FEATURE_SIZE = register("two_layers_feature_size", TwoLayersFeatureSize.CODEC);
    public static final FeatureSizeType<ThreeLayersFeatureSize> THREE_LAYERS_FEATURE_SIZE = register("three_layers_feature_size", ThreeLayersFeatureSize.CODEC);
    private final MapCodec<P> codec;

    private static <P extends FeatureSize> FeatureSizeType<P> register(String pName, MapCodec<P> pCodec) {
        return Registry.register(BuiltInRegistries.FEATURE_SIZE_TYPE, pName, new FeatureSizeType<>(pCodec));
    }

    public FeatureSizeType(MapCodec<P> pCodec) {
        this.codec = pCodec;
    }

    public MapCodec<P> codec() {
        return this.codec;
    }
}