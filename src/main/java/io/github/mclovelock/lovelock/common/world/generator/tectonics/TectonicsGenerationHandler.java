package io.github.mclovelock.lovelock.common.world.generator.tectonics;

import io.github.mclovelock.lovelock.utils.maths.Maths;
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

    private TectonicChunk getTectonicChunk(int x, int z) {
        return TectonicChunk.fromBlockXZ(x, z, tectonicSeed);
    }

    public TectonicChunk getTectonicPlateAt(int x, int z) {
        var tc = getTectonicChunk(x, z);

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

        /*cell_loop:
        for (VoronoiCell cell : voronoiGraph.getCells()) {
            site = (TectonicChunk)cell.getSite();

            for (Edge e : cell.getEdges()) {
                VoronoiSite a = voronoiGraph.getVertices().get(e.a());
                VoronoiSite b = voronoiGraph.getVertices().get(e.b());

                var ab = new Vector2d(b.siteX() - a.siteX(), b.siteY() - a.siteY());
                var aSite = new Vector2d(site.siteX() - a.siteX(), site.siteY() - a.siteY());
                var ap = new Vector2d((double)x - a.siteX(), (double)z - a.siteY());

                double angleEdge = aSite.angle(ab);
                double anglePoint = aSite.angle(ap);

                if (anglePoint > angleEdge)
                    continue cell_loop;
            }

            break;
        }*/

        return site;
    }

}
