package io.github.mclovelock.lovelock.utils.maths.voronoi;

import io.github.mclovelock.lovelock.utils.maths.Maths;
import org.jetbrains.annotations.NotNull;

class HalfEdge implements Comparable<HalfEdge> {

    HalfEdge left, right;
    HalfEdge qNext;
    Edge edge;
    VoronoiSite vertex;
    double ystar;
    int primarySite;

    HalfEdge() {
        this.left = this.right = null;
        this.qNext = null;
        this.edge = null;
        this.primarySite = Edge.LEFT;
        this.vertex = null;
        this.ystar = Double.POSITIVE_INFINITY;
    }

    HalfEdge(Edge edge, int primarySite) {
        this.left = this.right = null;
        this.qNext = null;
        this.edge = edge;
        this.primarySite = primarySite;
        this.vertex = null;
        this.ystar = Double.POSITIVE_INFINITY;
    }

    @Override
    public String toString() {
        return "HalfEdge--------------------\n" +
                "left: " + left.toString() +
                "right: " + right.toString() +
                "edge: " + edge.toString() +
                "vertex: " + ((vertex != null) ? ("(x: " + vertex.siteX() + ", y: " + vertex.siteY() + ")") : "null") +
                "ystar: " + ystar;
    }

    @Override
    public int compareTo(@NotNull HalfEdge o) {
        if (ystar > o.ystar)
            return 1;
        else if (ystar < o.ystar)
            return -1;
        else if (vertex.siteX() > o.vertex.siteX())
            return 1;
        else if (vertex.siteX() < o.vertex.siteX())
            return -1;
        else
            return 0;
    }

    VoronoiSite leftreg(VoronoiSite dflt) {
        if (edge == null)
            return dflt;
        else if (primarySite == Edge.LEFT)
            return edge.getSite(Edge.LEFT);
        else
            return edge.getSite(Edge.RIGHT);
    }

    VoronoiSite rightreg(VoronoiSite dflt) {
        if (edge == null)
            return dflt;
        else if (primarySite == Edge.LEFT)
            return edge.getSite(Edge.RIGHT);
        else
            return edge.getSite(Edge.LEFT);
    }

    boolean isPointRightOf(VoronoiSite pt) {
        Edge e = edge;
        VoronoiSite topsite = e.getSite(Edge.RIGHT);

        boolean rightOfSite = pt.siteX() > topsite.siteX();

        if (rightOfSite && (primarySite == Edge.LEFT))
            return true;

        if (!rightOfSite && (primarySite == Edge.RIGHT))
            return false;

        boolean above, fast;
        if (e.getA() == 1.0) {
            double dyp = pt.siteY() - topsite.siteY();
            double dxp = pt.siteX() - topsite.siteX();
            fast = false;
            if ((!rightOfSite && (e.getB() < 0.0)) || (rightOfSite && (e.getB() >= 0.0))) {
                above = dyp >= e.getB() * dxp;
                fast = above;
            } else {
                above = pt.siteX() + pt.siteY() * e.getB() > e.getC();
                if (e.getB() < 0.0)
                    above = !above;
                if (!above)
                    fast = true;
            }
            if (!fast) {
                double dxs = topsite.siteX() - e.getSite(Edge.LEFT).siteX();
                above = e.getB() * (dxp * dxp - dyp * dyp) < dxs * dyp * (1.0 + 2.0 * dxp / dxs + e.getB() * e.getB());
                if (e.getB() < 0.0)
                    above = !above;
            }
        } else {
            double yl = e.getC() - e.getA() * pt.siteX();
            double t1 = pt.siteY() - yl;
            double t2 = pt.siteX() - topsite.siteX();
            double t3 = yl - topsite.siteY();
            above = t1 * t1 > t2 * t2 + t3 * t3;
        }

        if (primarySite == Edge.LEFT)
            return above;
        else
            return !above;
    }

    VoronoiSite intersect(HalfEdge other) {
        Edge e1 = edge;
        Edge e2 = other.edge;

        if ((e1 == null) || (e2 == null))
            return null;

        if (e1.getSite(Edge.RIGHT) == e2.getSite(Edge.RIGHT))
            return null;

        double d = e1.getA() * e2.getB() - e1.getB() * e2.getA();
        if (Maths.isEqual(d, 0.0))
            return null;

        HalfEdge he;
        Edge e;
        double xint = (e1.getC() * e2.getB() - e2.getC() * e1.getB()) / d;
        double yint = (e2.getC() * e1.getA() - e1.getC() * e2.getA()) / d;
        if (e1.getSite(Edge.RIGHT).compareTo(e2.getSite(Edge.RIGHT)) < 0) {
            he = this;
            e = e1;
        } else {
            he = other;
            e = e2;
        }

        boolean rightOfSite = xint >= e.getSite(Edge.RIGHT).siteX();
        if ((rightOfSite && (he.primarySite == Edge.LEFT)) || (!rightOfSite && (he.primarySite == Edge.RIGHT)))
            return null;

        return new SiteImpl(xint, yint);
    }

}
