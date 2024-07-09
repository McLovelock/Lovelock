package io.github.mclovelock.lovelock.common.world.generator.tectonics;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.RandomSeed;

public class TectonicsGenerator {

    private final long tectonicsSeed;

    public TectonicsGenerator() {
        tectonicsSeed = RandomSeed.getSeed();
    }

    public TectonicChunk getTectonicChunkAt(ChunkPos chunk) {
        return new TectonicChunk(Math.floorDiv(chunk.x, TectonicChunk.TECTONIC_CHUNK_SIZE),
                Math.floorDiv(chunk.z, TectonicChunk.TECTONIC_CHUNK_SIZE), tectonicsSeed);
    }

    public TectonicPlate getTectonicPlateAt(ChunkPos chunk) {
        TectonicChunk localTectonicChunk = getTectonicChunkAt(chunk);
        TectonicChunk closest = localTectonicChunk;

        int distanceFromTectonicChunkVoronoiCenter = closest.noisyDistanceToVoronoiVertex(chunk.x, chunk.z);
        int bestDistance = distanceFromTectonicChunkVoronoiCenter;

        TectonicChunk[] neighbours = { closest.north(), closest.east(), closest.west(), closest.south(),
            closest.northEast(), closest.northWest(), closest.southEast(), closest.southWest() };
        for (TectonicChunk t : neighbours) {
            int d = t.distanceToVoronoiVertex(chunk.x, chunk.z);
            if (d < bestDistance) {
                bestDistance = d;
                closest = t;
            }
        }

        int delta = Math.abs(distanceFromTectonicChunkVoronoiCenter - bestDistance);

        return closest.getAssociatedPlate();
    }

}
