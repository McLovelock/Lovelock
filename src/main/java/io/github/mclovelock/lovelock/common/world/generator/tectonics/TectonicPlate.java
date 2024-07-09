package io.github.mclovelock.lovelock.common.world.generator.tectonics;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.random.Random;
import org.joml.Vector2d;

public class TectonicPlate {

    // in 10^6 years
    public static final int OLDEST_PLATE_AGE = 2500;
    public static final int YOUNGEST_PLATE_AGE = 150;

    public static final int CONTINENTAL_PLATE_AGE_THRESHOLD = 1000;

    public static final TectonicPlate BORDER = new TectonicPlate(-1, TectonicPlateType.FAKE_PLATE_BORDER_TYPE);

    private final Random plateGenerationRandom;

    private final int age;
    private final TectonicPlateType plateType;
    private final Vector2d direction;

    public TectonicPlate(Random plateGenerationRandom) {
        this.plateGenerationRandom = plateGenerationRandom;

        this.age = generateRandomAge();
        this.plateType = pickRandomPlateType();
        this.direction = generateRandomMovementDirection();
    }

    private TectonicPlate(int age, TectonicPlateType plateType) {
        this.plateGenerationRandom = null;
        this.direction = null;

        this.age = age;
        this.plateType = plateType;
    }

    private int generateRandomAge() {
        return plateGenerationRandom.nextBetween(YOUNGEST_PLATE_AGE, OLDEST_PLATE_AGE);
    }

    private TectonicPlateType pickRandomPlateType() {
        return (plateGenerationRandom.nextBoolean()) ? TectonicPlateType.CONTINENTAL_PLATE : TectonicPlateType.OCEANIC_PLATE;
    }

    private Vector2d generateRandomMovementDirection() {
        var v = new Vector2d(plateGenerationRandom.nextDouble(), plateGenerationRandom.nextDouble());
        return v.normalize();
    }

    public int getAge() {
        return age;
    }

    public TectonicPlateType getPlateType() {
        return plateType;
    }

    public Vector2d getDirection() {
        return direction;
    }

}
