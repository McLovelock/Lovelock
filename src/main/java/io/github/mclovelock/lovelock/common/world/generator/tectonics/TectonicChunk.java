package io.github.mclovelock.lovelock.common.world.generator.tectonics;

import io.github.mclovelock.lovelock.utils.maths.Circular2DSimplexNoiseSampler;
import io.github.mclovelock.lovelock.utils.maths.Maths;
import io.github.mclovelock.lovelock.utils.maths.Numerics;
import net.minecraft.util.math.random.Random;

public class TectonicChunk {

    public static final int TECTONIC_CHUNK_SIZE = 64;

    private final int gridX, gridZ;
    private final long tectonicsSeed;

    private final int voronoiVertexX, voronoiVertexZ;
    private final Circular2DSimplexNoiseSampler voronoiNoiseSampler;
    private final TectonicPlate associatedPlate;

    private TectonicChunk north, south, east, west;

    public TectonicChunk(int gridX, int gridZ, long tectonicsSeed) {
        this.gridX = gridX;
        this.gridZ = gridZ;
        this.tectonicsSeed = tectonicsSeed;

        long specificSeed = tectonicsSeed + Numerics.signedCantorPair(gridX, gridZ);

        var random = Random.create(specificSeed);
        voronoiVertexX = random.nextBetween(getMinChunkX(), getMaxChunkX());
        voronoiVertexZ = random.nextBetween(getMinChunkZ(), getMaxChunkZ());

        voronoiNoiseSampler = new Circular2DSimplexNoiseSampler(TECTONIC_CHUNK_SIZE, specificSeed);

        associatedPlate = new TectonicPlate(Random.create(specificSeed));

        north = south = east = west = null;
    }

    public TectonicChunk north() {
        if (north == null)
            north = new TectonicChunk(gridX, gridZ + 1, tectonicsSeed);
        return north;
    }

    public TectonicChunk south() {
        if (south == null)
            south = new TectonicChunk(gridX, gridZ - 1, tectonicsSeed);
        return south;
    }

    public TectonicChunk east() {
        if (east == null)
            east = new TectonicChunk(gridX + 1, gridZ, tectonicsSeed);
        return east;
    }

    public TectonicChunk west() {
        if (west == null)
            west = new TectonicChunk(gridX - 1, gridZ, tectonicsSeed);
        return west;
    }

    public TectonicChunk northEast() {
        return north().east();
    }

    public TectonicChunk northWest() {
        return north().west();
    }

    public TectonicChunk southEast() {
        return south().east();
    }

    public TectonicChunk southWest() {
        return south().west();
    }

    public int getMinChunkX() {
        return gridX * TECTONIC_CHUNK_SIZE;
    }

    public int getMinChunkZ() {
        return gridZ * TECTONIC_CHUNK_SIZE;
    }

    public int getMaxChunkX() {
        return (gridX + 1) * TECTONIC_CHUNK_SIZE - 1;
    }

    public int getMaxChunkZ() {
        return (gridZ + 1) * TECTONIC_CHUNK_SIZE - 1;
    }

    public int getVoronoiVertexX() {
        return voronoiVertexX;
    }

    public int getVoronoiVertexZ() {
        return voronoiVertexZ;
    }

    public Circular2DSimplexNoiseSampler getVoronoiNoiseSampler() {
        return voronoiNoiseSampler;
    }

    public int distanceToVoronoiVertex(int x, int z) {
        int dx = x - getVoronoiVertexX();
        int dz = z - getVoronoiVertexZ();
        return (int)Math.round(Math.sqrt(dx * dx + dz * dz));
    }

    public int noisyDistanceToVoronoiVertex(int x, int z) {
        double dx = x - getVoronoiVertexX();
        double dz = z - getVoronoiVertexZ();

        double len = Math.sqrt(dx * dx + dz * dz);

        double angle = Maths.angleOnUnitCircle(dx / len, dz / len);

        return (int)Math.round(len + voronoiNoiseSampler.sampleAtAngle(angle) * 2);
    }

    public TectonicPlate getAssociatedPlate() {
        return associatedPlate;
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridZ() {
        return gridZ;
    }

}
