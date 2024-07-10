package io.github.mclovelock.lovelock.utils.maths.voronoi;

class Edge {

    static final int LEFT = 0;
    static final int RIGHT = 1;

    static int nEdges = 0;

    private double a, b, c;
    private int edgeNum;

    private VoronoiSite leftEndpoint, rightEndpoint;
    private VoronoiSite left, right;

    private boolean deleted = false;

    Edge() {
        this.a = this.b = this.c = 0.0;
        this.leftEndpoint = null;
        this.rightEndpoint = null;
        this.left = null;
        this.right = null;

        this.edgeNum = 0;
    }

    @Override
    public String toString() {
        return String.format("#%d a=%g b=%g c=%g)%n", edgeNum, a, b, c);
    }

    boolean setEndpoint(int lrFlag, VoronoiSite site) {
        if (lrFlag == RIGHT) {
            rightEndpoint = site;
            return leftEndpoint != null;
        }
        else {
            leftEndpoint = site;
            return rightEndpoint != null;
        }
    }

    VoronoiSite getSite(int side) {
        if (side == LEFT)
            return left;
        else
            return right;
    }

    VoronoiSite getLeft() {
        return left;
    }

    VoronoiSite getRight() {
        return right;
    }

    VoronoiSite getLeftEndpoint() {
        return leftEndpoint;
    }

    VoronoiSite getRightEndpoint() {
        return rightEndpoint;
    }

    double getA() {
        return a;
    }

    double getB() {
        return b;
    }

    double getC() {
        return c;
    }

    boolean isDeleted() {
        return deleted;
    }

    void delete() {
        deleted = true;
    }

    int getEdgeNum() {
        return edgeNum;
    }

    public static Edge bisect(VoronoiSite s1, VoronoiSite s2) {
        var newEdge = new Edge();
        newEdge.left = s1;
        newEdge.right = s2;

        double dx = s2.siteX() - s1.siteX();
        double dy = s2.siteY() - s1.siteY();
        double adx = Math.abs(dx);
        double ady = Math.abs(dy);

        newEdge.c = s1.siteX() * dx + s1.siteY() * dy + (dx * dx + dy * dy) * 0.5;
        if (adx > ady) {
            newEdge.a = 1.0;
            newEdge.b = dy / dx;
            newEdge.c /= dx;
        } else {
            newEdge.b = 1.0;
            newEdge.a = dx / dy;
            newEdge.c /= dy;
        }

        newEdge.edgeNum = nEdges;
        nEdges++;
        return newEdge;
    }

}
