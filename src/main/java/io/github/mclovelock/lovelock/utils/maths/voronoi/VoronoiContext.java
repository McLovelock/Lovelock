package io.github.mclovelock.lovelock.utils.maths.voronoi;

import java.util.Arrays;
import java.util.List;

public class VoronoiContext {

    private final VoronoiSite[] vertices;
    private final VoronoiCell[] cells;

    private final DelaunayContext dual;

    VoronoiContext(DelaunayContext dual) {
        this.dual = dual;

        this.vertices = new VoronoiSite[dual.getTriangulation().size()];
        this.cells = new VoronoiCell[dual.getSites().size()];

        buildVoronoiDualGraphToDelaunayTriangulation();
    }

    private void visitTriangle(int site, int current, int previous, int first, VoronoiCell cell, boolean[] visited) {
        Triangle tri = dual.getTriangulation().get(current);

        if (previous != -1)
            cell.addEdge(new Edge(previous, current));

        visited[current] = true;

        for (Triangle neighbour : tri.getNeighbours()) {
            if (neighbour == null) continue;
            if (!neighbour.hasSite(site)) continue;

            int neighbourIndex = dual.getTriangulation().indexOf(neighbour);
            if (((previous == first) || (neighbourIndex != first)) && visited[neighbourIndex]) continue;

            visitTriangle(site, neighbourIndex, current, first, cell, visited);
        }
    }

    private void addCellForSite(int site) {
        var cell = new VoronoiCell(getSites().get(site));
        for (int i = 0; i < dual.getTriangulation().size(); i++) {
            Triangle tri = dual.getTriangulation().get(i);
            if (tri.hasSite(site)) {
                boolean[] visited = new boolean[dual.getTriangulation().size()];
                visitTriangle(site, i, -1, i, cell, visited);
                break;
            }
        }
        cells[site] = cell;
    }

    private void buildVoronoiDualGraphToDelaunayTriangulation() {
        for (int i = 0; i < dual.getTriangulation().size(); i++) {
            Circumcircle c = dual.circumcircle(dual.getTriangulation().get(i));
            vertices[i] = new SiteImpl(c.cx(), c.cy());
        }

        for (int i = 0; i < getSites().size(); i++) {
            addCellForSite(i);
        }
    }

    public DelaunayContext getDual() {
        return dual;
    }

    public VoronoiSite[] getVertices() {
        return vertices;
    }

    public VoronoiCell[] getCells() {
        return cells;
    }

    public List<VoronoiSite> getSites() {
        return dual.getSites();
    }

}
