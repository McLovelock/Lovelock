package io.github.mclovelock.lovelock.utils.maths.voronoi;

import io.github.mclovelock.lovelock.utils.maths.Maths;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DelaunayContext {

    public static final double VERY_LARGE = 1000000000;
    public static final double VERY_SMALL = -1000000000;

    private final List<VoronoiSite> sites;
    private final List<Triangle> triangulation;

    DelaunayContext(List<VoronoiSite> sites) {
        this.sites = sites;
        this.triangulation = new LinkedList<>();
    }

    private Triangle addExteriorPoints() {
        sites.add(new SiteImpl(-1e9, -1e9));
        sites.add(new SiteImpl(-1e9,12e9));
        sites.add(new SiteImpl(12e9,1e9));

        return new Triangle(sites.size() - 3, sites.size() - 2, sites.size() - 1);
    }

    Circumcircle circumcircle(Triangle triangle) {
        VoronoiSite a = sites.get(triangle.a());
        VoronoiSite b = sites.get(triangle.b());
        VoronoiSite c = sites.get(triangle.c());

        double ax = a.siteX();
        double ay = a.siteY();

        double bx = b.siteX() - ax;
        double by = b.siteY() - ay;

        double cx = c.siteX() - ax;
        double cy = c.siteY() - ay;

        double d = 2 * (bx * cy - by * cx);

        double ux = (1.0 / d) * (cy * (bx * bx + by * by) - by * (cx * cx + cy * cy));
        double uy = (1.0 / d) * (bx * (cx * cx + cy * cy) - cx * (bx * bx + by * by));

        return new Circumcircle(ux + ax, uy + ay, Math.sqrt(ux * ux + uy * uy));
    }

    private Triangle makeSuperTriangle() {
        double minX, minY, maxX, maxY;
        minX = minY = Double.POSITIVE_INFINITY;
        maxX = maxY = Double.NEGATIVE_INFINITY;

        for (VoronoiSite site : sites) {
            minX = Math.min(minX, site.siteX());
            minY = Math.min(minY, site.siteY());
            maxX = Math.max(maxX, site.siteX());
            maxY = Math.max(maxY, site.siteY());
        }

        double dx = (maxX - minX) * 10;
        double dy = (maxY - minY) * 10;

        sites.addLast(new SiteImpl(minX - dx, minY - dy * 3));
        sites.addLast(new SiteImpl(minX - dx, maxY + dy));
        sites.addLast(new SiteImpl(maxX + dx * 3, maxY + dy));

        return new Triangle(sites.size() - 3, sites.size() - 2, sites.size() - 1);
    }

    private List<Edge> uniqueEdges(List<Edge> edges) {
        var uniqueEdges = new LinkedList<Edge>();
        for (int i = 0; i < edges.size(); i++) {
            boolean isUnique = true;

            for (int j = 0; j < edges.size(); j++) {
                if (i == j)
                    continue;
                if (edges.get(i).equals(edges.get(j))) {
                    isUnique = false;
                    break;
                }
            }

            if (isUnique)
                uniqueEdges.add(edges.get(i));
        }

        return uniqueEdges;
    }

    private void addTriangle(Edge edge, int centerPoint) {
        var tri = new Triangle(edge.a(), edge.b(), centerPoint);
        for (Triangle existing : triangulation) {
            if (existing.hasEdge(tri.edgeA())) {
                existing.setNeighbourAdjacent(tri.edgeA(), tri);
                tri.setNeighbourOpposite(tri.a(), existing);
            } else if (existing.hasEdge(tri.edgeB())) {
                existing.setNeighbourAdjacent(tri.edgeB(), tri);
                tri.setNeighbourOpposite(tri.b(), existing);
            } else if (existing.hasEdge(tri.edgeC())) {
                existing.setNeighbourAdjacent(tri.edgeC(), tri);
                tri.setNeighbourOpposite(tri.c(), existing);
            }
        }
        triangulation.add(tri);
    }

    private void removeTriangle(Triangle triangle) {
        if (triangle.getNeighbourOppositeA() != null)
            triangle.getNeighbourOppositeA().setNeighbourAdjacent(triangle.edgeA(), null);
        if (triangle.getNeighbourOppositeB() != null)
            triangle.getNeighbourOppositeB().setNeighbourAdjacent(triangle.edgeB(), null);
        if (triangle.getNeighbourOppositeC() != null)
            triangle.getNeighbourOppositeC().setNeighbourAdjacent(triangle.edgeC(), null);
        triangulation.remove(triangle);
    }

    private void addVertex(int index, List<Triangle> triangles) {
        VoronoiSite site = sites.get(index);

        var polygon = new LinkedList<Edge>();
        var badTriangles = new LinkedList<Triangle>();

        for (Triangle triangle : triangles) {
            Circumcircle circumcircle = circumcircle(triangle);
            if (Maths.distance(site.siteX(), site.siteY(), circumcircle.cx(), circumcircle.cy()) < circumcircle.r()) {
                polygon.add(triangle.edgeA());
                polygon.add(triangle.edgeB());
                polygon.add(triangle.edgeC());
                badTriangles.add(triangle);
            }
        }

        for (Triangle bad : badTriangles) {
            removeTriangle(bad);
        }

        List<Edge> edges = uniqueEdges(polygon);

        for (Edge edge : edges) {
            addTriangle(edge, index);
        }
    }

    void computeDelaunayTriangulation() {
        Triangle st = makeSuperTriangle();

        triangulation.clear();
        triangulation.add(st);

        for (int i = 0; i < sites.size(); i++) {
            addVertex(i, triangulation);
        }

        var badTriangles = new LinkedList<Triangle>();
        for (Triangle tri : triangulation) {
            if ((tri.a() == st.a()) || (tri.a() == st.b()) || (tri.a() == st.c()) ||
                    (tri.b() == st.a()) || (tri.b() == st.b()) || (tri.b() == st.c()) ||
                    (tri.c() == st.a()) || (tri.c() == st.b()) || (tri.c() == st.c()))
                badTriangles.add(tri);
        }
        for (Triangle bad : badTriangles) {
            removeTriangle(bad);
        }

        sites.removeLast();
        sites.removeLast();
        sites.removeLast();
    }

    public List<Triangle> getTriangulation() {
        return triangulation;
    }

    public List<VoronoiSite> getSites() {
        return sites;
    }

}
