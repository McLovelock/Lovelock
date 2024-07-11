package io.github.mclovelock.lovelock.common.world.generator.tectonics;

import io.github.mclovelock.lovelock.utils.maths.Numerics;
import io.github.mclovelock.lovelock.utils.maths.voronoi.DelaunayContext;
import io.github.mclovelock.lovelock.utils.maths.voronoi.VoronoiContext;
import io.github.mclovelock.lovelock.utils.maths.voronoi.VoronoiSite;
import io.github.mclovelock.lovelock.utils.maths.voronoi.VoronoiTesselator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.Map;

public class TectonicChunk implements VoronoiSite {

    static final int TECTONIC_CHUNK_SIZE = 32;

    static TectonicChunk fromBlockXZ(int blockX, int blockZ, long baseSeed) {
        int gridX = Math.floorDiv(blockX, TECTONIC_CHUNK_SIZE);
        int gridZ = Math.floorDiv(blockZ, TECTONIC_CHUNK_SIZE);
        return getTectonicChunk(gridX, gridZ, baseSeed);
    }

    static TectonicChunk fromBlockPos(BlockPos pos, long baseSeed) {
        return fromBlockXZ(pos.getX(), pos.getZ(), baseSeed);
    }

    private static final Map<Long, TectonicChunk> tectonicChunkCache = new HashMap<>();

    static TectonicChunk getTectonicChunk(int gridX, int gridZ, long baseSeed) {
        long hash = Numerics.signedCantorPair(gridX, gridZ);
        TectonicChunk tc = tectonicChunkCache.get(hash);
        if (tc == null) {
            tc = new TectonicChunk(gridX, gridZ, hash, baseSeed);
            tectonicChunkCache.put(hash, tc);
        }
        return tc;
    }

    private final int gridX, gridZ;
    private final long baseSeed;

    private final TectonicPlate associatedPlate;

    private final int voronoiVertexX, voronoiVertexZ;
    private VoronoiContext voronoiGraph;

    private TectonicChunk north = null;
    private TectonicChunk south = null;
    private TectonicChunk east = null;
    private TectonicChunk west = null;

    private TectonicChunk(int gridX, int gridZ, long hash, long baseSeed) {
        this.gridX = gridX;
        this.gridZ = gridZ;
        this.baseSeed = baseSeed;

        long specificSeed = baseSeed + hash;

        var random = Random.create(specificSeed);
        voronoiVertexX = gridX * TECTONIC_CHUNK_SIZE + random.nextBetween(0, TECTONIC_CHUNK_SIZE - 1);
        voronoiVertexZ = gridZ * TECTONIC_CHUNK_SIZE + random.nextBetween(0, TECTONIC_CHUNK_SIZE - 1);

        this.associatedPlate = new TectonicPlate((gridX % 2 == 0) ^ (gridZ % 2 == 0));
    }

    TectonicChunk north() {
        if (north == null)
            north = getTectonicChunk(gridX, gridZ + 1, baseSeed);
        return north;
    }

    TectonicChunk south() {
        if (south == null)
            south = getTectonicChunk(gridX, gridZ - 1, baseSeed);
        return south;
    }

    TectonicChunk east() {
        if (east == null)
            east = getTectonicChunk(gridX - 1, gridZ, baseSeed);
        return east;
    }

    TectonicChunk west() {
        if (west == null)
            west = getTectonicChunk(gridX + 1, gridZ, baseSeed);
        return west;
    }

    TectonicChunk northEast() {
        return north().east();
    }

    TectonicChunk northWest() {
        return north().west();
    }

    TectonicChunk southEast() {
        return south().east();
    }

    TectonicChunk southWest() {
        return south().west();
    }

    @Override
    public double siteX() {
        return voronoiVertexX;
    }

    @Override
    public double siteY() {
        return voronoiVertexZ;
    }

    VoronoiContext getVoronoiGraph() {
        // only compute on request to avoid stack overflow by generating all TectonicChunks when loading the world.
        if (voronoiGraph == null) {
            voronoiGraph = VoronoiTesselator.buildVoronoiGraph(this,
                    north(), west(), south(), east(), northWest(), northEast(), southWest(), southEast());
        }
        return voronoiGraph;
    }

    public TectonicPlate getAssociatedPlate() {
        return associatedPlate;
    }

    int getGridX() {
        return gridX;
    }

    int getGridZ() {
        return gridZ;
    }

}
