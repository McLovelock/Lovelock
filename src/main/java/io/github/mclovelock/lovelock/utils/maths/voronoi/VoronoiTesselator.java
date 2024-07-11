package io.github.mclovelock.lovelock.utils.maths.voronoi;

import java.util.ArrayList;
import java.util.List;

public final class VoronoiTesselator {

    public static DelaunayContext delaunayBowyerWatson(VoronoiSite... sites) {
        var delaunayContext = new DelaunayContext(new ArrayList<VoronoiSite>(List.of(sites)));
        delaunayContext.computeDelaunayTriangulation();
        return delaunayContext;
    }

    public static VoronoiContext buildVoronoiGraph(VoronoiSite... sites) {
        DelaunayContext delaunayTriangulation = delaunayBowyerWatson(sites);
        return new VoronoiContext(delaunayTriangulation);
    }

}
