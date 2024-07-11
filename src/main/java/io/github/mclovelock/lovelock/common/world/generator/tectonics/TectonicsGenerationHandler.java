package io.github.mclovelock.lovelock.common.world.generator.tectonics;

import net.minecraft.util.math.random.RandomSeed;

import java.util.List;

public class TectonicsGenerationHandler {

    private final long tectonicSeed;

    public TectonicsGenerationHandler() {
        tectonicSeed = RandomSeed.getSeed();
    }

    private TectonicChunk getTectonicChunk(int x, int z) {
        return TectonicChunk.fromBlockXZ(x, z, tectonicSeed);
    }

    public TectonicPlate getTectonicPlateAt(int x, int z) {
        var tc = getTectonicChunk(x, z);

        tc.getVoronoiGraph();
        List<TectonicChunk> withNeighbouringChunks = List.of(tc,
                tc.north(), tc.northWest(), tc.west(), tc.southWest(), tc.south(), tc.southEast(), tc.east(), tc.northEast());


        return null;
    }

}
