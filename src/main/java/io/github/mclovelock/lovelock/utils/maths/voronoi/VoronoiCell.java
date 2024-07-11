package io.github.mclovelock.lovelock.utils.maths.voronoi;

import java.util.LinkedList;
import java.util.List;

public class VoronoiCell {

    private final List<Integer> vertices = new LinkedList<>();
    private final List<Edge> edges = new LinkedList<>();

    private final VoronoiSite site;

    private int currentVertex;

    VoronoiCell(int startVertex, VoronoiSite site) {
        this.site = site;
        this.currentVertex = startVertex;
    }

    void edgeTo(int newVertex) {
        vertices.add(newVertex);
        edges.add(new Edge(currentVertex, newVertex));
        currentVertex = newVertex;
    }

    public VoronoiSite getSite() {
        return site;
    }

    public List<Integer> getVertices() {
        return vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }

}
