package io.github.mclovelock.lovelock;

import io.github.mclovelock.lovelock.world.generator.LovelockChunkGenerators;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lovelock implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("lovelock");

	@Override
	public void onInitialize() {
		LovelockChunkGenerators.registerChunkGenerators();
	}
}