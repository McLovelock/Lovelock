package io.github.mclovelock.lovelock.utils.maths.voronoi;

import io.github.mclovelock.lovelock.utils.maths.Maths;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DelaunayContext {

    private final List<VoronoiSite> sites;
    private final List<Triangle> triangulation;

    DelaunayContext(List<VoronoiSite> sites) {
        this.sites = sites;
        this.triangulation = new LinkedList<>();
    }

    private Triangle addExteriorPoints() {
        sites.add(new SiteImpl(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
        sites.add(new SiteImpl(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));
        sites.add(new SiteImpl(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));

        return new Triangle(sites.size() - 3, sites.size() - 2, sites.size() - 1);
    }

    private void removeTriangle(int index) {
        Triangle tri = triangulation.remove(index);
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

        var badTriangles = new ArrayList<Integer>();
        var polygon = new ArrayList<Edge>();
        for (int i = 0; i < sites.size(); i++) {
            VoronoiSite site = sites.get(i);

            badTriangles.clear();
            for (int j = 0; j < triangulation.size(); j++) {
                Circumcircle c = circumcircle(triangulation.get(j));
                if (Maths.distance(site.siteX(), site.siteY(), c.cx(), c.cy()) < c.r())
                    badTriangles.add(j);
            }
            polygon.clear();
            for (int j : badTriangles) {
                Triangle badTriangle = triangulation.get(j);
                Edge[] edges = badTriangle.getEdges();
                Triangle[] neighbours = badTriangle.getNeighbours();
                for (int k = 0; k < 3; k++) {
                    if (neighbours[k] == null)
                        polygon.add(edges[k]);
                }
            }
            for (int j : badTriangles) {
                removeTriangle(j);
            }
            for (Edge edge : polygon) {
                var newTri = new Triangle(edge.a(), edge.b(), i);
                triangulation.add(newTri);
            }
        }
        for (int i = 0; i < triangulation.size(); i++) {
            Triangle tri = triangulation.get(i);
            if (tri.getAIndex() >= sites.size() - 3 || tri.getBIndex() >= sites.size() - 3 || tri.getCIndex() >= sites.size() - 3)
                removeTriangle(i);
        }
    }

    public List<Triangle> getTriangulation() {
        return triangulation;
    }

    public List<VoronoiSite> getSites() {
        return sites;
    }

}
