package io.github.mclovelock.lovelock.utils.maths.voronoi;

import java.util.ArrayList;
import java.util.List;

public class VoronoiContext {

    private final List<VoronoiSite> vertices = new ArrayList<>();
    private final List<VoronoiCell> cells = new ArrayList<>();

    private final DelaunayContext dual;

    VoronoiContext(DelaunayContext dual) {
        this.dual = dual;
        buildVoronoiDualGraphToDelaunayTriangulation();
    }

    private int triangleWithSite(int site) {
        for (int i = 0; i < dual.getTriangulation().size(); i++) {
            if (dual.getTriangulation().get(i).hasSite(site)) return i;
        }
        return -1;
    }

    private void addVertices() {
        for (Triangle tri : dual.getTriangulation()) {
            Circumcircle c = dual.circumcircle(tri);
            vertices.add(new SiteImpl(c.cx(), c.cy()));
        }
    }

    private void buildVoronoiDualGraphToDelaunayTriangulation() {
        addVertices();

        for (int i = 0; i < dual.getSites().size(); i++) {
            int startTriIdx = triangleWithSite(i);
            if (startTriIdx == -1) continue;

            VoronoiCell cell = new VoronoiCell(i, dual.getSites().get(i));

            Triangle startTri = dual.getTriangulation().get(startTriIdx);
            Triangle next = startTri;
            while ((next = next.neighbourWithSite(i, next)) != startTri && next != null) {
                cell.edgeTo(dual.getTriangulation().indexOf(next));
            }

            cells.add(cell);
        }
    }

    public DelaunayContext getDual() {
        return dual;
    }

    public List<VoronoiSite> getVertices() {
        return vertices;
    }

    public List<VoronoiCell> getCells() {
        return cells;
    }

    public List<VoronoiSite> getSites() {
        return dual.getSites();
    }

}
