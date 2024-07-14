package io.github.mclovelock.lovelock.common.world.generator.tectonics;

import io.github.mclovelock.lovelock.utils.maths.Maths;
import io.github.mclovelock.lovelock.utils.maths.geometry.Geometry;
import io.github.mclovelock.lovelock.utils.maths.geometry.LineEquation;
import io.github.mclovelock.lovelock.utils.maths.voronoi.Edge;
import io.github.mclovelock.lovelock.utils.maths.voronoi.VoronoiCell;
import io.github.mclovelock.lovelock.utils.maths.voronoi.VoronoiContext;
import io.github.mclovelock.lovelock.utils.maths.voronoi.VoronoiSite;
import net.minecraft.util.math.random.RandomSeed;
import org.joml.Vector2d;

import java.util.List;

public class TectonicsGenerationHandler {

    private final long tectonicSeed;

    public TectonicsGenerationHandler() {
        tectonicSeed = RandomSeed.getSeed();
    }

    public TectonicChunk getTectonicChunk(int x, int z) {
        return TectonicChunk.fromBlockXZ(x, z, tectonicSeed);
    }

    public TectonicChunk getTectonicPlateAt(int x, int z) {
        TectonicChunk tc = getTectonicChunk(x, z);
        VoronoiContext voronoiGraph = tc.getVoronoiGraph();

        double bestDist = Double.POSITIVE_INFINITY;
        TectonicChunk site = null;
        for (VoronoiCell cell : voronoiGraph.getCells()) {
            TectonicChunk s = (TectonicChunk)cell.getSite();
            double dist = Maths.distance(s.siteX(), s.siteY(), x, z);
            if ((dist < bestDist) || (site == null)) {
                site = s;
                bestDist = dist;
            }
        }

        return site;
    }

}
