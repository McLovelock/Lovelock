package io.github.mclovelock.lovelock.utils.maths.voronoi;

public class Triangle {

    private final int aIndex, bIndex, cIndex;

    private final Edge[] edges;
    private final Triangle[] neighbours;

    Triangle(int aIndex, int bIndex, int cIndex) {
        this.aIndex = aIndex;
        this.bIndex = bIndex;
        this.cIndex = cIndex;

        this.edges = new Edge[3];
        this.edges[0] = new Edge(bIndex, cIndex);
        this.edges[1] = new Edge(aIndex, cIndex);
        this.edges[2] = new Edge(aIndex, bIndex);

        this.neighbours = new Triangle[3];
    }

    void remove() {
        for (Triangle neighbour : neighbours) {
            for (int i = 0; (i < 3) && (neighbour != null); i++) {
                if (neighbour.neighbours[i] == this) {
                    neighbour.neighbours[i] = null;
                    break;
                }
            }
        }
    }

    boolean hasSite(int site) {
        return (aIndex == site) || (bIndex == site) || (cIndex == site);
    }

    Triangle neighbourWithSite(int site, Triangle previous) {
        for (Triangle neighbour : neighbours) {
            if (neighbour == previous) continue;
            if (neighbour.hasSite(site)) return neighbour;
        }
        return null;
    }

    public Edge edgeA() {
        return edges[0];
    }

    public Edge edgeB() {
        return edges[1];
    }

    public Edge edgeC() {
        return edges[2];
    }

    public Edge[] getEdges() {
        return edges;
    }

    public Triangle oppositeA() {
        return neighbours[0];
    }

    public Triangle oppositeB() {
        return neighbours[1];
    }

    public Triangle oppositeC() {
        return neighbours[2];
    }

    public Triangle[] getNeighbours() {
        return neighbours;
    }

    public int getAIndex() {
        return aIndex;
    }

    public int getBIndex() {
        return bIndex;
    }

    public int getCIndex() {
        return cIndex;
    }

}
