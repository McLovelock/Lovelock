package io.github.mclovelock.lovelock.utils.maths.voronoi;

public class Triangle {

    private final Triangle[] neighbours = new Triangle[3];

    private final int a, b, c;

    public Triangle(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Edge edgeA() {
        return new Edge(b, c);
    }

    public Edge edgeB() {
        return new Edge(a, c);
    }

    public Edge edgeC() {
        return new Edge(a, b);
    }

    public Edge edgeOpposite(int site) {
        if (site == a)
            return edgeA();
        else if (site == b)
            return edgeB();
        else if (site == c)
            return edgeC();
        else
            return null;
    }

    public Triangle getNeighbourOppositeA() {
        return neighbours[0];
    }

    public Triangle getNeighbourOppositeB() {
        return neighbours[1];
    }

    public Triangle getNeighbourOppositeC() {
        return neighbours[2];
    }

    public void setNeighbourOppositeA(Triangle neighbour) {
        this.neighbours[0] = neighbour;
    }

    public void setNeighbourOppositeB(Triangle neighbour) {
        this.neighbours[1] = neighbour;
    }

    public void setNeighbourOppositeC(Triangle neighbour) {
        this.neighbours[2] = neighbour;
    }

    public Triangle getNeighbourOpposite(int point) {
        if (point == a)
            return getNeighbourOppositeA();
        else if (point == b)
            return getNeighbourOppositeB();
        else if (point == c)
            return getNeighbourOppositeC();
        else
            return null;
    }

    public void setNeighbourOpposite(int point, Triangle newNeighbour) {
        if (point == a)
            setNeighbourOppositeA(newNeighbour);
        else if (point == b)
            setNeighbourOppositeB(newNeighbour);
        else if (point == c)
            setNeighbourOppositeC(newNeighbour);
    }

    public Triangle getNeighbourAdjacent(Edge dividingEdge) {
        if (((dividingEdge.a() == a) && (dividingEdge.b() == b)) || ((dividingEdge.a() == b) && (dividingEdge.b() == a)))
            return getNeighbourOppositeC();
        else if (((dividingEdge.a() == b) && (dividingEdge.b() == c)) || ((dividingEdge.a() == c) && (dividingEdge.b() == b)))
            return getNeighbourOppositeA();
        else if (((dividingEdge.a() == a) && (dividingEdge.b() == c)) || ((dividingEdge.a() == c) && (dividingEdge.b() == a)))
            return getNeighbourOppositeB();
        else
            return null;
    }

    public void setNeighbourAdjacent(Edge dividingEdge, Triangle newNeighbour) {
        if (((dividingEdge.a() == a) && (dividingEdge.b() == b)) || ((dividingEdge.a() == b) && (dividingEdge.b() == a)))
            setNeighbourOppositeC(newNeighbour);
        else if (((dividingEdge.a() == b) && (dividingEdge.b() == c)) || ((dividingEdge.a() == c) && (dividingEdge.b() == b)))
            setNeighbourOppositeA(newNeighbour);
        else if (((dividingEdge.a() == a) && (dividingEdge.b() == c)) || ((dividingEdge.a() == c) && (dividingEdge.b() == a)))
            setNeighbourOppositeB(newNeighbour);
    }

    public boolean hasSite(int site) {
        return (a == site) || (b == site) || (c == site);
    }

    public boolean hasEdge(Edge edge) {
        Edge a = edgeA();
        if (((edge.a() == a.a()) && (edge.b() == a.b())) || ((edge.a() == a.b()) && (edge.b() == a.a())))
            return true;

        Edge b = edgeB();
        if (((edge.a() == b.a()) && (edge.b() == b.b())) || ((edge.a() == b.b()) && (edge.b() == b.a())))
            return true;

        Edge c = edgeC();
        if (((edge.a() == c.a()) && (edge.b() == c.b())) || ((edge.a() == c.b()) && (edge.b() == c.a())))
            return true;

        return false;
    }

    public Triangle[] getNeighbours() {
        return neighbours;
    }

    public int a() {
        return a;
    }

    public int b() {
        return b;
    }

    public int c() {
        return c;
    }

}
