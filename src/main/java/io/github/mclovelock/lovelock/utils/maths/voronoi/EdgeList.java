package io.github.mclovelock.lovelock.utils.maths.voronoi;

class EdgeList {

    private final int hashsize;

    private double xmin;
    private double deltaX;
    private HalfEdge[] hash;

    private HalfEdge leftEnd, rightEnd;

    EdgeList(double xmin, double xmax, int nsites) {
        this.hashsize = (int)(2 * Math.sqrt(nsites + 4));

        this.xmin = xmin;
        this.deltaX = xmax - xmin;
        this.hash = new HalfEdge[this.hashsize];

        this.leftEnd = new HalfEdge();
        this.rightEnd = new HalfEdge();
        this.leftEnd.right = this.rightEnd;
        this.rightEnd.left = this.leftEnd;

        this.hash[0] = this.leftEnd;
        this.hash[1] = this.rightEnd;
    }

    void insert(HalfEdge left, HalfEdge he) {
        he.left = left;
        he.right = left.right;
        left.right.left = he;
        left.right = he;
    }

    void delete(HalfEdge he) {
        he.left.right = he.right;
        he.right.left = he.left;
        he.edge.delete();
    }

    HalfEdge getHash(int b) {
        if ((b < 0) || (b >= hashsize))
            return null;
        HalfEdge he = hash[b];
        if ((he == null) || (he.edge == null) || !he.edge.isDeleted())
            return he;

        hash[b] = null;
        return null;
    }

    HalfEdge leftbnd(VoronoiSite pt) {
        int bucket = (int)((pt.siteX() - xmin) / deltaX * hashsize);

        if (bucket < 0)
            bucket = 0;

        if (bucket >= hashsize)
            bucket = hashsize - 1;

        HalfEdge he = getHash(bucket);
        if (he == null) {
            for (int i = 1; true; i++) {
                he = getHash(bucket - i);
                if (he != null)
                    break;
                he = getHash(bucket + i);
                if (he != null)
                    break;
            }
        }

        if ((he == leftEnd) || ((he != rightEnd) && he.isPointRightOf(pt))) {
            do he = he.right;
            while ((he != rightEnd) && he.isPointRightOf(pt));
            he = he.left;
        } else {
            do he = he.left;
            while ((he != leftEnd) && !he.isPointRightOf(pt));
        }

        if ((bucket > 0) && (bucket < hashsize - 1))
            hash[bucket] = he;
        return he;
    }

    HalfEdge getLeftEnd() {
        return leftEnd;
    }

    HalfEdge getRightEnd() {
        return rightEnd;
    }

}
