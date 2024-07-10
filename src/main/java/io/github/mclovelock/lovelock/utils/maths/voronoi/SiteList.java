package io.github.mclovelock.lovelock.utils.maths.voronoi;

import org.jetbrains.annotations.NotNull;

import java.util.*;

class SiteList implements  Iterable<VoronoiSite> {

    private final List<VoronoiSite> sites = new ArrayList<>();

    private int siteNum;

    private double xmin, ymin;
    private double xmax, ymax;

    SiteList(List<VoronoiSite> pointList) {
        this.siteNum = 0;

        this.xmin = pointList.getFirst().siteX();
        this.ymin = pointList.getFirst().siteY();
        this.xmax = pointList.getFirst().siteX();
        this.ymax = pointList.getFirst().siteY();
        for (int i = 0; i < pointList.size(); i++) {
            VoronoiSite pt = pointList.get(i);
            this.sites.add(new SiteImpl(pt.siteX(), pt.siteY(), i));
            if (pt.siteX() < this.xmin)
                this.xmin = pt.siteX();
            if (pt.siteY() < this.ymin)
                this.ymin = pt.siteY();
            if (pt.siteX() > this.xmax)
                this.xmax = pt.siteX();
            if (pt.siteY() > this.ymax)
                this.ymax = pt.siteY();
        }
        sites.sort(VoronoiSite::compareTo);
    }

    void setSiteNumber(VoronoiSite site) {
        site.setSiteNum(siteNum++);
    }

    private static class SiteIterator implements Iterator<VoronoiSite> {
        private final Iterator<VoronoiSite> generator;

        SiteIterator(List<VoronoiSite> lst)  {
            this.generator = Collections.unmodifiableList(lst).iterator();
        }

        @Override
        public boolean hasNext() {
            return generator.hasNext();
        }

        @Override
        public VoronoiSite next() {
            if (hasNext())
                return generator.next();
            else
                return null;
        }
    }

    @NotNull
    @Override
    public Iterator<VoronoiSite> iterator() {
        return new SiteIterator(sites);
    }

    int size() {
        return sites.size();
    }

    public double getXmin() {
        return xmin;
    }

    public double getXmax() {
        return xmax;
    }

    public double getYmin() {
        return ymin;
    }

    public double getYmax() {
        return ymax;
    }

}
