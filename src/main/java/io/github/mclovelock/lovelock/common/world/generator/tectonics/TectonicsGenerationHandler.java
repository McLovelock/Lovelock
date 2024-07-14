package io.github.mclovelock.lovelock.common.world.generator.tectonics;

import io.github.mclovelock.lovelock.utils.maths.Maths;
import io.github.mclovelock.lovelock.utils.maths.Numerics;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TectonicsGenerationHandler {

    private final Map<Long, TectonicChunk> tectonicChunkCache = new HashMap<>();

    private final SimplexNoiseSampler seedXJitterSampler;
    private final SimplexNoiseSampler seedZJitterSampler;

    private final SimplexNoiseSampler xOffsetSampler;
    private final SimplexNoiseSampler zOffsetSampler;

    public TectonicsGenerationHandler(long seed) {
        this.seedXJitterSampler = new SimplexNoiseSampler(Random.create(seed));
        this.seedZJitterSampler = new SimplexNoiseSampler(Random.create(-seed));

        this.xOffsetSampler = new SimplexNoiseSampler(Random.create(25 * seed));
        this.zOffsetSampler = new SimplexNoiseSampler(Random.create(-52 * seed));
    }

    public TectonicChunk getTectonicChunkAt(int x, int z) {
        int gridX = Math.floorDiv(x, TectonicChunk.TECTONIC_CHUNK_SIZE);
        int gridZ = Math.floorDiv(z, TectonicChunk.TECTONIC_CHUNK_SIZE);
        return getTectonicChunk(gridX, gridZ);
    }

    public TectonicChunk getTectonicChunk(int gridX, int gridZ) {
        long hash = Numerics.signedCantorPair(gridX, gridZ);
        TectonicChunk tc = tectonicChunkCache.get(hash);
        if (tc == null) {
            tc = new TectonicChunk(gridX, gridZ, this);
            tectonicChunkCache.put(hash, tc);
        }
        return tc;
    }

    private List<TectonicChunk> getProximityChunks(TectonicChunk centre) {
        return List.of(
            centre.northEast().northEast(), centre.north().northEast(), centre.north().north(), centre.north().northWest(), centre.northWest().northWest(),
            centre.east().northEast(), centre.northEast(), centre.north(), centre.northWest(), centre.west().northWest(),
            centre.east().east(), centre.east(), centre, centre.west(), centre.west().west(),
            centre.east().southEast(), centre.southEast(), centre.south(), centre.southWest(), centre.west().southWest(),
            centre.southEast().southEast(), centre.south().southEast(), centre.south().south(), centre.south().southWest(), centre.southWest().southWest()
        );
    }

    public TectonicChunk getTectonicPlateAt(int x, int z) {
        int distortedX = x + (int)(xOffsetSampler.sample(x * 0.1 + 0.5, z * 0.1 + 0.5) * 0.05 * TectonicChunk.TECTONIC_CHUNK_SIZE);
        int distortedZ = z + (int)(zOffsetSampler.sample(x * 0.1 + 0.5, z * 0.1 + 0.5) * 0.05 * TectonicChunk.TECTONIC_CHUNK_SIZE);

        TectonicChunk localTectonicChunk = getTectonicChunkAt(distortedX, distortedZ);
        List<TectonicChunk> proximityChunk = getProximityChunks(localTectonicChunk);

        TectonicChunk closest = null;
        double closestDistance = Double.POSITIVE_INFINITY;
        for (TectonicChunk chunk : proximityChunk) {
            double distance = Maths.distance(chunk.getSeedX(), chunk.getSeedZ(), distortedX, distortedZ);
            if ((distance < closestDistance) || (closest == null)) {
                closestDistance = distance;
                closest = chunk;
            }
        }

        return closest;
    }

    public SimplexNoiseSampler getSeedXJitterSampler() {
        return seedXJitterSampler;
    }

    public SimplexNoiseSampler getSeedZJitterSampler() {
        return seedZJitterSampler;
    }

}
