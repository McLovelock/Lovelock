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
        sites.add(new SiteImpl(VERY_SMALL, VERY_SMALL));
        sites.add(new SiteImpl(VERY_SMALL, VERY_LARGE));
        sites.add(new SiteImpl(VERY_LARGE, VERY_LARGE));

        return new Triangle(sites.size() - 3, sites.size() - 2, sites.size() - 1);
    }

    private void removeTriangle(Triangle tri) {
        triangulation.remove(tri);
        tri.remove();
    }

    Circumcircle circumcircle(Triangle triangle) {
        VoronoiSite a = sites.get(triangle.getAIndex());
        VoronoiSite b = sites.get(triangle.getBIndex());
        VoronoiSite c = sites.get(triangle.getCIndex());

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

    void computeDelaunayTriangulation() {
        triangulation.add(addExteriorPoints());

        var badTriangles = new ArrayList<Triangle>();
        var polygon = new ArrayList<Edge>();
        for (int i = 0; i < sites.size(); i++) {
            VoronoiSite site = sites.get(i);

            badTriangles.clear();
            for (Triangle tri : triangulation) {
                Circumcircle c = circumcircle(tri);
                if (Maths.distance(site.siteX(), site.siteY(), c.cx(), c.cy()) < c.r())
                    badTriangles.add(tri);
            }
            polygon.clear();
            for (Triangle badTriangle : badTriangles) {
                Edge[] edges = badTriangle.getEdges();
                Triangle[] neighbours = badTriangle.getNeighbours();
                for (int k = 0; k < 3; k++) {
                    if (neighbours[k] == null)
                        polygon.add(edges[k]);
                }
            }
            for (Triangle bad : badTriangles) {
                removeTriangle(bad);
            }
            for (Edge edge : polygon) {
                var newTri = new Triangle(edge.a(), edge.b(), i);
                triangulation.add(newTri);
            }
        }

        var toRemove = new LinkedList<Triangle>();
        for (Triangle tri : triangulation) {
            if (tri.getAIndex() >= sites.size() - 3 || tri.getBIndex() >= sites.size() - 3 || tri.getCIndex() >= sites.size() - 3)
                toRemove.add(tri);
        }
        for (Triangle tri : toRemove) {
            removeTriangle(tri);
        }

        // Remove helper vertices
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
