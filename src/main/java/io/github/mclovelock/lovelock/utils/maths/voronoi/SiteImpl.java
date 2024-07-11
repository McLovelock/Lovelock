package io.github.mclovelock.lovelock.utils.maths.voronoi;

class SiteImpl implements VoronoiSite {

    private final double siteX, siteY;

    SiteImpl(double siteX, double siteY) {
        this.siteX = siteX;
        this.siteY = siteY;
    }

    @Override
    public double siteX() {
        return siteX;
    }

    @Override
    public double siteY() {
        return siteY;
    }

}
