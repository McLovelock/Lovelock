package io.github.mclovelock.lovelock.common.world.generator.tectonics;

import net.minecraft.util.math.random.Random;

public class TectonicPlate {

    private final boolean isOceanic;

    TectonicPlate(long specificSeed) {
        this.isOceanic = Random.create(specificSeed).nextBoolean();
    }

    TectonicPlate(boolean isOceanic) {
        this.isOceanic = isOceanic;
    }

    public boolean isOceanic() {
        return isOceanic;
    }

}
