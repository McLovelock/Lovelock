package io.github.mclovelock.lovelock.utils.maths.voronoi;

import java.util.LinkedList;
import java.util.List;

public class VoronoiCell {

    private final List<Edge> edges = new LinkedList<>();

    private final VoronoiSite site;

    VoronoiCell(VoronoiSite site) {
        this.site = site;
    }

    void addEdge(Edge edge) {
        edges.add(edge);
    }

    public VoronoiSite getSite() {
        return site;
    }

    public List<Edge> getEdges() {
        return edges;
    }

}
