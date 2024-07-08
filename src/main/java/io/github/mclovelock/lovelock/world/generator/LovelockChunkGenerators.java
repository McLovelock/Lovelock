package io.github.mclovelock.lovelock.world.generator;

import io.github.mclovelock.lovelock.Lovelock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class LovelockChunkGenerators {

    public static void registerChunkGenerators() {
        Lovelock.LOGGER.info("Registering chunk generators.");

        Registry.register(Registries.CHUNK_GENERATOR, Identifier.of("lovelock", "lovelock"), LovelockChunkGenerator.CODEC);
    }

}
