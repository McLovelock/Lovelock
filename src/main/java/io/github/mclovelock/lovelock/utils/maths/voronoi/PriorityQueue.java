package io.github.mclovelock.lovelock.utils.maths.voronoi;

import java.util.LinkedList;
import java.util.List;

class PriorityQueue {

    private final List<HalfEdge> hash = new LinkedList<>();

    private double ymin;
    private double deltaY;
    private int hashsize;
    private int count;
    private int minIdx;

    PriorityQueue(double ymin, double ymax, int nsites){
        this.ymin = ymin;
        this.deltaY = ymax - ymin;
        this.hashsize = (int)(4 * Math.sqrt(nsites));
        this.count = 0;
        this.minIdx = 0;

        for (int i = 0; i < hashsize; i++) {
            hash.add(new HalfEdge());
        }
    }

    int size() {
        return count;
    }

    boolean isEmpty() {
        return count == 0;
    }

    void insert(HalfEdge he, VoronoiSite site, double offset) {
        he.vertex = site;
        he.ystar = site.siteY() + offset;
        HalfEdge last = hash.get(getBucket(he));
        HalfEdge next = last.qNext;
        while ((next != null) && (he.compareTo(next) > 0)) {
            last = next;
            next = last.qNext;
        }
        he.qNext = last.qNext;
        last.qNext = he;
        count++;
    }

    void delete(HalfEdge he) {
        if (he.vertex != null) {
            HalfEdge last = hash.get(getBucket(he));
            while (last.qNext != he)
                last = last.qNext;
            last.qNext = he.qNext;
            count--;
            he.vertex = null;
        }
    }

    int getBucket(HalfEdge he) {
        int bucket = (int)(((he.ystar - ymin) / deltaY) * hashsize);
        if (bucket < 0)
            bucket = 0;
        if (bucket >= hashsize)
            bucket = hashsize - 1;
        if (bucket < minIdx)
            minIdx = bucket;
        return bucket;
    }

    VoronoiSite getMinPt() {
        while (hash.get(minIdx).qNext == null) {
            minIdx++;
        }
        HalfEdge he = hash.get(minIdx).qNext;
        double x = he.vertex.siteX();
        double y = he.ystar;
        return new SiteImpl(x, y);
    }

    HalfEdge popMinHalfEdge() {
        HalfEdge curr = hash.get(minIdx).qNext;
        hash.get(minIdx).qNext = curr.qNext;
        count--;
        return curr;
    }

}
