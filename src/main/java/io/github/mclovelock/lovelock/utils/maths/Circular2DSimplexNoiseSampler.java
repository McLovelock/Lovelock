package io.github.mclovelock.lovelock.utils.maths;

import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.Random;

public class Circular2DSimplexNoiseSampler {

    private final double radius;
    private final SimplexNoiseSampler innerSimplexSampler;

    public Circular2DSimplexNoiseSampler(double radius, long seed) {
        this.radius = radius;
        this.innerSimplexSampler = new SimplexNoiseSampler(Random.create(seed));
    }

    public double sampleAtAngle(double rads) {
        double x = Math.cos(rads) * radius;
        double z = Math.sin(rads) * radius;
        return innerSimplexSampler.sample(x, z);
    }

}
