package io.github.mclovelock.lovelock.utils.maths.voronoi;

import io.github.mclovelock.lovelock.Lovelock;

import java.util.*;

public class VoronoiResult {

    public record Vertex(double x, double y) { }

    public record Line(double a, double b, double c) { }

    public record Edge(int lineIndex, int vertex1Index, int vertex2Index) { }

    public record Triangle(int a, int b, int c) { }

    private final List<Vertex> vertices = new LinkedList<>();
    private final List<Line> lines = new LinkedList<>();
    private final List<Edge> edges = new LinkedList<>();
    private final List<Triangle> triangles = new LinkedList<>();
    private final Map<Integer, List<Edge>> polygons = new HashMap<>();

    void outSite(VoronoiSite s) {
        Lovelock.LOGGER.debug(String.format("Site (%d) at (%f|%f)", s.siteNum(), s.siteX(), s.siteY()));
    }

    void outVertex(VoronoiSite s) {
        vertices.add(new Vertex(s.siteX(), s.siteY()));
        Lovelock.LOGGER.debug(String.format("Vertex(%d) at (%f|%f)", s.siteNum(), s.siteX(), s.siteY()));
    }

    void outTriple(VoronoiSite s1, VoronoiSite s2, VoronoiSite s3) {
        triangles.add(new Triangle(s1.siteNum(), s2.siteNum(), s3.siteNum()));
        Lovelock.LOGGER.debug(String.format("Circle through left=%d right=%d bottom=%d", s1.siteNum(), s2.siteNum(), s3.siteNum()));
    }

    void outBisector(io.github.mclovelock.lovelock.utils.maths.voronoi.Edge edge) {
        lines.add(new Line(edge.getA(), edge.getB(), edge.getC()));
        Lovelock.LOGGER.debug(String.format("Line (%d) %gx+%gy=%g, bisecting %d %d",
                edge.getEdgeNum(), edge.getA(), edge.getB(), edge.getC(),
                edge.getSite(io.github.mclovelock.lovelock.utils.maths.voronoi.Edge.LEFT).siteNum(),
                edge.getSite(io.github.mclovelock.lovelock.utils.maths.voronoi.Edge.RIGHT).siteNum()));
    }

    void outEdge(io.github.mclovelock.lovelock.utils.maths.voronoi.Edge edge) {
        int sitenumL = -1;
        if (edge.getLeftEndpoint() != null)
            sitenumL = edge.getLeftEndpoint().siteNum();
        int sitenumR = -1;
        if (edge.getRightEndpoint() != null)
            sitenumR = edge.getRightEndpoint().siteNum();
        if (!polygons.containsKey(edge.getLeft().siteNum()))
            polygons.put(edge.getLeft().siteNum(), new ArrayList<>());
        if (!polygons.containsKey(edge.getRight().siteNum()))
            polygons.put(edge.getRight().siteNum(), new ArrayList<>());
        polygons.get(edge.getLeft().siteNum()).add(new Edge(edge.getEdgeNum(), sitenumL, sitenumR));
        polygons.get(edge.getRight().siteNum()).add(new Edge(edge.getEdgeNum(), sitenumL, sitenumR));
        edges.add(new Edge(edge.getEdgeNum(), sitenumL, sitenumR));
        Lovelock.LOGGER.debug("e {} {} {}", edge.getEdgeNum(), sitenumL, sitenumR);
    }

    public List<Vertex> getVertices() {
        return Collections.unmodifiableList(vertices);
    }

    public List<Line> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public List<Edge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public List<Triangle> getTriangles() {
        return Collections.unmodifiableList(triangles);
    }

    public Map<Integer, List<Edge>> getPolygons() {
        return Collections.unmodifiableMap(polygons);
    }

}
