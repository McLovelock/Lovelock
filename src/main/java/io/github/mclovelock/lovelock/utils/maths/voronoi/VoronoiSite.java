package io.github.mclovelock.lovelock.utils.maths.voronoi;

import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

public interface VoronoiSite extends Comparable<VoronoiSite> {

    double siteX();

    double siteY();

    @Override
    default int compareTo(@NotNull VoronoiSite other) {
        if (siteY() < other.siteY())
            return -1;
        else if (siteY() > other.siteY())
            return +1;
        else if (siteX() < other.siteX())
            return -1;
        else if (siteX() > other.siteX())
            return +1;
        else return 0;
    }

    default double distance(@NotNull VoronoiSite other) {
        double dx = siteX() - other.siteX();
        double dy = siteY() - other.siteY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    default int siteNum() {
        return 0;
    }

    default void setSiteNum(int siteNum) {
        throw new NotImplementedException("Cannot set SiteNum of user-specified Site.");
    }

}
