package io.github.mclovelock.lovelock.common.world.generator.tectonics;

import io.github.mclovelock.lovelock.Lovelock;

public class TectonicChunk {

    public static final int TECTONIC_CHUNK_SIZE = 512;

    private final int gridX, gridZ;
    private final TectonicsGenerationHandler tectonicsHandler;

    private final int seedX, seedZ;
    private final TectonicPlate associatedPlate;

    private TectonicChunk north = null;
    private TectonicChunk south = null;
    private TectonicChunk east = null;
    private TectonicChunk west = null;

    public TectonicChunk(int gridX, int gridZ, TectonicsGenerationHandler tectonicsHandler) {
        this.gridX = gridX;
        this.gridZ = gridZ;
        this.tectonicsHandler = tectonicsHandler;

        double jitterX = this.tectonicsHandler.getSeedXJitterSampler().sample(this.gridX, this.gridZ) / 2.0 + 0.5;
        double jitterZ = this.tectonicsHandler.getSeedZJitterSampler().sample(this.gridX, this.gridZ) / 2.0 + 0.5;

        this.seedX = (int)Math.floor(((double)gridX + jitterX) * (double)TECTONIC_CHUNK_SIZE);
        this.seedZ = (int)Math.floor(((double)gridZ + jitterZ) * (double)TECTONIC_CHUNK_SIZE);

        this.associatedPlate = new TectonicPlate((gridX % 2 == 0) ^ (gridZ % 2 == 0));
    }

    public TectonicChunk north() {
        if (north == null)
            north = tectonicsHandler.getTectonicChunk(gridX, gridZ + 1);
        return north;
    }

    public TectonicChunk south() {
        if (south == null)
            south = tectonicsHandler.getTectonicChunk(gridX, gridZ - 1);
        return south;
    }

    public TectonicChunk east() {
        if (east == null)
            east = tectonicsHandler.getTectonicChunk(gridX - 1, gridZ);
        return east;
    }

    public TectonicChunk west() {
        if (west == null)
            west = tectonicsHandler.getTectonicChunk(gridX + 1, gridZ);
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

    public TectonicPlate getAssociatedPlate() {
        return associatedPlate;
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridZ() {
        return gridZ;
    }

    public int getSeedX() {
        return seedX;
    }

    public int getSeedZ() {
        return seedZ;
    }

}
