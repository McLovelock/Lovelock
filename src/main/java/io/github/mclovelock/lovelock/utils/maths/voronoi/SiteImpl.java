package io.github.mclovelock.lovelock.utils.maths.voronoi;

class SiteImpl implements VoronoiSite {

    private final double x;
    private final double y;

    private int siteNum;

    SiteImpl(double x, double y, int siteNum) {
        this.x = x;
        this.y = y;
        this.siteNum = siteNum;
    }

    SiteImpl(double x, double y) {
        this(x, y, 0);
    }

    @Override
    public double siteX() {
        return x;
    }

    @Override
    public double siteY() {
        return y;
    }

    @Override
    public int siteNum() {
        return siteNum;
    }

    @Override
    public void setSiteNum(int siteNum) {
        this.siteNum = siteNum;
    }

}
