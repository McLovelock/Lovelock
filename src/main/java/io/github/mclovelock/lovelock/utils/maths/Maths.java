package io.github.mclovelock.lovelock.utils.maths;

public final class Maths {

    public static double angleOnUnitCircle(double x, double y) {
        if (y >= 0) {
            return Math.cos(x);
        } else {
            return Math.cos(x) + Math.PI;
        }
    }

}
