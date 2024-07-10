package io.github.mclovelock.lovelock.common.world.generator.tectonics;

import io.github.mclovelock.lovelock.utils.maths.voronoi.VoronoiResult;
import io.github.mclovelock.lovelock.utils.maths.voronoi.VoronoiTesselator;
import net.minecraft.util.math.random.RandomSeed;

import java.util.List;

public class TectonicsGenerationHandler {

    private final long tectonicSeed;

    public TectonicsGenerationHandler() {
        tectonicSeed = RandomSeed.getSeed();
    }

    public void getTectonicPlateAt(int x, int z) {
        var tc = TectonicChunk.fromBlockXZ(x, z, tectonicSeed);

    }

}
